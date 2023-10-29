/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jiot.labmid;

import java.io.IOException;
import java.util.concurrent.Executors;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author comit4
 */
public class BUZZER {
    private GPIOPin buzzerPin = null;
    private ScheduledExecutorService executor = null;
    private ScheduledFuture future = null;
    
    public BUZZER(String buzzerName) throws IOException {
        buzzerPin = DeviceManager.open(buzzerName, GPIOPin.class);
        executor = Executors.newSingleThreadScheduledExecutor();
    }
    
    public void close() throws IOException{
        if(executor != null) executor.shutdown();
        if(buzzerPin != null && buzzerPin.isOpen()) buzzerPin.close();
    }
    
    public void play(){
        if(future != null){
            return;
        }

        future = executor.scheduleWithFixedDelay(new Runnable(){
            @Override
            public void run() {
                try {
                    buzzerPin.setValue(!buzzerPin.getValue());
                } catch (IOException ex) {
                    Logger.getLogger(BUZZER.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }, 0, 1095, TimeUnit.MICROSECONDS);
    }
    
    public void stop(){
        if(future != null){
            future.cancel(false);
            future = null;
        }
    }
}
