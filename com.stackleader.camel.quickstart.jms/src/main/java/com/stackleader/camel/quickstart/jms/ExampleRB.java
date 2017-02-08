/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stackleader.camel.quickstart.jms;

import org.apache.camel.builder.RouteBuilder;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 *
 * @author jeckste
 */
@Component(service = org.apache.camel.RoutesBuilder.class)
public class ExampleRB extends RouteBuilder {
    
    private String MESSAGE_ENTRY = "direct:entry";
    
    private SomeSink someSink;
    private Validator validator;

    @Override
    public void configure() throws Exception {
        from("custom-amq:queue:source1").routeId(ExampleRB.class.getCanonicalName()+"-source1")
                .to(MESSAGE_ENTRY);
        
        from("custom-amq:queue:source2").routeId(ExampleRB.class.getCanonicalName()+"-source2")
                .to(MESSAGE_ENTRY);
        
        from("custom-amq:queue:source3").routeId(ExampleRB.class.getCanonicalName()+"-source3")
                .to(MESSAGE_ENTRY);
        
        from(MESSAGE_ENTRY).routeId(ExampleRB.class.getCanonicalName()+"-main")
                .log("${body}")
                //Validate
                .bean(validator).id("validator")
                //Aggregate
                .aggregate(new SomeAggregationStrategy()).constant(true)
                .completionSize(2)
                .completionTimeout(1000L)
                .throttle(constant(3)).timePeriodMillis(5000L)
                .log("throttled: ${body.size}")
                .bean(someSink).id("somesink")
                ;
    }
    
    
   

    @Reference
    public void setSomeSink(SomeSink someSink) {
        this.someSink = someSink;
    }

    @Reference
    public void setValidator(Validator validator) {
        this.validator = validator;
    }
    
    
    
    
}
