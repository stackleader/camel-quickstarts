/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stackleader.camel.quickstart.jms;

import javax.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsConfiguration;
import org.apache.camel.spi.ComponentResolver;
import org.osgi.service.component.annotations.Component;

/**
 *
 * @author jeckste
 */
@Component(immediate = true, service = ComponentResolver.class, property = "component=custom-amq")
public class AmqCamelComponent implements ComponentResolver {

    @Override
    public org.apache.camel.Component resolveComponent(String name, CamelContext arg1) throws Exception {
        ConnectionFactory cf = new ActiveMQConnectionFactory();
        ((ActiveMQConnectionFactory) cf).setBrokerURL("failover://tcp://localhost:61616");
        ((ActiveMQConnectionFactory) cf).setUserName("admin");
        ((ActiveMQConnectionFactory) cf).setPassword("admin");

        JmsConfiguration jmsConfiguration = new JmsConfiguration();
        jmsConfiguration.setConnectionFactory(cf);
        jmsConfiguration.setConcurrentConsumers(10);
        ActiveMQComponent activeMQComponent = new ActiveMQComponent(arg1);
        activeMQComponent.setConfiguration(jmsConfiguration);

        return activeMQComponent;
    }

}
