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
public class JmsRB extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("custom-amq:queue:foo")
                .log("${body}");
    }
    
}
