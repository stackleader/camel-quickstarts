/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stackleader.camel.quickstart.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsConfiguration;
import org.apache.camel.spi.ComponentResolver;
import org.osgi.service.component.annotations.Component;
import org.springframework.jms.connection.JmsTransactionManager;

/**
 *
 * @author jeckste
 */
@Component(immediate = true, service = ComponentResolver.class, property = "component=tx-amq")
public class TransactedAmqCamelComponent implements ComponentResolver {
    
    private JmsTransactionManager txm;
    private ActiveMQConnectionFactory cf;
    
    public TransactedAmqCamelComponent() {
        
        cf = new ActiveMQConnectionFactory();
        cf.setBrokerURL("failover://tcp://localhost:61616");
        cf.setUserName("admin");
        cf.setPassword("admin");
        
        txm = new JmsTransactionManager(cf);
    }

    @Override
    public org.apache.camel.Component resolveComponent(String name, CamelContext arg1) throws Exception {
        JmsConfiguration jmsConfiguration = new JmsConfiguration();
        jmsConfiguration.setConnectionFactory(cf);
        jmsConfiguration.setConcurrentConsumers(3);
        ActiveMQComponent activeMQComponent = new ActiveMQComponent(arg1);
        activeMQComponent.setConfiguration(jmsConfiguration);
        activeMQComponent.setTransactionManager(txm);
        activeMQComponent.setTransacted(true);

        return activeMQComponent;
    }
    
    public JmsTransactionManager getTransactionManager() {
        return txm;
    }

}
