/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stackleader.camel.quickstart.rest.service;

import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.component.servlet.osgi.OsgiServletRegisterer;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;

/**
 *
 * @author jeckste
 */
@Component(immediate = true, service = ServletRegisterer.class)
public class ServletRegisterer extends OsgiServletRegisterer {
    
    private HttpService httpService;
    
    public ServletRegisterer() {
        
    }
    
    @Activate
    public void activate() throws Exception {
        this.setHttpService(httpService);
        this.setServlet(new CamelHttpTransportServlet());
        this.setAlias("/foo/bar");
        this.register();
    }
    
    @Deactivate
    public void deactivate() {
        this.unregister();
    }
    
    @Reference
    public void getHttpRequest(HttpService httpService) {
        this.httpService = httpService;
    }
}
