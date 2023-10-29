/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jiot.spi_dev;

import java.io.IOException;
import java.nio.ByteBuffer;
import jdk.dio.DeviceManager;
import jdk.dio.spibus.SPIDevice;
import jdk.dio.spibus.SPIDeviceConfig;

/**
 *
 * @author comit4
 */
public class SPIRPI {
    private SPIDeviceConfig spiConfig;
    private SPIDevice spiDev = null;
    
    public static final int CE0 = 0;
    public static final int CE1 = 1;
    
    public SPIRPI (int address, int bitOrder) throws IOException {
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
    }
    
    public void close() throws IOException {
        if(spiDev != null && spiDev.isOpen()) {
            spiDev.close();
        }
    }
    
    public void writeAndRead(ByteBuffer out, ByteBuffer in) throws IOException {
        spiDev.writeAndRead(out, in);
    }
}
