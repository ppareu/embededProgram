/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jiot.gpio_dev.driver;

import java.io.IOException;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.GPIOPinConfig;
import jdk.dio.gpio.PinListener;

/**
 *
 * @author yjkim
 */
public class ButtonDevice {
    
    public static final int PULL_UP_INPUT = GPIOPinConfig.MODE_INPUT_PULL_UP;
    public static final int PULL_DOWN_INPUT = GPIOPinConfig.MODE_INPUT_PULL_DOWN;
    public static final int TRIGGER_NONE = GPIOPinConfig.TRIGGER_NONE;
    public static final int TRIGGER_RISING_EDGE = GPIOPinConfig.TRIGGER_RISING_EDGE;
    public static final int TRIGGER_FALLING_EDGE = GPIOPinConfig.TRIGGER_FALLING_EDGE;

    private GPIOPin pin = null;
    
    public ButtonDevice(int pinNum) throws IOException {
        init(pinNum, PULL_UP_INPUT, TRIGGER_NONE);
    }

    public ButtonDevice(int pinNum, int mode, int trigger) throws IOException {
        init(pinNum, mode, trigger);
    }
    
    private void init(int pinNum, int mode, int trigger) throws IOException {
        GPIOPinConfig gpioConfig = new GPIOPinConfig.Builder()
                .setControllerNumber(0)
                .setPinNumber(pinNum)
                .setDirection(GPIOPinConfig.DIR_INPUT_ONLY)
                .setDriveMode(mode)
                .setTrigger(trigger)
                .build();
        
        pin = DeviceManager.open(gpioConfig);
    }
    
    public boolean read() throws IOException {
        return pin.getValue();
    }
    
    public void setTrigger(int trigger) throws IOException {
        if (pin != null && pin.isOpen()) {
            pin.setTrigger(trigger);
        }
    }
    
    public void setInputListener(PinListener pl) throws IOException {
        if (pin != null && pin.isOpen()) {
            if (pin.getTrigger() != TRIGGER_NONE) {
                pin.setInputListener(pl);
            }
        }
    }
    
}
