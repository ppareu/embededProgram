/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jiot.labmid;

import java.io.IOException;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;

/**
 *
 * @author comit4
 */
public class Ultrasonic {
   
    private GPIOPin trigPin = null;
    private GPIOPin echoPin = null;
   
    public Ultrasonic(String trigName, String echoName) throws IOException {
        trigPin = DeviceManager.open(trigName, GPIOPin.class);
        echoPin = DeviceManager.open(echoName, GPIOPin.class);
    }
    
    public void close() throws IOException {
        if (trigPin != null && trigPin.isOpen())  trigPin.close();
        if (echoPin != null && echoPin.isOpen())  echoPin.close();
    }
   
    public double getDistance() throws IOException, InterruptedException {
        trigPin.setValue(false);
        Thread.sleep(0, 2000);      // delay 2 usec
        trigPin.setValue(true);
        Thread.sleep(0, 50000);      // delay 10 usec
        trigPin.setValue(false);
       
        while (!echoPin.getValue()) {}
        
        long echoTime = System.currentTimeMillis();
        
        while (echoPin.getValue()) {}
        
        echoTime = System.currentTimeMillis() - echoTime;
        //System.out.println(">>> echo time: " + echoTime);

        return (echoTime/1000.0) * 34300.0 / 2;
    }
   
}
