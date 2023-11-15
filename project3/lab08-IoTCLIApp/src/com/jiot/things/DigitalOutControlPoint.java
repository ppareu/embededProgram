/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jiot.things;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.GPIOPinConfig;

/**
 *
 * @author yjkim
 */
public class DigitalOutControlPoint extends OutputControlPoint {
    private int pinId;
    private GPIOPin pinDev = null;

    public DigitalOutControlPoint(int pinId) {
        super();
        this.pinId = pinId;
        this.presentValue.set(0);
    }
    
    public DigitalOutControlPoint(int pinId, int initValue) {
        super();
        this.pinId = pinId;
        this.presentValue.set(initValue);
    }
    
    @Override
    public void open() {
        try {
            GPIOPinConfig gpioConfig = new GPIOPinConfig.Builder()
                .setControllerNumber(0)
                .setPinNumber(pinId)
                .setDirection(GPIOPinConfig.DIR_OUTPUT_ONLY)
                .setDriveMode(GPIOPinConfig.MODE_OUTPUT_PUSH_PULL)
                .setInitValue(getValue() == 0 ? false : true)
                .build();
            pinDev = DeviceManager.open(gpioConfig);

//            pinDev = DeviceManager.open(pinId, GPIOPin.class);

            setName("GPIO" + pinId);
        } catch (IOException ex) {
            Logger.getLogger(DigitalOutControlPoint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void close() {
        if (isEnabled()) {
            try {
                pinDev.close();
                pinDev = null;
            } catch (IOException ex) {
                Logger.getLogger(DigitalOutControlPoint.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return (pinDev != null && pinDev.isOpen());
    }

    @Override
    public Type getType() {
        return Type.DO;
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
            pinDev.setValue(value == 1);
            presentValue.set(value);
            success = true;
        } catch (IOException ex) {
            Logger.getLogger(DigitalOutControlPoint.class.getName()).log(Level.SEVERE, null, ex);
        }
        return success;
    }

}
