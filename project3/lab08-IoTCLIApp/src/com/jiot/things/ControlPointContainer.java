/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jiot.things;

import com.jiot.uart_dev.UARTCommCommand;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 * @author yjkim
 */
public class ControlPointContainer {
    private static final AtomicReference<ControlPointContainer> instance =
            new AtomicReference<ControlPointContainer>();
    
    public static ControlPointContainer getInstance() {
        if (instance.get() == null)
            instance.set(new ControlPointContainer());
        return instance.get();
    }
    
    private final Map<Integer, ControlPoint>  controlPoints = 
            new HashMap<Integer, ControlPoint>();
    
    private ControlPointContainer() {
    }
    
    public void start() {
        createAndOpenControlPoints();
    }
    
    public void stop() {
        controlPoints.values().forEach((cp) -> {
            cp.close();
        });
        controlPoints.clear();
        ControlPoint.shutdownExecutor();
    }
    
    public Collection<ControlPoint> getControlPoints() {
        return Collections.unmodifiableCollection(controlPoints.values());
    }
    
    public ControlPoint getControlPoint(int cpId) {
        return controlPoints.get(cpId);
    }
    
    public ControlPoint getControlPoint(String name) {
        ControlPoint point = null;
        
        for (ControlPoint cp : controlPoints.values()) {
            if (cp.getName().equals(name)) {
                point = cp;
                break;
            }
        }
        
        return point;
    }
    
    public void addControlPoint(ControlPoint cp, ControlPointID cpID) {
        controlPoints.put(cp.getId(), cp);
        cp.open();
        cpID.setId(cp.getId());
    }
    
    private void createAndOpenControlPoints() {
        addControlPoint(new DigitalOutControlPoint(17), ControlPointID.LED1);       // LED1 id=0
        addControlPoint(new DigitalOutControlPoint(27), ControlPointID.LED2);       // LED2 id=1
        addControlPoint(new DigitalOutControlPoint(22), ControlPointID.LED3);       // LED3 id=2
        
        addControlPoint(new DigitalInControlPoint(23), ControlPointID.BTN1);        // Button1 id=3
        addControlPoint(new DigitalInControlPoint(24), ControlPointID.BTN2);        // Button2 id=4
        addControlPoint(new DigitalInControlPoint(25), ControlPointID.PIR);         // PIR Motion Sensor id=5
        
        int[] pins = {5, 6};
        addControlPoint(new BusOutControlPoint(pins), ControlPointID.FAN);          // Vantilation Fan id=6

        addControlPoint(new AnalogInControlPoint(1), ControlPointID.CDR);           // CDR sensor id=7
        
        addControlPoint(
            new SerialInControlPoint(
                UARTCommCommand.GET_TMP.get(), 
                UARTCommCommand.GET_TMP.getResponsePrefix()), 
            ControlPointID.TMP);     // Temperature sensor id=8
        addControlPoint(
            new SerialInControlPoint(
                UARTCommCommand.GET_HMD.get(), 
                UARTCommCommand.GET_HMD.getResponsePrefix()), 
            ControlPointID.HMD);     // Humidity sensor id=9
        addControlPoint(
            new SerialInControlPoint(
                UARTCommCommand.GET_ACK.get(), 
                UARTCommCommand.GET_ACK.getResponsePrefix()), 
            ControlPointID.ACK);    // Acknowledgement request id=10
        addControlPoint(new AnalogInControlPoint(2), ControlPointID.SOUND);
        addControlPoint(new AnalogInControlPoint(3), ControlPointID.PI);
        addControlPoint(
            new SerialInControlPoint(
                UARTCommCommand.GET_PSD.get(), 
                UARTCommCommand.GET_PSD.getResponsePrefix()), 
            ControlPointID.PSD); // PSD distance id = 13
    }
    
    public enum ControlPointID {
        LED1(0),
        LED2(1),
        LED3(2),
        BTN1(3),
        BTN2(4),
        PIR(5),
        FAN(6),
        CDR(7),
        TMP(8),
        HMD(9),
        ACK(10),
        SOUND(11),
        PI(12),
        PSD(13);
        
        private int id;
        
        private ControlPointID(int id) {
            this.id = id;
        }
        
        public int getId() {
            return this.id;
        }
        
        public void setId(int id) {
            this.id = id;
        }
    }    
}
