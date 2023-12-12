/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.jiot.simplemqttpublisher;

import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

/**
 *
 * @author yjkim
 */
public class SimpleMqttPublisher {
    
    private final static String CLIENT_ID = "simplepublisher";
    private final static String TOPIC_NAME = "Sports";
    
    private MqttClient client;
    private String uri;

    public SimpleMqttPublisher() {
        uri = System.getProperty("mqtt.server");
        String tmpDir = System.getProperty("java.io.tmpdir");
        System.out.println("uri = " + uri);
        System.out.println("tmpDir = " + tmpDir);
            
        try {
            MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir);            
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            
            client = new MqttClient(uri, CLIENT_ID, dataStore);
            
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable thrwbl) {
                    System.err.println("Lose the connection to the broker!");
                    System.exit(-1);
                }

                @Override
                public void messageArrived(String string, MqttMessage mm) throws Exception {
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken imdt) {
                    try {
                        System.out.println(String.format("\nDelivered - [%s] message: %s", new Date(), imdt.getMessage()));
                    } catch (MqttException ex) {
                        Logger.getLogger(SimpleMqttPublisher.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
                    
            client.connect(connOpts);
        } catch (MqttException ex) {
            Logger.getLogger(SimpleMqttPublisher.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }
    
    public void close() {
        if (client != null) {
            try {
                client.disconnect();
                client.close();
                client = null;
            } catch (MqttException ex) {
                Logger.getLogger(SimpleMqttPublisher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void publish(String payload, int qos) {
        try {
            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(qos);
            client.publish(TOPIC_NAME, message);
        } catch (MqttException ex) {
            Logger.getLogger(SimpleMqttPublisher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        SimpleMqttPublisher publisher = new SimpleMqttPublisher();
        
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a message (or 'q' to exit): ");
        for (String line = scanner.nextLine(); !line.equals("q"); line = scanner.nextLine()) {
            System.out.print("Enter a QoS level(0, 1 or 2): ");
            int qos = Integer.parseInt(scanner.nextLine());
            System.out.print(">>> Publishing '" + line + "' with qos=" + qos + "...");
            publisher.publish(line, qos);
            System.out.print("Enter a message (or 'q' to exit): ");
        }
        
        System.out.print("Simple MQTT publisher is closing...");
        publisher.close();
        scanner.close();
    }
}
