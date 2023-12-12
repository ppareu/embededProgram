/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.jiot.pcf8591bbtest;

import com.jiot.i2c_dev.PCF8591Command;
import com.jiot.i2c_dev.driver.PCF8591Device;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yjkim
 */
public class PCF8591BBTest implements Runnable {

    private PCF8591Device adConverter = null;
    private ScheduledExecutorService executor = null;
    private int pin = 0; 
    
    public PCF8591BBTest() throws IOException {
        adConverter = new PCF8591Device();
        executor = Executors.newSingleThreadScheduledExecutor();
        
        System.out.println("PCF8591 device successfully opened...");        
    }
    
    public void close() {
        try {
            if (adConverter != null) adConverter.close();
        } catch (IOException ex) {
            Logger.getLogger(PCF8591BBTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run() {
        try {
            int value = adConverter.analogRead(pin);
            System.out.println("PCF8591BB Channel(" + pin +"): " +  value + ", Voltage: " + adConverter.convertVolts(value) );
        } catch (IOException ex) {
            Logger.getLogger(PCF8591BBTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        pin = ++pin % 3;    
    }
    
    public void start() throws IOException {
        ScheduledFuture future1 = executor.scheduleWithFixedDelay(this, 0, 500, TimeUnit.MILLISECONDS);
        
        ScheduledFuture future2 = executor.scheduleWithFixedDelay(new Runnable() {
            private int pwm = 0;
            private int step = 5;
            
            @Override
            public void run() {
                pwm += step;
                if (pwm > 255) {
                    pwm = 255;
                    step *= -1;
                }
                else if (pwm < 0) {
                    pwm = 0;
                    step *= -1;
                }
                try {
                    adConverter.write(PCF8591Command.AOUT.get(), (byte) pwm);
                } catch (IOException ex) {
                    Logger.getLogger(PCF8591BBTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
        
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    future1.cancel(false);
                    future2.cancel(false);
                    executor.shutdown();
                    adConverter.close();
                } catch (IOException ex) {
                    Logger.getLogger(PCF8591BBTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, 60, TimeUnit.SECONDS);
    }
         
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            PCF8591BBTest test = new PCF8591BBTest();
            test.start();
        } catch (IOException ex) {
            Logger.getLogger(PCF8591BBTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
