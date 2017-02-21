/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stackleader.camel.quickstart.jms;

import com.ibm.mq.jms.MQConnectionFactory;
import javax.jms.ConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.jms.JmsConfiguration;
import org.apache.camel.component.jms.JmsMessageType;
import org.apache.camel.spi.ComponentResolver;
import org.osgi.service.component.annotations.Component;

/**
 *
 * @author jeckste
 */
@Component(immediate = true, service = ComponentResolver.class, property = "component=wmq")
public class MqCamelComponent implements ComponentResolver {

    @Override
    public org.apache.camel.Component resolveComponent(String name, CamelContext arg1) throws Exception {
        
        MQConnectionFactory cf = new MQConnectionFactory();
        cf.setChannel("aChannel");
        cf.setHostName("abc.com");
        cf.setTransportType(1);
        cf.setQueueManager("qman");
        cf.setPort(1414);

        JmsConfiguration jmsConfig = new JmsConfiguration();
        jmsConfig.setConnectionFactory((ConnectionFactory) cf);
        jmsConfig.setJmsMessageType(JmsMessageType.Object);
        return new JmsComponent(jmsConfig);
    }

}
