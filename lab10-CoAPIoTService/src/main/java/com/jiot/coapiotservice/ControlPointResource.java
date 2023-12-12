/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jiot.coapiotservice;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jiot.things.ControlPoint;
import com.jiot.things.FloatInputSupport;
import com.jiot.things.OutputControlPoint;
import java.util.Observable;
import java.util.Observer;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;

/**
 *
 * @author yjkim
 */
public class ControlPointResource extends CoapResource {
    private ControlPoint point = null;
    
    public ControlPointResource(ControlPoint cp) {
        super(String.valueOf(cp.getId()));
        this.point = cp;
        addChildResources();
    }
    
    private String stringfyJsonWithSingleproperty(String name, String value) {
        JsonObject object = new JsonObject();
        object.addProperty(name, value);
        return (new Gson()).toJson(object);
    }
    
    private String getPropertyFromJson(String json, String propName) {
        String value = null;
        JsonElement element = (new JsonParser()).parse(json);
        if (element != null) {
            JsonElement prop = element.getAsJsonObject().get(propName);
            if (prop != null) {
                value = prop.getAsString();
            }
        }
        return value;
    }
    
    private void addChildResources() {
        add(new CoapResource("properties") {        
            private CoapResource initialize() {
                setObservable(true);
                setObserveType(CoAP.Type.CON);
                getAttributes().setObservable();
                
                point.addObserver(new Observer() {
                    @Override
                    public void update(Observable obj, Object arg) {
                        if (arg != null) {
                            changed();
                        }
                    }
                });
                
                return this;
            }

            @Override
            public void handleGET(CoapExchange exchange) {
                System.out.println("[DEBUG] GET " + getURI());
                JsonObject object = new JsonObject();
                object.addProperty("Id", point.getId());
                object.addProperty("Type", point.getType().name());
                object.addProperty("Name", point.getName());
                object.addProperty("Enabled", point.isEnabled());
                String response = (new Gson()).toJson(object);
                exchange.respond(response);
                System.out.println("[DEBUG] Response " + response);
            }
            
            @Override
            public void handlePOST(CoapExchange exchange) {
                String jsonStr = exchange.getRequestText();
                String response = null;
                if (jsonStr != null) {
                    System.out.println("[DEBUG] POST " + getURI() + " " + jsonStr);
                    String newName = getPropertyFromJson(jsonStr, "name");
                    if (newName != null) {
                        point.setName(newName);
                        response = stringfyJsonWithSingleproperty("result", "true");
                    }
                    else {
                        response = stringfyJsonWithSingleproperty("result", "Incorrect request");
                    }
                }
                else {
                    response = stringfyJsonWithSingleproperty("result", "false");                    
                }
                exchange.respond(response);
                System.out.println("[DEBUG] Response " + response);
            }
        }.initialize());
        
        add(new CoapResource("value") {
            private CoapResource initialize() {
                setObservable(true);
                setObserveType(CoAP.Type.CON);
                getAttributes().setObservable();
                
                point.addObserver(new Observer() {
                    @Override
                    public void update(Observable obj, Object arg) {
                        if (arg == null) {
                            changed();
                        }
                    }
                });
                
                return this;
            }

            @Override
            public void handleGET(CoapExchange exchange) {
                System.out.println("[DEBUG] GET " + getURI());
                String response = stringfyJsonWithSingleproperty("value", 
                    String.valueOf(point instanceof FloatInputSupport ?
                            ((FloatInputSupport)point).getFloatValue() : 
                            (int)point.getValue()));
                exchange.respond(response);
                System.out.println("[DEBUG] Response " + response);
            }
            
            @Override
            public void handlePOST(CoapExchange exchange) {
                String jsonStr = exchange.getRequestText();
                String response = null;
                if ((jsonStr != null) && (point instanceof OutputControlPoint)) {
                    System.out.println("[DEBUG] POST " + getURI() + " " + jsonStr);
                    try {
                        String valueStr = getPropertyFromJson(jsonStr, "value");
                        int newValue = valueStr.contains(".") ? (int)Double.parseDouble(valueStr) : Integer.parseInt(valueStr);
                        ((OutputControlPoint)point).setValue(newValue);
                        response = stringfyJsonWithSingleproperty("result", "true");
                    } catch (Throwable ex) {
                        response = stringfyJsonWithSingleproperty("result", 
                            "Exception: occured: " + ex.getMessage());                        
                    }
                }
                else {
                    response = stringfyJsonWithSingleproperty("result", "false");                    
                }
                exchange.respond(response);
                System.out.println("[DEBUG] Response " + response);
            }
        }.initialize());
    } 
    
}
