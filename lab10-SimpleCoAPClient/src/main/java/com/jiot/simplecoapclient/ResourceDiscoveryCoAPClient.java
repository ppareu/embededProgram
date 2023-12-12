/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jiot.simplecoapclient;

import java.util.Scanner;
import java.util.Set;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.WebLink;
import org.eclipse.californium.core.coap.LinkFormat;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.ResourceAttributes;

/**
 *
 * @author yjkim
 */
public class ResourceDiscoveryCoAPClient implements CoAPClient {

    private CoapClient client = null;
        
    @Override
    public void run(String hostName) {
        
        String uri = "coap://" + hostName + ":5683";
        String discoveryPath = "/.well-known/core";
        
        client = new CoapClient(uri+discoveryPath);
        
        client.get(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse cr) {
                System.out.println("Async. Get: " + cr.getResponseText());
                
                if (cr.getOptions().getContentFormat() == MediaTypeRegistry.APPLICATION_LINK_FORMAT) {
                    System.out.println("---------------------------------------------");
                    Set<WebLink> links = LinkFormat.parse(cr.getResponseText());
                    for (WebLink link : links) {
                        System.out.println(link.getURI());
                        ResourceAttributes attrs = link.getAttributes();
                        Set<String> keys = attrs.getAttributeKeySet();
                        for (String key : keys) {
                            System.out.println(key + ": " + attrs.getAttributeValues(key));
                        }
                    }
                    System.out.println("---------------------------------------------");
                }
            }

            @Override
            public void onError() {
                System.out.println("Cannot find CoAP resources...");
            }
        });
        
        System.out.println("Press enter to exit: ");
        (new Scanner(System.in)).nextLine();
    }
    
}
