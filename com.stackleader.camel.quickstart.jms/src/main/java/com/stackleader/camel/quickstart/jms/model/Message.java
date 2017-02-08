/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stackleader.camel.quickstart.jms.model;

public class Message {
    private final int code;

    public Message(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
    
    
}
