/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stackleader.camel.quickstart.rest.service;


import com.stackleader.camel.quickstart.rest.model.Order;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jeckstei
 */
@Component(service = org.apache.camel.RoutesBuilder.class)
public class JettyRB extends RouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(JettyRB.class);
    private static final String ORDERS_ENDPOINT = "direct:orders";
    
    private OrderProcessor orderProcessor;

    @Override
    public void configure() throws Exception {

        restConfiguration().component("jetty").host("0.0.0.0").port("8182").bindingMode(RestBindingMode.json);
        rest("/orders").post().type(Order.class).to(ORDERS_ENDPOINT);

        from(ORDERS_ENDPOINT)
                .log(LoggingLevel.INFO, "Processing order")
                .bean(orderProcessor, "processOrder");
    }

    @Reference(target = "(component=jetty)")
    public void waitForCamelJetty9(org.apache.camel.spi.ComponentResolver cr) {

    }

    @Reference(target = "(dataformat=json-jackson)")
    public void waitForJsonDataFormat(org.apache.camel.spi.DataFormatResolver dataformat) {

    }

    @Reference
    public void waitForOrderProcessor(OrderProcessor orderProcessor) {
        this.orderProcessor = orderProcessor;
    }
}
