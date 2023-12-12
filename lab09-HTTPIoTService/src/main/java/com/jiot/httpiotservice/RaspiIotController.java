/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jiot.httpiotservice;

import com.jiot.things.ControlPoint;
import com.jiot.things.ControlPointContainer;
import com.jiot.things.DigitalOutControlPoint;
import com.jiot.things.OutputControlPoint;
import java.util.Observable;
import java.util.Observer;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author yjkim
 */
@RestController
public class RaspiIotController implements Observer {
    
    private ControlPointContainer cpContainer = null;
            
    public RaspiIotController() {
        cpContainer = ControlPointContainer.getInstance();
        cpContainer.start();
        
        for (ControlPoint cp : cpContainer.getControlPoints()) {
            cp.addObserver(this);
        }
        
        System.out.println(">>> Start control point container and register Observer object to control points.");
    }
    
    public void close() {
        if (cpContainer != null) {
            cpContainer.stop();
            System.out.println(">>> Stop control point container.");
        }
    }

    @Override
    public void update(Observable obj, Object arg) {
        if (obj instanceof ControlPoint) {
            ControlPoint cp = (ControlPoint)obj;
            if (arg == null) {
                log("[Observer] Changed value (" + cp.getName() + "): " + cp.getValue());
            }
            else {
                if (arg.toString().equals("name")) {
                    log("[Observer] Changed value (" + cp.getName() + "): " + cp.getName());
                }
                else {
                    log("[Observer] Changed value (" + cp.getName() + "): " + arg);                    
                }
            }
        }
    }
    
    private void log(String msg) {
        System.out.println(">>> " + msg);
    }
    
    @RequestMapping(value="/ledOn/{ledId}", method=RequestMethod.GET)
    @ResponseBody
    public String turnOnLed(@PathVariable("ledId") String ledId) {
        int ledNum = Integer.parseInt(ledId);
        if (ledNum < 1 || ledNum > 3) {
            return "LED ID parameter is wrong. LED ID must be from 1 to 3.";
        }
        else {
            int ledCPId = ControlPointContainer.ControlPointID.LED1.getId() + (ledNum-1);
            OutputControlPoint ledCP = (OutputControlPoint)cpContainer.getControlPoint(ledCPId);
            ledCP.setValue(1);
            return "LED #" + ledId + " is turn on!";
        }
    }
    
    @RequestMapping(value="/ledOff/{ledId}", method=RequestMethod.GET)
    @ResponseBody
    public String turnOffLed(@PathVariable("ledId") String ledId) {
        int ledNum = Integer.parseInt(ledId);
        if (ledNum < 1 || ledNum > 3) {
            return "LED ID parameter is wrong. LED ID must be from 1 to 3.";
        }
        else {
            int ledCPId = ControlPointContainer.ControlPointID.LED1.getId() + (ledNum-1);
            OutputControlPoint ledCP = (OutputControlPoint)cpContainer.getControlPoint(ledCPId);
            ledCP.setValue(0);
            return "LED #" + ledId + " is turn off!";
        }
    }
    
    // Request URL: "/getButton?btn=1"
    @RequestMapping(value="/getButton", method=RequestMethod.GET)
    @ResponseBody
    public String getButton(@RequestParam("btn") String btnId) {
        int btnNum = Integer.parseInt(btnId);
        if (btnNum < 1 || btnNum > 2) {
            return "Button ID parameter is wrong. LED ID must be from 1 to 2.";
        }
        else {
            int btnCPId = ControlPointContainer.ControlPointID.BTN1.getId() + (btnNum-1);
            ControlPoint btnCP = cpContainer.getControlPoint(btnCPId);
            int value = btnCP.getValue();
            return "Button #" + btnId + " Input : " + value + "(" + (value == 0 ? "pressed" : "unpressed") +")";
        }
    }
    
}
