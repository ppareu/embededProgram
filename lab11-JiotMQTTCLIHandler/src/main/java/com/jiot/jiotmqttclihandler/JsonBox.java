/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jiot.jiotmqttclihandler;

import com.google.gson.Gson;
import java.util.Date;

public class JsonBox {
    private int channelId;
    private String createdAt;
    private int entryId;
    private String field1;
    private String field2;
    private String field3;
    private String field4;

    public static JsonBox fromJson(String jsonStr) {
        return new Gson().fromJson(jsonStr, JsonBox.class);
    }

    @Override
    public String toString() {
        return
           "channelId= " + channelId + "\n" +
           "createdAt= " + createdAt + "\n" +
           "entryId= " + entryId + "\n" +
           "LED1= " + field1 + "\n" +
           "LED2= " + field2 + "\n" +
           "LED3= " + field3 + "\n" +
           "FAN= " + field4 + "\n";
    }
    
}

