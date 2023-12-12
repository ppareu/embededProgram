/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jiot.things;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.GPIOPinConfig;
import jdk.dio.gpio.PinEvent;
import jdk.dio.gpio.PinListener;

/**
 *
 * @author yjkim
 */
public class DigitalInControlPoint extends ControlPoint {
    private int pinId = 0;
    private GPIOPin pinDev = null;

    public DigitalInControlPoint(int pinId) {
        super();
        this.pinId = pinId;
    }

    @Override
    public void open() {
        try {
            GPIOPinConfig gpioConfig = new GPIOPinConfig.Builder()
                .setControllerNumber(0)
                .setPinNumber(pinId)
                .setDirection(GPIOPinConfig.DIR_INPUT_ONLY)
                .setDriveMode(GPIOPinConfig.MODE_INPUT_PULL_UP)
                .setTrigger(GPIOPinConfig.TRIGGER_BOTH_EDGES)
                .build();
            pinDev = DeviceManager.open(gpioConfig);

//            pinDev = DeviceManager.open(pinId, GPIOPin.class);

            presentValue.set(pinDev.getValue() ? 1 : 0);
            setName("GPIO" + pinId);
            
            pinDev.setInputListener(new PinListener() {
                @Override
                public void valueChanged(PinEvent pe) {
                    int oldValue = presentValue.get();
                    int newValue = pe.getValue() ? 1 : 0;
                    if (oldValue != newValue) {
                        presentValue.set(newValue);
                        fireChanged();
                    }
                }
            });
        } catch (IOException ex) {
            Logger.getLogger(DigitalInControlPoint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void close() {
        if (isEnabled()) {
            try {
                pinDev.close();
                pinDev = null;
            } catch (IOException ex) {
                Logger.getLogger(DigitalInControlPoint.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return (pinDev != null && pinDev.isOpen());
    }

    @Override
    public Type getType() {
        return Type.DI;
    }
    
}
