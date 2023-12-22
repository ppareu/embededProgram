/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.jiot.jiotmqttclihandler;

import com.jiot.things.ControlPoint;
import com.jiot.things.ControlPointContainer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 *
 * @author comit4
 */
public class JiotMQTTCLIHandlerMain {
    private static ControlPointContainer cpContainer = null;
    private static JiotMQTTCLIHandler cliHandler = null;
    
    public static void close() throws MqttException {
        if (cpContainer != null) cpContainer.stop();
        if (cpContainer != null) cliHandler.close();
    }
    
    public static void main(String[] args) {
        try {
            cpContainer = ControlPointContainer.getInstance();
            cpContainer.start();
            
            cliHandler = new JiotMQTTCLIHandler("cli");
            
            for (ControlPoint cp : cpContainer.getControlPoints()) {
                cp.addObserver(cliHandler);
            }
            
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        System.out.println("jiot MQTT CLI service handler is now shutdowning...");
                        clone();
                    } catch (CloneNotSupportedException ex) {
                        Logger.getLogger(JiotMQTTCLIHandlerMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            
            for(;;) {
                Thread.sleep(1000);
            }
        } catch (MqttException ex) {
            Logger.getLogger(JiotMQTTCLIHandlerMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(JiotMQTTCLIHandlerMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
