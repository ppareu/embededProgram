/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jiot.simplecoapclient;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;

/**
 *
 * @author yjkim
 */
public class CountObservationCoAPClient implements CoAPClient {

    private CoapClient client = null;
        
    @Override
    public void run(String hostName) {
        if (hostName == null) hostName = "localhost";
        
        client = new CoapClient("coap://" + hostName + ":5683/hello/count");
        CoapObserveRelation relation = client.observe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse cr) {
                String count = cr.getResponseText();
                System.out.println("Changed value of count: " + count);
            }

            @Override
            public void onError() {
                System.out.println("Errors occured at observing the change of count...");
            }
        });

        Thread quitThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Press enter key to exit: ");
                (new Scanner(System.in)).nextLine();
                relation.proactiveCancel();
            }
        });
        
        try {
            quitThread.start();
            quitThread.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(CountObservationCoAPClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
