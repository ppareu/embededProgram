/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.jiot.simplecoapclient;

/**
 *
 * @author yjkim
 */
public class SimpleCoAPClient {
    
    public static void main(String[] args) {
        CoAPClient client = null;
        
//        client = new HelloCoAPClient();
//        client = new AsyncHelloCoAPClient();
//        client = new CountObservationCoAPClient();
        client = new ResourceDiscoveryCoAPClient();
        
        client.run(args[0]);
    }
}
