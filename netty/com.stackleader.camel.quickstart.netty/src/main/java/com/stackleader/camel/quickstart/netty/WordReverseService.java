/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stackleader.camel.quickstart.netty;

import org.osgi.service.component.annotations.Component;


/**
 *
 * @author jeckstei
 */
@Component(service = WordReverseService.class)
public class WordReverseService {

    /**
     * Copied from
     * http://codereview.stackexchange.com/questions/37364/reversing-words-in-a-string
     *
     * @param sentence
     * @return
     */
    public String reverseWords(String sentence) {
        StringBuilder sb = new StringBuilder(sentence.length() + 1);
        String[] words = sentence.split(" ");
        for (int i = words.length - 1; i >= 0; i--) {
            sb.append(words[i]).append(' ');
        }
        sb.setLength(sb.length() - 1);  // Strip trailing space
        return sb.toString();
    }

}
