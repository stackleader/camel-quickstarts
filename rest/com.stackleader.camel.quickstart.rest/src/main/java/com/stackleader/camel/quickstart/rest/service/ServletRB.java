/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stackleader.camel.quickstart.rest.service;

import com.stackleader.camel.quickstart.rest.model.Order;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 *
 * @author jeckste
 */
@Component(service = org.apache.camel.RoutesBuilder.class)
public class ServletRB extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        restConfiguration().component("servlet").bindingMode(RestBindingMode.json);
        rest("/ping")
                .get()
                .route()
                .setBody(constant("foo"));
    }
    
    @Reference
    public void waitForServletRegister(ServletRegisterer servletRegister) {
        
    }
    
}
