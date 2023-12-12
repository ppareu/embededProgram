/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jiot.things;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.GPIOPinConfig;

/**
 *
 * @author yjkim
 */
public class BusOutControlPoint extends OutputControlPoint {

    private int[] pinIds;
    private int pins = 0;
    private ArrayList<GPIOPin> pinDevs = null;

    public BusOutControlPoint(int[] pinIds) {
        super();
        this.pinIds = pinIds;
        this.pins = pinIds.length;
        this.pinDevs = this.pins != 0 ? new ArrayList<GPIOPin>() : null;
    }
    
    @Override
    public void open() {
        try {
            for (int i=0; i<pins; i++) {
                GPIOPin pinDev = DeviceManager.open(new GPIOPinConfig.Builder()
                                        .setControllerNumber(0)
                                        .setPinNumber(pinIds[i])
                                        .setDirection(GPIOPinConfig.DIR_OUTPUT_ONLY)
                                        .setDriveMode(GPIOPinConfig.MODE_OUTPUT_PUSH_PULL)
                                        .setInitValue(false)
                                        .build());
                pinDevs.add(i, pinDev);
            }
//            for (int i=0; i<pins; i++) {
//                    pinDevs.add(i, DeviceManager.open(pinIds[i], GPIOPin.class));
//            }
            setName("BusOut_" + getId());
        } catch (IOException ex) {
            Logger.getLogger(BusOutControlPoint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void close() {
        if (isEnabled()) {
            pinDevs.forEach((pin) -> {
                try {
                    pin.close();
                } catch (IOException ex) {
                    Logger.getLogger(BusOutControlPoint.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            pinDevs.clear();
            pinDevs = null;
        }
    }

    @Override
    public boolean isEnabled() {
        return (pinDevs != null && pinDevs.get(0).isOpen());
    }

    @Override
    public Type getType() {
        return Type.BO;
    }
    
    @Override
    public void setValue(int value) {
        int oldValue = getValue();
        
        if (writeValue(value) && oldValue != getValue()) {
            fireChanged();
        }
    }
    
    private boolean writeValue(int value) {
        boolean success = false;        
        try {
            int mask = 0;
            for (int i=0; i<pins; i++) {
                mask = 1 << (pins-(i+1));
                pinDevs.get(i).setValue((value & mask) != 0);
            }
            presentValue.set(value);
            success = true;
        } catch (IOException ex) {
            Logger.getLogger(BusOutControlPoint.class.getName()).log(Level.SEVERE, null, ex);
        }
        return success;
    }    
}
