/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jiot.jiotmqttcliclient;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 *
 * @author yjkim
 */
public class ChangeOfValue {
    
    public static final String TYPE = "cov";
    
    private String handlerId = null;
    private int pointId = -1;
    private String pointName = null;
    private int value = -1;

    public ChangeOfValue(String handlerId, int pointId, String pointName, int value) {
        this.handlerId = handlerId;
        this.pointId = pointId;
        this.pointName = pointName;
        this.value = value;
    }

    public ChangeOfValue(JsonObject jsonObj) {
        this.handlerId = jsonObj.get("handlerId").getAsString();
        this.pointId = jsonObj.get("pointId").getAsInt();
        this.pointName = jsonObj.get("pointName").getAsString();
        this.value = jsonObj.get("value").getAsInt();
    }

    public String getHandlerId() {
        return handlerId;
    }

    public String getPointName() {
        return pointName;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", TYPE);
        obj.addProperty("handlerId", handlerId);
        obj.addProperty("pointId", pointId);
        obj.addProperty("pointName", pointName);
        obj.addProperty("value", value);
        return (new Gson()).toJson(obj);
    }
    
}
