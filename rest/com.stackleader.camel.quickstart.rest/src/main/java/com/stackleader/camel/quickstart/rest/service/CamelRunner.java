/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stackleader.camel.quickstart.rest.service;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.logging.Level;
import org.apache.camel.CamelContext;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.core.osgi.OsgiDefaultCamelContext;
import org.apache.camel.spi.ComponentResolver;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jeckste
 */
@Component(immediate = true)
public class CamelRunner {

    private static final Logger LOG = LoggerFactory.getLogger(CamelRunner.class);

    private CamelContext context;
    
    private List<RoutesBuilder> routesBuilders;

    public CamelRunner() {
        routesBuilders = Lists.newArrayList();
    }
    
    
    

    @Activate
    public void activate(BundleContext bundleContext) throws Exception {
        context = new OsgiDefaultCamelContext(bundleContext);
        routesBuilders.forEach(rb -> {
            try {
                context.addRoutes(rb);
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }

        });

        context.start();
    }

    @Deactivate
    public void deactivate() throws Exception {
        context.stop();
    }

    @Reference(name = "camelComponent", service = ComponentResolver.class,
            cardinality = ReferenceCardinality.AT_LEAST_ONE, policy = ReferencePolicy.DYNAMIC,
            policyOption = ReferencePolicyOption.GREEDY, unbind = "lostCamelComponent")
    public void gotCamelComponent(ServiceReference serviceReference) {
        LOG.info("got comp: {}", serviceReference.getBundle().getSymbolicName());
    }

    @Reference
    public void getRouteBuilder(ServiceRB serviceRB) {
        routesBuilders.add(serviceRB);
    }

    public void lostCamelComponent(ServiceReference serviceReference) {
    }

}
