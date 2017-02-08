/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stackleader.camel.quickstart.jms;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.stackleader.camel.quickstart.jms.model.CreditCardApplication;
import com.stackleader.camel.quickstart.jms.model.CreditCardApplicationResponse;
import com.stackleader.camel.quickstart.jms.model.ErrorResponse;
import com.stackleader.camel.quickstart.jms.model.Message;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author eckstj2
 */
public class IntroductionToTest extends CamelTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(IntroductionToTest.class);

    @Produce
    protected ProducerTemplate producer;

    @Override
    public boolean isUseAdviceWith() {
        return true;
    }

    @Override
    public boolean isUseDebugger() {
        return true;
    }

    @Override
    protected void debugBefore(Exchange exchange, Processor processor,
            ProcessorDefinition<?> definition, String id, String shortName) {
        //Set break points here to debug before each step
        LOG.info("Definition: {}, Id: {}, ShortName: {}", definition, id, shortName);
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                //Example
                from("direct:exampleSource").routeId(IntroductionToTest.class.getCanonicalName() + "-" + "example")
                        .to("bean:foo");

                //Exercise1
                from("direct:exercise1").routeId(IntroductionToTest.class.getCanonicalName() + "-" + "exercise1")
                        .onException(Exception.class).handled(true).to("direct:error").maximumRedeliveries(0).end()
                        .to("bean:getCreditCardApplication").id("getCreditCardApplication")
                        //Start of Content Based Router (CBR)
                        .choice()
                        .when().simple("${header.score} range '600..850'")
                        .to("bean:approveApplication").id("approveApplication")
                        .when().simple("${header.score} range '0..599'")
                        .to("bean:denyApplication").id("denyApplication")
                        .otherwise()
                        .throwException(new RuntimeException())
                        .endChoice();
                        //End of CBR

                from("direct:error")
                        .process(ex -> {
                            Message message1 = new Message(1001);
                            Message message2 = new Message(1002);
                            ex.getOut().setBody(new ErrorResponse(Lists.newArrayList(message1, message2)));
                        });
            }
        };
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();
        //
        return registry;
    }
    
    

    @Test
    public void testMockEndpoint() throws Exception {
        context.getRouteDefinition(IntroductionToTest.class.getCanonicalName() + "-" + "example").adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                mockEndpointsAndSkip("bean*");
            }
        });
        context.start();

        MockEndpoint mockFoo = getMockEndpoint("mock:bean:foo");
        mockFoo.setExpectedMessageCount(1);
        producer.sendBody("direct:exampleSource", "bar");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testExercise1LowScore() throws Exception {
        context.getRouteDefinition(IntroductionToTest.class.getCanonicalName() + "-" + "exercise1").adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                weaveById("getCreditCardApplication").replace()
                        .process((Exchange exchange) -> {
                            CreditCardApplication application = new CreditCardApplication();
                            application.setStatus("A");
                            exchange.getIn().setBody(application);
                        });
                
                weaveById("approveApplication").replace()
                        .process((Exchange exchange) -> {
                            CreditCardApplicationResponse applicationResponse = 
                                    new CreditCardApplicationResponse();
                            applicationResponse.setApproved(true);
                            exchange.getIn().setBody(applicationResponse);
                        })
                        .to("mock:approveApplication");
                
                weaveById("denyApplication").replace()
                        .process((Exchange exchange) -> {
                            CreditCardApplicationResponse applicationResponse = 
                                    new CreditCardApplicationResponse();
                            applicationResponse.setApproved(false);
                            exchange.getIn().setBody(applicationResponse);
                        })
                        .to("mock:denyApplication");
            }
        });
        context.start();
        
        MockEndpoint mockApprove = getMockEndpoint("mock:approveApplication");
        mockApprove.setExpectedMessageCount(0);
        
        MockEndpoint mockDeny = getMockEndpoint("mock:denyApplication");
        mockDeny.setExpectedMessageCount(1);
        
        Map<String, Object> headers = Maps.newHashMap();
        headers.put("score", 400);
        Object response
                = (Object) producer
                        .sendBodyAndHeaders("direct:exercise1",
                                ExchangePattern.InOut, null, headers);
        Assert.assertTrue(response instanceof CreditCardApplicationResponse);
        Assert.assertFalse(((CreditCardApplicationResponse)response).isApproved());
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testExercise1HighScore() throws Exception {
        context.getRouteDefinition(IntroductionToTest.class.getCanonicalName() + "-" + "exercise1").adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                weaveById("getCreditCardApplication").replace()
                        .process((Exchange exchange) -> {
                            CreditCardApplication application = new CreditCardApplication();
                            application.setStatus("A");
                            exchange.getIn().setBody(application);
                        });
                
                weaveById("approveApplication").replace()
                        .process((Exchange exchange) -> {
                            CreditCardApplicationResponse applicationResponse = 
                                    new CreditCardApplicationResponse();
                            applicationResponse.setApproved(true);
                            exchange.getIn().setBody(applicationResponse);
                        })
                        .to("mock:approveApplication");
                
                weaveById("denyApplication").replace()
                        .process((Exchange exchange) -> {
                            CreditCardApplicationResponse applicationResponse = 
                                    new CreditCardApplicationResponse();
                            applicationResponse.setApproved(false);
                            exchange.getIn().setBody(applicationResponse);
                        })
                        .to("mock:denyApplication");
            }
        });
        context.start();
        
        MockEndpoint mockApprove = getMockEndpoint("mock:approveApplication");
        mockApprove.setExpectedMessageCount(1);
        
        MockEndpoint mockDeny = getMockEndpoint("mock:denyApplication");
        mockDeny.setExpectedMessageCount(0);
        
        
        Map<String, Object> headers = Maps.newHashMap();
        headers.put("score", 700);
        Object response
                = (Object) producer
                        .sendBodyAndHeaders("direct:exercise1",
                                ExchangePattern.InOut, null, headers);
        Assert.assertTrue(response instanceof CreditCardApplicationResponse);
        Assert.assertTrue(((CreditCardApplicationResponse)response).isApproved());
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testExercise1Error() throws Exception {
        context.getRouteDefinition(IntroductionToTest.class.getCanonicalName() + "-" + "exercise1").adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                weaveById("getCreditCardApplication").replace()
                        .process((Exchange exchange) -> {
                            CreditCardApplication application = new CreditCardApplication();
                            application.setStatus("A");
                            exchange.getIn().setBody(application);
                        });
                
                weaveById("approveApplication").replace()
                        .process((Exchange exchange) -> {
                            CreditCardApplicationResponse applicationResponse = 
                                    new CreditCardApplicationResponse();
                            applicationResponse.setApproved(true);
                            exchange.getIn().setBody(applicationResponse);
                        })
                        .to("mock:approveApplication");
                
                weaveById("denyApplication").replace()
                        .process((Exchange exchange) -> {
                            CreditCardApplicationResponse applicationResponse = 
                                    new CreditCardApplicationResponse();
                            applicationResponse.setApproved(false);
                            exchange.getIn().setBody(applicationResponse);
                        })
                        .to("mock:denyApplication");
            }
        });
        context.start();
        Map<String, Object> headers = Maps.newHashMap();
        headers.put("score", -1);
        Object response
                = (Object) producer
                        .sendBodyAndHeaders("direct:exercise1",
                                ExchangePattern.InOut, null, headers);
        Assert.assertTrue(response instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse)response;
        Assert.assertTrue(errorResponse.getMessages().stream().filter(m -> m.getCode() == 1001).findFirst().isPresent());
    }
}
