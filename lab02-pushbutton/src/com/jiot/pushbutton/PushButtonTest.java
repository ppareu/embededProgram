/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.jiot.pushbutton;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;

/**
 *
 * @author yjkim
 */
public class PushButtonTest {
    
    private final String LED_PIN = "GPIO17";
    private final String LED_BTN = "GPIO23";
    private final String EXIT_BTN = "GPIO24";
    
    private GPIOPin ledPin;
    private GPIOPin ledBtnPin;
    private GPIOPin exitBtnPin;
    
    public PushButtonTest() {
        try {
            this.ledPin = DeviceManager.open(LED_PIN, GPIOPin.class);
            this.ledBtnPin = DeviceManager.open(LED_BTN, GPIOPin.class);
            this.exitBtnPin = DeviceManager.open(EXIT_BTN, GPIOPin.class);
        } catch (IOException ex) {
            Logger.getLogger(PushButtonTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void close() {
        try {
            if (ledPin != null && ledPin.isOpen())
                ledPin.close();
            if (ledBtnPin != null && ledBtnPin.isOpen())
                ledBtnPin.close();
            if (exitBtnPin != null && exitBtnPin.isOpen())
                exitBtnPin.close();
        } catch (IOException ex) {
            Logger.getLogger(PushButtonTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void run() {
        boolean exit = false;
        boolean value = false;
        
        while (!exit) {
            try {
                value = !ledBtnPin.getValue();
                ledPin.setValue(value);
                System.out.println("LED: " + (value?"on":"off"));
                
                exit = !exitBtnPin.getValue();
                Thread.sleep(1000);
            } catch (IOException ex) {
                Logger.getLogger(PushButtonTest.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(PushButtonTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        System.out.println("Program exit...");
        close();
    }
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        PushButtonTest test = new PushButtonTest();
        test.run();
    }
    
}
