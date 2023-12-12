/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jiot.jiotmqttclihandler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jiot.cli.CommandInterpreter;
import com.jiot.things.ControlPoint;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

/**
 *
 * @author yjkim
 */
public class JiotMqttCLIHandler implements Observer {
    
    public static final String TOPIC_PREFIX = "jiot/thing/";
    public static final String TOPIC_COMMAND = TOPIC_PREFIX + "+/%s/command";
    public static final String TOPIC_RESULT = TOPIC_PREFIX + "%s/result";
    public static final String TOPIC_BROADCAST = TOPIC_PREFIX + "%s/broadcast";
    public static final String CLIENT_ID = "cli";
    
    private ExecutorService commandThreadPool = Executors.newCachedThreadPool();
    private MqttClient client = null;
    private String uri = null;
    private String clientId = null;
    private String commandTopic = null;
    private String broadcastTopic = null;

    public JiotMqttCLIHandler() throws Exception {
        uri = System.getProperty("mqtt.server", "tcp://127.0.0.1:1883");
        System.out.println("MQTT server URI: " + uri);
        
        String ipAddr = getLocalIPAddress();
        if (ipAddr == null) {
            throw new Exception("Cannot find local IP address of this thing");
        }
        System.out.println("Used IP Address: " + ipAddr);
//        clientId = (ipAddr != null && !ipAddr.isBlank()) ? CLIENT_ID+"_"+ipAddr.replace(".", "_") : CLIENT_ID;
        clientId = CLIENT_ID;
        
        commandTopic = String.format(TOPIC_COMMAND, clientId);
        broadcastTopic = String.format(TOPIC_BROADCAST, clientId);
        System.out.println("Command Topic: " + commandTopic);
        System.out.println("Broadcast Topic: " + broadcastTopic);
        
        String tmpDir = System.getProperty("java.io.tmpdir");
        MqttDefaultFilePersistence dataStore= new MqttDefaultFilePersistence(tmpDir);
        System.out.println("Default file persistence: " + tmpDir);

        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        
        client = new MqttClient(uri, clientId, dataStore);
        
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable thrwbl) {
                Logger.getLogger(JiotMqttCLIHandler.class.getName()).log(Level.SEVERE, null, thrwbl);
                System.exit(-1);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken imdt) {
                try {
                    Logger.getLogger(JiotMqttCLIHandler.class.getName())
                            .log(Level.INFO, String.format("Delivered - [%s] message: %s", new Date(), imdt.getMessage()));
                } catch (MqttException ex) {
                    Logger.getLogger(JiotMqttCLIHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void messageArrived(String topic, MqttMessage msg) throws Exception {
                String[] clientInfo = topic.substring(TOPIC_PREFIX.length()).split("/");
                String commandStr = msg.toString();
                
                System.out.println("Message received from " + topic + " : " + msg);
                
                commandThreadPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        String[] command = commandStr.split(" ");
                        String result = null;
                        
                        result = CommandInterpreter.getInstance().execute(command);
                        result = (result == null) ? "Success" : result;
                        
                        try {
                            publish(clientInfo[0], result, 0);
                        } catch (MqttException ex) {
                            Logger.getLogger(JiotMqttCLIHandler.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
            }
        });
        
        client.connect(connOpts);
        
        client.subscribe(commandTopic);
    }
    
    public void close() {
        if (client != null) {
            try {
                client.disconnect();
                client.close();
                client = null;
            } catch (MqttException ex) {
                Logger.getLogger(JiotMqttCLIHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @Override
    public void update(Observable obj, Object arg) {
        if (obj instanceof ControlPoint) {
            ControlPoint cp = (ControlPoint)obj;
            JsonObject jsonObj = new JsonObject();
            jsonObj.addProperty("type", "cov");
            jsonObj.addProperty("handlerId", clientId);
            jsonObj.addProperty("pointId", cp.getId());
            jsonObj.addProperty("pointName", cp.getName());
            jsonObj.addProperty("value", cp.getValue());
            String payload = (new Gson()).toJson(jsonObj);
            try {
                broadcast(payload);
            } catch (MqttException ex) {
                Logger.getLogger(JiotMqttCLIHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void publish(String clientId, String payload, int qos) throws MqttException {
        MqttTopic topic = client.getTopic(String.format(TOPIC_RESULT, clientId));
//        System.out.println("Topic published: " + topic.getName());
        topic.publish(payload.getBytes(), qos, false);
    }
    
    private void broadcast(String payload) throws MqttException {
        MqttTopic topic = client.getTopic(broadcastTopic);
        topic.publish(payload.getBytes(), 0, false);
    }
    
    private String getLocalIPAddress() throws UnknownHostException {
        String ipAddr = null;
        
        InetAddress localhost = InetAddress.getLocalHost();
        ipAddr = (localhost.getHostAddress()).trim();
        
        return ipAddr;
    }
            
}
