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
public class SimpleMqttSubscriber {
    
    private final static String CLIENT_ID = "simplesubscriber";
    private final static String TOPIC_NAME = "Sports";
    
    private MqttClient client;
    private String uri;

    public SimpleMqttSubscriber() {
        uri = System.getProperty("mqtt.server", "tcp://localhost:1883");
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
                    System.out.println(String.format("\nArrived - [%s] message: %s", new Date(), mm));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken imdt) {
                }
            });
                    
            client.connect(connOpts);
        } catch (MqttException ex) {
            Logger.getLogger(SimpleMqttSubscriber.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(SimpleMqttSubscriber.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void subscribe() {
        try {
            client.subscribe(TOPIC_NAME);
        } catch (MqttException ex) {
            Logger.getLogger(SimpleMqttSubscriber.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        SimpleMqttSubscriber subscriber = new SimpleMqttSubscriber();        
        Scanner scanner = new Scanner(System.in);
        
        subscriber.subscribe();
        
        System.out.print("Press enter key to quit...");
        scanner.nextLine();
        
        System.out.print("Simple MQTT subscriber is closing...");
        subscriber.close();
        scanner.close();
    }
}
