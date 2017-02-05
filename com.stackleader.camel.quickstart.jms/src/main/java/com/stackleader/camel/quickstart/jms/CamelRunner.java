/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stackleader.camel.quickstart.jms;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
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

    private ReentrantLock lock;
    
    public CamelRunner() {
        routesBuilders = Collections.synchronizedList(Lists.newArrayList());
        lock = new ReentrantLock();
    }

    @Activate
    public void activate(BundleContext bundleContext) throws Exception {
        try {
            lock.lock();
            context = new OsgiDefaultCamelContext(bundleContext);
            context.start();
            synchronized (routesBuilders) {
                routesBuilders.forEach(rb -> {
                    try {
                        context.addRoutes(rb);
                    } catch (Exception ex) {
                        LOG.error(ex.getMessage(), ex);
                    }

                });
            }
        } finally {
            lock.unlock();
        }

    }

    @Deactivate
    public void deactivate() throws Exception {
        context.stop();
    }

    @Reference(name = "camelComponent", service = ComponentResolver.class,
            cardinality = ReferenceCardinality.AT_LEAST_ONE, policy = ReferencePolicy.DYNAMIC,
            policyOption = ReferencePolicyOption.GREEDY, unbind = "unbindCamelComponent")
    public void gotCamelComponent(ServiceReference serviceReference) {
        //TODO: handle this if after context start
        LOG.info("got comp: {}", serviceReference.getBundle().getSymbolicName());
    }

    @Reference(service = RoutesBuilder.class, 
            cardinality = ReferenceCardinality.AT_LEAST_ONE, policy = ReferencePolicy.DYNAMIC,
            policyOption = ReferencePolicyOption.GREEDY, unbind = "unbindRouteBuilder")
    public void getRouteBuilder(org.apache.camel.RoutesBuilder routesBuilder) {
        if (context != null) {
            try {
                lock.lock();
                context.addRoutes(routesBuilder);
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
            } finally {
                lock.unlock();
            }
        }
        routesBuilders.add(routesBuilder);
    }
    
    //TODO: handle this
    public void unbindRouteBuilder(RoutesBuilder routesBuilder) {
        
    }

    public void unbindCamelComponent(ServiceReference serviceReference) {
    }

}
