/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.jiot.mcp3208input;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.spibus.SPIDevice;

/**
 *
 * @author yjkim
 */
public class MCP3208AsyncPollingTest implements Runnable {

    public static final int CMD_BIT = 0x060000;     // 24-bits command 
    
    private SPIDevice spiDev = null;
    
    public MCP3208AsyncPollingTest(String spiDevName) throws IOException {
        spiDev = DeviceManager.open(spiDevName, SPIDevice.class);
        
        System.out.println("SPI comm device successfully opened...");        
    }
    
    public void close() {
        try {
            if (spiDev != null && spiDev.isOpen())
                spiDev.close();
        } catch (IOException ex) {
            Logger.getLogger(MCP3208AsyncPollingTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int analogRead(int channel) throws IOException {
        ByteBuffer out = ByteBuffer.allocate(3);
        ByteBuffer in = ByteBuffer.allocate(3);
        
        int cmd = (channel << 14) | CMD_BIT;
        out.put((byte)((cmd >> 16) & 0xff));
        out.put((byte)((cmd >> 8) & 0xff));
        out.put((byte)(cmd & 0xff));
        out.flip();
        
        spiDev.writeAndRead(out, in);

        int highByte = (int)(in.get(1) & 0x1f);
        int lowByte = (int)(in.get(2) & 0xff);
        
        return (highByte << 8) | lowByte;
    }
    
    public void run() {
        try {
            System.out.println("MCP3208 Channel(0): " + analogRead(0));     // CDS sensor
            Thread.sleep(500);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(MCP3208AsyncPollingTest.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    public void start() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture future = executor.scheduleWithFixedDelay(this, 0, 1, TimeUnit.SECONDS);
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                future.cancel(false);
                executor.shutdown();
                close();
            }
        }, 30, TimeUnit.SECONDS);
    }
     
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            MCP3208AsyncPollingTest test = new MCP3208AsyncPollingTest("SPI0.1");
            test.start();
        } catch (IOException ex) {
            Logger.getLogger(MCP3208AsyncPollingTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
