/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.jiot.helloled;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;

/**
 *
 * @author yjkim
 */
public class HelloLED {
    
    public static final String LED_PIN = "GPIO17";
    public static final int INTERVAL = 1000;    // msec

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Blink LED...");
        
        try {
            GPIOPin ledPin = DeviceManager.open(LED_PIN, GPIOPin.class);
            boolean value = false;
            for (int i=0; i<10; i++) {
                ledPin.setValue(value);
                if (value)
                    System.out.println("LED on...");
                else
                    System.out.println("LED off...");
                Thread.sleep(INTERVAL);
                value = !value;
            }
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(HelloLED.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
}
