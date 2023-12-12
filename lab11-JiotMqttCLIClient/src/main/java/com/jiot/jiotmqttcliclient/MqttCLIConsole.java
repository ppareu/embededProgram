/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jiot.jiotmqttcliclient;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Date;
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
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

/**
 *
 * @author yjkim
 */
public class MqttCLIConsole {
    
    public static final String TOPIC_PREFIX = "jiot/thing/";
    public static final String TOPIC_COMMAND = TOPIC_PREFIX + "%s/%s/command";
    public static final String TOPIC_RESULT = TOPIC_PREFIX + "%s/result";
    public static final String TOPIC_BROADCAST = TOPIC_PREFIX + "%s/broadcast";
    public static final String CLIENT_ID = "cli";
    
    private MqttClient client = null;
    private String uri = null;
    private String clientId = null;
    private String commandTopic = null;
    private String resultTopic = null;
    private String broadcastTopic = null;
    private BigDataHandler bgHandler = null;
    private String promptStr = null;

    public MqttCLIConsole(String clientId, String cliHandlerId, BigDataHandler bdHandler) throws MqttException {
        this.clientId = clientId;
        this.bgHandler = bdHandler;
        
        uri = System.getProperty("mqtt.server", "tcp://127.0.0.1:1883");
        System.out.println("MQTT server URI: " + uri);
                
        commandTopic = String.format(TOPIC_COMMAND, clientId, cliHandlerId);
        resultTopic = String.format(TOPIC_RESULT, clientId);
        broadcastTopic = String.format(TOPIC_BROADCAST, cliHandlerId);
        System.out.println("Command Topic: " + commandTopic);
        System.out.println("Result Topic: " + resultTopic);
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
                Logger.getLogger(MqttCLIConsole.class.getName()).log(Level.SEVERE, null, thrwbl);
                System.exit(-1);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken imdt) {
                try {
                    Logger.getLogger(MqttCLIConsole.class.getName())
                            .log(Level.INFO, String.format("Delivered - [%s] message: %s", new Date(), imdt.getMessage()));
                } catch (MqttException ex) {
                    Logger.getLogger(MqttCLIConsole.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void messageArrived(String topic, MqttMessage msg) throws Exception {
                if (topic.endsWith("broadcast")) {
                    JsonObject jsonObj = (new JsonParser()).parse(msg.toString()).getAsJsonObject();
                    String broadcastType = jsonObj.get("type").getAsString();
                    if (broadcastType.equals(ChangeOfValue.TYPE)) {
                        ChangeOfValue cov = new ChangeOfValue(jsonObj);
                        bdHandler.saveData(cov);
                    }
                    else {
                        Logger.getLogger(MqttCLIConsole.class.getName())
                            .log(Level.INFO, "Received a broadcast message: {0}", msg);
                        displayPrompt();
                    }
                }
                else {
                        Logger.getLogger(MqttCLIConsole.class.getName())
                            .log(Level.INFO, "Result: {0}", msg);
                        displayPrompt();
                }
            }
        });
        
        client.connect(connOpts);
        
        client.subscribe(resultTopic);
        client.subscribe(broadcastTopic);
    }
    
    public void close() throws MqttException {
        if (client != null) {
            client.disconnect();
            client.close();
            client = null;
        }
    }
    
    public void publish(String payload, int qos) throws MqttException {
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(qos);
        client.publish(commandTopic, message);
    }

    public void setPrompt(String prompt) {
        this.promptStr = prompt;
    }
    
    public void displayPrompt() {
        System.out.print(promptStr);
    }
    
}
