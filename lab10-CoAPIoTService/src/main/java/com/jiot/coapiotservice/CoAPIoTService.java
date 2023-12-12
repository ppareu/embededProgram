/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.jiot.coapiotservice;

/**
 *
 * @author yjkim
 */
public class CoAPIoTService {
    
    private static Thread mainThread = null;

    public static void main(String[] args) {
        mainThread = Thread.currentThread();
        
        final ControlPointCoAPServer server = new ControlPointCoAPServer();
        server.start();
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("Control Point CoAP server is now shutdowned...");
                server.stop();
                mainThread.interrupt();
            }
        });
    }
}
