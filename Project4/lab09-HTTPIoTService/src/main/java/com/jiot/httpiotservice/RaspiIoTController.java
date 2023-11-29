/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jiot.httpiotservice;

import com.jiot.things.AnalogInControlPoint;
import com.jiot.things.BusOutControlPoint;
import com.jiot.things.ControlPoint;
import com.jiot.things.ControlPointContainer;
import com.jiot.things.FloatInputSupport;
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
 * @author comit4
 */
@RestController
public class RaspiIoTController implements Observer{
    private ControlPointContainer cpContainer = null;
    
    public RaspiIoTController() {
        cpContainer = ControlPointContainer.getInstance();
        cpContainer.start();
        
        for (ControlPoint cp : cpContainer.getControlPoints()) {
            cp.addObserver(this);
        }
        
        System.out.println(">>> Start control point container...");
    }

    private void log(String msg) {
        System.out.println(">>> " + msg);
    }
    
    @Override
    public void update(Observable obj, Object arg) {    
        if (obj instanceof ControlPoint) {
            ControlPoint cp = (ControlPoint)obj;
            if (arg == null) {
                log("[Observer] Value Changed (" + cp.getName() + ") : " + cp.getValue());
            } else {
                if (arg.toString().equals("name")) {
                    log("[Observer] Name Changed (" + cp.getName() + ") : " + cp.getName());
                } else {
                    log("[Observer] Value Changed (" + cp.getName() + ") : " + arg);
                }
            }
        }
    }
    
    @RequestMapping(value = "/ledOn/{ledId}", method = RequestMethod.GET)
    @ResponseBody
    public String turnOnLed (@PathVariable("ledId") int ledNum) {
        if (ledNum < 1 || ledNum > 3) {
            return "LED ID parameter is wrong. LED ID must be form 1 to 3.";
        } else {
            int ledCPId = ControlPointContainer.ControlPointID.LED1.getId() + (ledNum-1);
            OutputControlPoint cp = (OutputControlPoint)cpContainer.getControlPoint(ledCPId);
            cp.setValue(1);
            return "LED #" + ledNum + " is turn on.";
        }
    }
    
    @RequestMapping(value = "/ledOff/{ledId}", method = RequestMethod.GET)
    @ResponseBody
    public String turnOffLed (@PathVariable("ledId") int ledNum) { // @PathVariable
        if (ledNum < 1 || ledNum > 3) {
            return "LED ID parameter is wrong. LED ID must be form 1 to 3.";
        } else {
            int ledCPId = ControlPointContainer.ControlPointID.LED1.getId() + (ledNum - 1);
            OutputControlPoint cp = (OutputControlPoint)cpContainer.getControlPoint(ledCPId);
            cp.setValue(0);
            return "LED #" + ledNum + " is turn off.";
        }
    }
    
    // request URL : /getButton?btn=1
    @RequestMapping(value = "/getButton", method = RequestMethod.GET)
    @ResponseBody
    public String getButton(@RequestParam("btn") int btnNum) { // @RequestParam 입력 파라메타
        if (btnNum < 1 || btnNum > 2) {
            return "Button Id parameter is wrong. Button id must be from 1 to 2";
        } else {
            int btnCPId = ControlPointContainer.ControlPointID.BTN1.getId() + (btnNum - 1);
            ControlPoint cp = cpContainer.getControlPoint(btnCPId);
            int value = cp.getValue();
            return "Button ID #" + btnNum + " : " + value + "(" + ((value == 0) ? "pressed" : "unpressed") + ")";
        }
    }
    
    @RequestMapping(value = "/temp", method = RequestMethod.GET)
    @ResponseBody
    public String getTemperature() {
        int tempCPId = ControlPointContainer.ControlPointID.TMP.getId();
        ControlPoint cp = cpContainer.getControlPoint(tempCPId);
        float value = ((FloatInputSupport)cp).getFloatValue();
        return "Current Temperature : " + value;
    }
    
    @RequestMapping(value = "/humd", method = RequestMethod.GET)
    @ResponseBody
    public String getHumidity() {
        int humdCPId = ControlPointContainer.ControlPointID.HMD.getId();
        ControlPoint cp = cpContainer.getControlPoint(humdCPId);
        float value = ((FloatInputSupport)cp).getFloatValue();
        return "Current Humidity : " + value;
    }
    
    @RequestMapping(value = "/fanOn", method = RequestMethod.GET)
    @ResponseBody
    public String turnOnFan () {
        int start = 6;
        int fanCPId = ControlPointContainer.ControlPointID.FAN.getId();
        BusOutControlPoint cp = (BusOutControlPoint)cpContainer.getControlPoint(fanCPId);
        cp.setValue(start);
        System.out.println("value >>> " + cp); // setValue 동작 값
        return "Fan is turn on.";
    }
    
    @RequestMapping(value = "/fanOff", method = RequestMethod.GET)
    @ResponseBody
    public String turnOffFan () {
        int stop = 0;
        int fanCPId = ControlPointContainer.ControlPointID.FAN.getId();
        BusOutControlPoint cp = (BusOutControlPoint)cpContainer.getControlPoint(fanCPId);
        cp.setValue(stop);
        return "Fan is turn off.";
    }
    
    @RequestMapping(value = "/move", method = RequestMethod.GET)
    @ResponseBody
    public String getMove() {
        int pirCPId = ControlPointContainer.ControlPointID.PIR.getId();
        ControlPoint cp = cpContainer.getControlPoint(pirCPId);
        int value = cp.getValue();
        return "PIR : " + value + " (" + ((value == 0) ? "no motion detection" : "motion detection") + ")";
    }
    
    @RequestMapping(value = "/light", method = RequestMethod.GET)
    @ResponseBody
    public String getLight() {
        int lightCPId = ControlPointContainer.ControlPointID.CDR.getId();
        AnalogInControlPoint cp = (AnalogInControlPoint)cpContainer.getControlPoint(lightCPId);
        int value = cp.getValue();
        return "Current Light : " + value;
    }
 
}
