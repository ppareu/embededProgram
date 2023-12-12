/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.jiot.simplecoapserver;

import java.util.Timer;
import java.util.TimerTask;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;

/**
 *
 * @author yjkim
 */
public class SimpleCoAPServer {

    public static void main(String[] args) {
        CoapServer server = new CoapServer();
        
        CoapResource resource = new CoapResource("hello") {
            @Override
            public void handlePOST(CoapExchange exchange) {
                String text = exchange.getRequestText();
                System.out.println("'" + getName() + "' called by POST method");
                exchange.respond("Hello " + text);
            }

            @Override
            public void handleGET(CoapExchange exchange) {
                String text = exchange.getRequestOptions().getUriQuery().get(0);
                System.out.println("'" + getName() + "' called by GET method");
                exchange.respond("Hello " + text.substring(text.indexOf("=")+1));
            }            
        };
        
        resource.add(new CoapResource("world") {
            @Override
            public void handlePOST(CoapExchange exchange) {
                String text = exchange.getRequestText();
                System.out.println("'" + getName() + "' called by POST method");
                exchange.respond("Hello World!! " + text);
            }

            @Override
            public void handleGET(CoapExchange exchange) {
                String text = exchange.getRequestOptions().getUriQuery().get(0);
                System.out.println("'" + getName() + "' called by GET method");
                exchange.respond("Hello World!!" + text.substring(text.indexOf("=")+1));
            }            
        });
        
        resource.add(new CoapResource("count") {
            private long count = 0;
            private Timer timer = null;
            
            private CoapResource initialize() {
                setObservable(true);
                setObserveType(CoAP.Type.CON);
                
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        increaseCount();
                        changed();                        
                    }
                }, 1000, 1000);
                return this;
            }
            
            private void increaseCount() {
                count++;
            }
            
            @Override
            public void handleGET(CoapExchange exchange) {
                exchange.respond(String.valueOf(count));
            }            
        }.initialize());
           
        server.add(resource);
        
        System.out.println("Start simple COAP server...");
        server.start();
    }
}
