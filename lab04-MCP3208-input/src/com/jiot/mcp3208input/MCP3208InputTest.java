/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.jiot.mcp3208input;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.spibus.SPIDevice;

/**
 *
 * @author yjkim
 */
public class MCP3208InputTest implements Runnable {

    public static final int CMD_BIT = 0x060000;     // 24-bits command 
    
    private SPIDevice spiDev = null;
    private GPIOPin csPin = null;
    
    public MCP3208InputTest(String spiDevName, String csPinName) throws IOException {
        spiDev = DeviceManager.open(spiDevName, SPIDevice.class);
//        csPin = DeviceManager.open(csPinName, GPIOPin.class);
        
        System.out.println("SPI comm device successfully opened...");        
    }
    
    public void close() {
        try {
            if (spiDev != null && spiDev.isOpen())
                spiDev.close();
        } catch (IOException ex) {
            Logger.getLogger(MCP3208InputTest.class.getName()).log(Level.SEVERE, null, ex);
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
        
//        csPin.setValue(false);
        spiDev.writeAndRead(out, in);
//        csPin.setValue(true);

        int highByte = (int)(in.get(1) & 0x1f);
        int lowByte = (int)(in.get(2) & 0xff);
        
        return (highByte << 8) | lowByte;
    }
    
    public void run() {
        for (;;) {
            try {
                System.out.println("MCP3208 Channel(0): " + analogRead(0));     // CDS sensor
                Thread.sleep(500);
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(MCP3208InputTest.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
    }
     
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Thread t = new Thread(new MCP3208InputTest("SPI0.1", "GPIO7"));
            t.start();
        } catch (IOException ex) {
            Logger.getLogger(MCP3208InputTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
