/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stackleader.camel.quickstart.jms;

import java.util.Map;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

/**
 *
 * @author jeckste
 */
@Component(service = org.apache.camel.RoutesBuilder.class, configurationPolicy = ConfigurationPolicy.REQUIRE)
public class JmsRB extends RouteBuilder {
    
    private Map<String, String> properties;
    private String queueName;
    
    @Activate
    public void activate(Map<String, String> properties) {
        this.properties = properties;
        this.queueName = properties.get("queue.name");
    }

    @Override
    public void configure() throws Exception {
        from("custom-amq:queue:"+queueName).routeId(JmsRB.class.getCanonicalName() + "-foo")
                .onException(Exception.class).to("direct:errorHandler").end()
                .log(LoggingLevel.DEBUG, "${body}")
                .process(ex -> {
                    throw new RuntimeException("it failed");
                })
                .process(ex -> {
                    ex.getIn().getBody();
                });
        
        from("direct:errorHandler")
                .process(ex -> {
                    ex.getIn().getBody();
                });
    }

}
