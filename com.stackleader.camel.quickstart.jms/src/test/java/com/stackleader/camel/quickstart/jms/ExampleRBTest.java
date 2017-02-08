/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stackleader.camel.quickstart.jms;

import java.util.concurrent.TimeUnit;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jeckste
 */
public class ExampleRBTest extends CamelTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(ExampleRBTest.class);

    @Produce
    protected ProducerTemplate producer;

    @Override
    public boolean isUseAdviceWith() {
        return true;
    }

    @Override
    public boolean isUseDebugger() {
        return true;
    }

    @Override
    protected void debugBefore(Exchange exchange, Processor processor,
            ProcessorDefinition<?> definition, String id, String shortName) {
        //Set break points here to debug before each step
        LOG.info("Definition: {}, Id: {}, ShortName: {}", definition, id, shortName);
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        ExampleRB exampleRB = new ExampleRB();
        exampleRB.setValidator(new Validator());
        return exampleRB;
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();
        return registry;
    }
    
    @Test
    public void testMockEndpoint() throws Exception {
        context.getRouteDefinition(ExampleRB.class.getCanonicalName()+"-source1").adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith("direct:source1");
            }
        });
        context.getRouteDefinition(ExampleRB.class.getCanonicalName()+"-source2").adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith("direct:source2");
            }
        });
        context.getRouteDefinition(ExampleRB.class.getCanonicalName()+"-source3").adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith("direct:source3");
            }
        });
        
        context.getRouteDefinition(ExampleRB.class.getCanonicalName()+"-main").adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                weaveAddFirst()
                        .to("mock:start");
                
                weaveById("somesink").replace()
                        .to("mock:end");
            }
        });
        context.start();
        MockEndpoint mockStart = getMockEndpoint("mock:start");
        MockEndpoint mockEnd = getMockEndpoint("mock:end");
        mockStart.expectedMessageCount(3);
        mockEnd.expectedMessageCount(2);
        producer.sendBody("direct:source1", ExchangePattern.InOnly, "bar");
        producer.sendBody("direct:source2", ExchangePattern.InOnly, "bar");
        producer.sendBody("direct:source3", ExchangePattern.InOnly, "bar");
        assertMockEndpointsSatisfied(10, TimeUnit.SECONDS);
    }
    
}
