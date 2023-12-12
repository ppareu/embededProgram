/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.jiot.jiotmqttclihandler;

import com.jiot.things.ControlPoint;
import com.jiot.things.ControlPointContainer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yjkim
 */
public class JiotMqttCLIHandlerMain {
    private static ControlPointContainer cpContainer = null;
    private static JiotMqttCLIHandler cliHandler = null;
    
    public static void close() {
        if (cpContainer != null) cpContainer.stop();
        if (cliHandler != null) cliHandler.close();
    }

    public static void main(String[] args) {
        try {
            cpContainer = ControlPointContainer.getInstance();
            cpContainer.start();
            
            cliHandler = new JiotMqttCLIHandler();
            
            for (ControlPoint cp : cpContainer.getControlPoints()) {
                cp.addObserver(cliHandler);
            }
            
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    System.out.println("jiot MQTT CLI service program is shutdowning...");
                    close();
                }
            });
            
            for(;;)
                Thread.sleep(1000);
        } catch (Exception ex) {
            Logger.getLogger(JiotMqttCLIHandlerMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
