/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.jiot.mcp3208input;

import com.jiot.spi_dev.SPIRPI;
import com.jiot.spi_dev.driver.MCP3208Device;
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
public class MCP3208DriverTest implements Runnable {

    private MCP3208Device adcDev = null;
    
    public MCP3208DriverTest() throws IOException {
        adcDev = new MCP3208Device(SPIRPI.CE1);
        
        System.out.println("MCP3208 ADC device successfully opened...");        
    }
    
    public void close() {
        try {
            if (adcDev != null) adcDev.close();
        } catch (IOException ex) {
            Logger.getLogger(MCP3208DriverTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run() {
        try {
            int value = adcDev.analogRead(0);
            System.out.println("MCP3208 Channel(0): " +  value + ", Voltage: " + adcDev.convertVolts(value) );     // CDS sensor
        } catch (IOException ex) {
            Logger.getLogger(MCP3208DriverTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void start() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture future = executor.scheduleWithFixedDelay(this, 0, 1, TimeUnit.SECONDS);
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    future.cancel(false);
                    executor.shutdown();
                    adcDev.close();
                } catch (IOException ex) {
                    Logger.getLogger(MCP3208DriverTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, 30, TimeUnit.SECONDS);
    }
         
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            MCP3208DriverTest test = new MCP3208DriverTest();
            test.start();
        } catch (IOException ex) {
            Logger.getLogger(MCP3208DriverTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
