/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stackleader.camel.quickstart.jms;

import org.apache.camel.ExchangePattern;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 *
 * @author jeckste
 */
public class AmqTest extends CamelTestSupport {

    @Produce
    private ProducerTemplate producer;

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();
        AmqCamelComponent amq = new AmqCamelComponent();
        registry.bind("custom-amq", amq.resolveComponent("", context));
        return registry;
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:q1")
                        .setExchangePattern(ExchangePattern.InOnly)
                        .to("custom-amq:queue:source1");

                from("direct:q2")
                        .setExchangePattern(ExchangePattern.InOnly)
                        .to("custom-amq:queue:source2");

                from("direct:q3")
                        .setExchangePattern(ExchangePattern.InOnly)
                        .to("custom-amq:queue:source3");
            }
        };
    }

    //@Test
    public void testBlast() {
        for (int i = 0; i < 10; i++) {
            producer.sendBody("direct:q1", "foo1");
            producer.sendBody("direct:q2", "foo2");
            producer.sendBody("direct:q3", "foo3");
        }
    }

}
