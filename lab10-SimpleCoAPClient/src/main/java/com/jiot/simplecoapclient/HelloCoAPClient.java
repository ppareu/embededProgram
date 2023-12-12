/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.jiot.simplecoapclient;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.elements.exception.ConnectorException;

/**
 *
 * @author yjkim
 */
public class HelloCoAPClient implements CoAPClient {
    
    private CoapClient client = null;
    
    public void run(String hostName) {
        client = new CoapClient();
        
        CoapResponse response;
        String uri;
        
        Scanner input = new Scanner(System.in);
        System.out.print("Input URI path(or 'q'): ");
        for (String line = input.nextLine(); !line.equals("q"); line = input.nextLine()) {
            try {
                uri = "coap://" + hostName + ":5683" + line + "?text=Everybody";
                client.setURI(uri);
                response = client.get();
                if (response != null) {
                    System.out.println("code: " + response.getCode());
                    System.out.println("options: " + response.getOptions());
                    System.out.println("payload: " + Utils.toHexString(response.getPayload()));
                    System.out.println("text: " + response.getResponseText());
                    System.out.println("advanced: " + Utils.prettyPrint(response));
                }
                
                uri = "coap://" + hostName + ":5683" + line;
                client.setURI(uri);
                response = client.post("Everybody", MediaTypeRegistry.TEXT_PLAIN);
                if (response != null) {
                    System.out.println("Post: " + response.getResponseText());
                }
                
                System.out.print("Input URI path(or 'q'): ");
            } catch (ConnectorException ex) {
                Logger.getLogger(HelloCoAPClient.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(HelloCoAPClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
