/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stackleader.camel.quickstart.jms;

import org.apache.camel.Exchange;
import org.osgi.service.component.annotations.Component;

/**
 *
 * @author jeckste
 */
@Component(service = Validator.class)
public class Validator {

    
    public void process(Exchange exchange) {
        
    }
}
