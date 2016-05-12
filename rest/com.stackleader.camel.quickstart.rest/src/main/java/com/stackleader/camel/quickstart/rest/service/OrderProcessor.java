/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stackleader.camel.quickstart.rest.service;

import aQute.bnd.annotation.component.Component;
import com.stackleader.camel.quickstart.rest.model.LineItem;
import com.stackleader.camel.quickstart.rest.model.Order;
import com.stackleader.camel.quickstart.rest.model.Receipt;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author jeckstei
 */
@Component(provide = OrderProcessor.class)
public class OrderProcessor {
    public Receipt processOrder(Order order) {
        Receipt receipt = new Receipt();
        receipt.setId(1);
        receipt.setOrderId(1);
        receipt.setLastFourCC(1111);
        receipt.setLineItems(order.getLineItems());
        receipt.setTotal(sum(order.getLineItems()));
        return receipt;
    }
    
    private BigDecimal sum(List<LineItem> lineItems) {
        BigDecimal total = BigDecimal.ZERO;
        for(LineItem lineItem : lineItems) {
            total = total.add(lineItem.getPrice().multiply(BigDecimal.valueOf(lineItem.getQuantity())));
        }
        return total;
    }
}
