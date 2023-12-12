/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jiot.simplecoapclient;

import java.util.Scanner;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

/**
 *
 * @author yjkim
 */
public class AsyncHelloCoAPClient implements CoAPClient {

    private CoapClient client = null;
        
    @Override
    public void run(String hostName) {
        client = new CoapClient("coap://" + hostName + ":5683/hello?text=Everybody");
        
        client.get(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse cr) {
                System.out.println("Async. Get: " + cr.getResponseText());
            }

            @Override
            public void onError() {
                System.out.println("Cannot get response asynchronouslly...");
            }
        });
        
        client.post(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse cr) {
                System.out.println("Async. Post: " + cr.getResponseText());
            }

            @Override
            public void onError() {
                System.out.println("Errors were occured in posting the request asynchronouslly...");
            }
        }, "Everybody", MediaTypeRegistry.TEXT_PLAIN);
        
        System.out.println("Press enter to exit: ");
        (new Scanner(System.in)).nextLine();
    }
    
}
