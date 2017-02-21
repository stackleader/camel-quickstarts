/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stackleader.camel.quickstart.jms;

import org.apache.camel.ExchangePattern;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.spring.spi.SpringTransactionPolicy;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author jeckste
 */
public class TransactionTest extends CamelTestSupport {

    @Produce
    private ProducerTemplate producer;

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();
        TransactedAmqCamelComponent amq = new TransactedAmqCamelComponent();
        registry.bind("transactionManager", amq.getTransactionManager());
        registry.bind("PROPAGATION_REQUIRED", new SpringTransactionPolicy(
                new TransactionTemplate(amq.getTransactionManager(),
                        new DefaultTransactionDefinition(
                                TransactionDefinition.PROPAGATION_REQUIRED))));
        registry.bind("tx-amq", amq.resolveComponent("", context));
        return registry;
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("tx-amq:queue:source1")
                        //.setExchangePattern(ExchangePattern.InOnly)
                        .to("direct:source");
                
                from("tx-amq:queue:source2")
                        //.setExchangePattern(ExchangePattern.InOnly)
                        .to("direct:source");

                from("direct:source")
                        .transacted()
                        .log(LoggingLevel.INFO, "Jms Body: ${body}")
                        //Transaction supported in aggregation
                        .aggregate(new ExceptionThrowingAggregationStrategy()).constant(true)
                        .completionSize(10)
                        .completionTimeout(2000L)
                        //Output exchange is no longer in transaction
                        .to("direct:processAgg");
                        
                        
                from("direct:processAgg")
                        .errorHandler(deadLetterChannel("tx-amq:queue:ActiveMQ.DLQ"))
                        .log(LoggingLevel.INFO, "Aggregated body size: ${body.size}")
                        .throwException(new RuntimeException("Explosion"))
                        .process(ex -> {
                            //Send to some endpoint
                        });
            }
        };
    }

   
    @Test
    //@Ignore
    public void addToQueue() throws Exception {
        //First 10 messages will fail in aggregator
        //Remaining 5 will be aggregated but will fail after agg.
        for (int i = 0; i < 10; i++) {
            producer.sendBody("tx-amq:queue:source1", ExchangePattern.InOnly, "foo1");
        }
        for (int i = 0; i < 5; i++) {
            producer.sendBody("tx-amq:queue:source2", ExchangePattern.InOnly, "foo1");
        }

        Thread.sleep(10000);
    }
}
