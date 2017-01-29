/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stackleader.camel.quickstart.netty;

import org.apache.camel.CamelContext;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.core.osgi.OsgiDefaultCamelContext;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jeckstei
 */
@Component
public class NettyRB extends RouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(NettyRB.class);

    private CamelContext context;

    @Activate
    public void activate(BundleContext bundleContext) throws Exception {
        context = new OsgiDefaultCamelContext(bundleContext);
        context.addRoutes(this);
        context.start();
    }

    @Deactivate
    public void deactivate() throws Exception {
        context.stop();
    }

    @Override
    public void configure() throws Exception {

        from("netty4:tcp://0.0.0.0:8181?textline=true&sync=true")
                .log(LoggingLevel.INFO, "before ${body}")
                .to("bean:" + WordReverseService.class.getCanonicalName() + "?method=reverseWords")
                .log(LoggingLevel.INFO, "after ${body}");
    }

    @Reference(target = "(component=netty4)")
    public void waitForCamelComponentNetty4(org.apache.camel.spi.ComponentResolver cr) {

    }
}
