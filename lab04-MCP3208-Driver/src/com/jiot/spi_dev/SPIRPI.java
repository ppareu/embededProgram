/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jiot.spi_dev;

import java.io.IOException;
import java.nio.ByteBuffer;
import jdk.dio.DeviceManager;
//import jdk.dio.gpio.GPIOPin;
//import jdk.dio.gpio.GPIOPinConfig;
import jdk.dio.spibus.SPIDevice;
import jdk.dio.spibus.SPIDeviceConfig;

/**
 *
 * @author yjkim
 */
public class SPIRPI {
    
    private SPIDeviceConfig spiConfig;
//    private GPIOPinConfig gpioConfig;
   
    private SPIDevice spiDev = null;
//    private GPIOPin ssPin = null;     // not used in DIO v.1.2
    
    public static final int CE0 = 0;   // SPI address 0
    public static final int CE1 = 1;   // SPI address 0

    public SPIRPI(int address, int bitOrder) throws IOException {
        spiConfig = new SPIDeviceConfig.Builder()
                .setControllerNumber(0)
                .setAddress(address)
                .setCSActiveLevel(SPIDeviceConfig.CS_ACTIVE_LOW)
                .setClockFrequency(2000000)
                .setClockMode(1)
                .setWordLength(8)
                .setBitOrdering(bitOrder)
                .build();
        
        spiDev = DeviceManager.open(spiConfig);
        
//        gpioConfig = new GPIOPinConfig.Builder()
//                .setControllerNumber(0)
//                .setPinNumber((address == CE0) ? 8 : 7)
//                .setDirection(GPIOPinConfig.DIR_OUTPUT_ONLY)
//                .setDriveMode(GPIOPinConfig.MODE_OUTPUT_PUSH_PULL)
//                .setInitValue(true)
//                .build();
//        
//        ssPin = DeviceManager.open(gpioConfig);
    }
    
    public void close() throws IOException {
        if (spiDev != null && spiDev.isOpen())
            spiDev.close();
//        if (ssPin != null && ssPin.isOpen())
//            ssPin.close();
    }
    
    public void writeAndRead(ByteBuffer out, ByteBuffer in) throws IOException {
//        ssPin.setValue(false);      // enable MCP3208 device
        spiDev.writeAndRead(out, in);
//        ssPin.setValue(true);       // disable MCP3208 device
    }
    
}
