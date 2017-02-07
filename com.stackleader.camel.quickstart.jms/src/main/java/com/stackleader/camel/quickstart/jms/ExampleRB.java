/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stackleader.camel.quickstart.jms;

import org.apache.camel.builder.RouteBuilder;
import org.osgi.service.component.annotations.Component;

/**
 *
 * @author jeckste
 */
@Component(service = org.apache.camel.RoutesBuilder.class)
public class ExampleRB extends RouteBuilder {
    
    private String MESSAGE_ENTRY = "direct:entry";

    @Override
    public void configure() throws Exception {
        from("custom-amq:queue:source1").routeId(ExampleRB.class+"-source1")
                .to(MESSAGE_ENTRY);
        
        from("custom-amq:queue:source2")
                .to(MESSAGE_ENTRY);
        
        from("custom-amq:queue:source3")
                .to(MESSAGE_ENTRY);
        
        from(MESSAGE_ENTRY)
                .log("${body}")
                //Validate
                //Aggregate
                .aggregate(new SomeAggregationStrategy()).constant(true)
                .completionSize(5)
                .completionTimeout(5000L)
                .log("${body.size}")
                //Throttle
                ;
    }
    
}
