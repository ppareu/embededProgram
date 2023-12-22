/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license parkhyeonbin
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jiot.jiotmqttclihandler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jiot.things.ControlPoint;
import com.jiot.things.ControlPointContainer;
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
 * @author comit4
 */

public class JiotMQTTCLIHandler implements Observer {

    private ControlPointContainer cpContainer = null;
    
    public static final String TOPIC_PREFIX = "jiot/thing/";
    public static final String TOPIC_COMMAND = TOPIC_PREFIX + "+/%s/command";
    public static final String TOPIC_RESULT = TOPIC_PREFIX + "%s/result";
    public static final String TOPIC_BROADCAST = TOPIC_PREFIX + "%s/broadcast";
    
    private ExecutorService commandThreadPool = Executors.newCachedThreadPool();
    private MqttClient client = null;
    private String brokerURI = null;
    private String handlerId = null;
    private String commandTopic = null;
    private String broadcastTopic = null;
    
    private String sensorWriteAPI = "W5HSVAR8VGA9TPNU";
    private String sensorReadAPI = "FDVVD70XIVHY4YR7";
    
    private String actuatorWriteAPI = "NRNBFO36JRH4WAD6";
    private String actuatorReadAPI = "2YXWDSE7P5YGUUTR";
    
    private String thingSpeakPWD = "0XD/j+H0jOq3hSqti6x/5WSs";
    private String thingSpeakChannelId = "Fh8HDTIRNTE8EREzGTAYOCY";
    private String actuatorChannelId = "2378342";
    private String sensorChannelId = "2378332";
    
    //LedOn = "https://api.thingspeak.com/apps/thinghttp/send_request?api_key=HH963VJB1PO2FH0Y";
    //LedOff = "https://api.thingspeak.com/apps/thinghttp/send_request?api_key=TWI6BYKKQMEWOIYC";
    //FanOn = "https://api.thingspeak.com/apps/thinghttp/send_request?api_key=MYE7MIKAR6IIVOKG";
    //FanOff = "https://api.thingspeak.com/apps/thinghttp/send_request?api_key=KOOTOOZZO3R9IG2Q";
    
    public JiotMQTTCLIHandler(String handlerId) throws MqttException {
        this.handlerId = handlerId;
        brokerURI = System.getProperty("mqtt.server", "tcp://127.0.0.1:1883");
        System.out.println("MQTT Broker URI: " + brokerURI);
        
//        commandTopic = String.format(TOPIC_COMMAND, handlerId);
        commandTopic = String.format("channels/%s/subscribe", actuatorChannelId);
//        broadcastTopic = String.format(TOPIC_BROADCAST, handlerId);
        broadcastTopic = String.format("channels/%s/publish", sensorChannelId);
        System.out.println("Command Topic: " + commandTopic);
        System.out.println("Broadcast Topic: " + broadcastTopic);
        
        String tmpDir = System.getProperty("java.io.tmpdir");
        MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir);
        System.out.println("Default file persistence: " + dataStore);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setUserName(thingSpeakChannelId);
        connOpts.setPassword(thingSpeakPWD.toCharArray());
        
        client = new MqttClient(brokerURI, handlerId, dataStore);
        
        client = new MqttClient(
                brokerURI, 
                thingSpeakChannelId,
                dataStore);
        
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable thrwbl) {
                System.out.println("Connection loss...");
                System.exit(-1);
            }

            @Override
            public void messageArrived(String topic, MqttMessage mm) throws Exception {
                
                //System.out.println(String.format("\nArrived - [%s] message: %s", new Date(), mm));
                
                String messageContent = new String(mm.getPayload());
                JsonBox jsonBox = JsonBox.fromJson(messageContent);
                
                System.out.println("Parsed JsonBox Object: \n" + new Date() + "\n" + jsonBox.toString());
                
//                String[] clientInfo = topic.substring(TOPIC_PREFIX.length()).split("/");
//                String commandStr = mm.toString();
//                System.out.println("Command received from " + topic + " : " + commandStr);
//                
//                commandThreadPool.submit(new Runnable() {
//                    @Override
//                    public void run() {
//                        String[] command = commandStr.split(" ");
//                        String result = null;
//                        result = CommandInterpreter.getInstance().execute(command);
//                        result = (result == null) ? "Success" : result;
//                        
//                        try {
//                            publish(clientInfo[0], result, 0);
//                        } catch (MqttException ex) {
//                            Logger.getLogger(JiotMQTTCLIHandler.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                    }
//                    
//                });
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken imdt) {
                try {
                    System.out.println(String.format("Delivered  - [%s] message: %s", new Date(), imdt.getMessage()));
                } catch (MqttException ex) {
                    Logger.getLogger(JiotMQTTCLIHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            
        });
        
        client.connect(connOpts);
        
        client.subscribe(commandTopic);
    }
    
    public void close() throws MqttException {
        if (client != null) {
            client.disconnect();
            client.close();
            client = null;
        }
    }
    
    @Override
    public void update(Observable obj, Object arg) {
        if (obj instanceof ControlPoint) {
            ControlPoint cp = (ControlPoint)obj;
            JsonObject jsonObj = new JsonObject();
            jsonObj.addProperty("type", "cov");
            jsonObj.addProperty("handlerId", handlerId);
            jsonObj.addProperty("pointId", cp.getId());
            jsonObj.addProperty("pointName", cp.getName());
            jsonObj.addProperty("value", cp.getValue());
            String payload = (new Gson()).toJson(jsonObj);
            
//            try {
//                broadcast(payload);
//            } catch (MqttException ex) {
//                Logger.getLogger(JiotMQTTCLIHandler.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }
        
        
    }
    
    private void publish(String clientId, String payload, int qos) throws MqttException {
        MqttTopic topic = client.getTopic(String.format(TOPIC_RESULT, clientId));
        topic.publish(payload.getBytes(), qos, false);
    }
    
    private void broadcast(String payload) throws MqttException {
        MqttTopic topic = client.getTopic(broadcastTopic);
        topic.publish(payload.getBytes(), 0, false);
    }
}
