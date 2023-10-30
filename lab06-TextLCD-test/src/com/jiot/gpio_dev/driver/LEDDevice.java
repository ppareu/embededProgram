/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jiot.gpio_dev.driver;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.GPIOPinConfig;

/**
 *
 * @author yjkim
 */
public class LEDDevice {
    
    private GPIOPin pin = null;
    private boolean activeLevel = true;
    
    private ScheduledExecutorService executor = null;
    private ScheduledFuture blinkFuture = null;
      
    public LEDDevice(int pinNum, boolean activeLevel) throws IOException {
        GPIOPinConfig gpioConfig = new GPIOPinConfig.Builder()
                .setControllerNumber(0)
                .setPinNumber(pinNum)
                .setDirection(GPIOPinConfig.DIR_OUTPUT_ONLY)
                .setDriveMode(GPIOPinConfig.MODE_OUTPUT_PUSH_PULL)
                .setInitValue(!activeLevel)
                .build();
        
        pin = DeviceManager.open(gpioConfig);
        this.activeLevel = activeLevel;
        
        executor = Executors.newSingleThreadScheduledExecutor();
    }
    
    public void close() throws IOException {
        if (blinkFuture != null)  blinkFuture.cancel(false);
        if (executor != null)  executor.shutdown();
        
        if (pin != null && pin.isOpen()) {
            pin.close();
        }
    }
    
    public void on() throws IOException {
        pin.setValue(activeLevel);
    }
    
    public void off() throws IOException {
        pin.setValue(!activeLevel);
    }
    
    public void toggle() throws IOException {
        pin.setValue(!pin.getValue());
    }
    
    public void blink(int millis) {
        if (millis == 0) {
            if (blinkFuture != null) {
                blinkFuture.cancel(false);
                blinkFuture = null;
            }
        }
        else {
            if (blinkFuture == null) {
                executor.scheduleWithFixedDelay(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            pin.setValue(!pin.getValue());
                        } catch (IOException ex) {
                            Logger.getLogger(LEDDevice.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }, 0, millis, TimeUnit.MILLISECONDS);
            }
        }
    }
    
}
