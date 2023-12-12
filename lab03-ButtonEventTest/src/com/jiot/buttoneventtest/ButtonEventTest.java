/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.jiot.buttoneventtest;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.PinEvent;
import jdk.dio.gpio.PinListener;

/**
 *
 * @author yjkim
 */
public class ButtonEventTest implements Runnable {

    private final String LED_PIN = "GPIO17";
    private final String LED_BTN = "GPIO23";
    private final String EXIT_BTN = "GPIO24";
    private final String PIR_PIN = "GPIO25";
    
    private GPIOPin ledPin;
    private GPIOPin ledBtnPin;
    private GPIOPin exitBtnPin;
    private GPIOPin pirPin;
    
    private volatile boolean toggleStop = false, exit = false;
    private boolean prevToggleStop = false;

    public ButtonEventTest() throws IOException {
        try {
            this.ledPin = DeviceManager.open(LED_PIN, GPIOPin.class);
            this.ledBtnPin = DeviceManager.open(LED_BTN, GPIOPin.class);
            this.exitBtnPin = DeviceManager.open(EXIT_BTN, GPIOPin.class);
            this.pirPin = DeviceManager.open(PIR_PIN, GPIOPin.class);
        } catch (IOException ex) {
            Logger.getLogger(ButtonEventTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ledBtnPin.setInputListener(new PinListener() {
            @Override
            public void valueChanged(PinEvent pe) {
                if (!pe.getValue()) {
                    toggleStop = !toggleStop;
                }
            }
        });
        
        exitBtnPin.setInputListener(new PinListener() {
            @Override
            public void valueChanged(PinEvent pe) {
                if (!pe.getValue()) {
                    exit = true;
                }
            }
        });
        
        pirPin.setInputListener(new PinListener() {
            @Override
            public void valueChanged(PinEvent pe) {
                if (!pe.getValue()) {
                    toggleStop = !toggleStop;
                }
            }
        });
        
        System.out.println("LED & Button devices successfully opened...");
    }
    
    public void close() {
        try {
            if (ledPin != null && ledPin.isOpen())
                ledPin.close();
            if (ledBtnPin != null && ledBtnPin.isOpen())
                ledBtnPin.close();
            if (exitBtnPin != null && exitBtnPin.isOpen())
                exitBtnPin.close();
            if (pirPin != null && pirPin.isOpen())
                pirPin.close();
        } catch (IOException ex) {
            Logger.getLogger(ButtonEventTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void run() {
        System.out.println("Start LED toggling...");
        
        while (!exit) {
            if (prevToggleStop != toggleStop) {
                System.out.printf("%s LED toggling...\n", toggleStop ? "Stop" : "Restart");
                prevToggleStop = toggleStop;
            }
            
            try {
                if (!toggleStop) {
                    ledPin.setValue(true);
                    Thread.sleep(500);

                    ledPin.setValue(false);
                    Thread.sleep(500);
                }
            } catch (InterruptedException | IOException ex) {
                Logger.getLogger(ButtonEventTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        System.out.println("Program exit...");
        close();
    }
     
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Thread t = new Thread(new ButtonEventTest());
            t.start();
        } catch (IOException ex) {
            Logger.getLogger(ButtonEventTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
