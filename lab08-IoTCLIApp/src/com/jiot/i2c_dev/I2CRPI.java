/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jiot.i2c_dev;

import static com.jiot.i2c_dev.I2CUtils.asInt;
import java.io.IOException;
import java.nio.ByteBuffer;
import jdk.dio.DeviceManager;
import jdk.dio.i2cbus.I2CDevice;
import jdk.dio.i2cbus.I2CDeviceConfig;

/**
 *
 * @author yjkim
 */
public class I2CRPI {
    
    private I2CDeviceConfig i2cConfig;   
    private I2CDevice i2cDev = null;
    
    public I2CRPI(int i2cAddress) throws IOException {
        i2cConfig = new I2CDeviceConfig.Builder()
                .setAddress(i2cAddress, I2CDeviceConfig.ADDR_SIZE_7)
                .build();
        
        i2cDev = DeviceManager.open(i2cConfig);
    }
    
    public void close() throws IOException {
        if (i2cDev != null && i2cDev.isOpen())
            i2cDev.close();
    }
    
    public int read(int cmd) throws IOException {
        ByteBuffer rxBuf = ByteBuffer.allocateDirect(1);
        i2cDev.read(cmd, 1, rxBuf);
        return asInt(rxBuf.get(0));
    }
    
    public int readShort(int cmd) throws IOException {
        ByteBuffer rxBuf = ByteBuffer.allocateDirect(2);
        i2cDev.read(cmd, 1, rxBuf);
        rxBuf.clear();
        return rxBuf.getShort();
    }
    
    public void write(int cmd) throws IOException {
        ByteBuffer txBuf = ByteBuffer.allocateDirect(1);
        txBuf.put((byte)cmd);
        txBuf.flip();
        i2cDev.write(txBuf);
    }
    
    public void write(int cmd, byte value) throws IOException {
        ByteBuffer txBuf = ByteBuffer.allocateDirect(2);
        txBuf.put((byte)cmd);
        txBuf.put(value);
        txBuf.flip();
        i2cDev.write(txBuf);
    }
    
    public void writeBit(int regAddr, int bitNum, int data) throws IOException {
        int currValue = read(regAddr);
        int mask = 1 << bitNum;
        
        currValue = ((data & mask) != 0) ? (currValue | mask) : (currValue & ~mask);
        
        write(regAddr, (byte)currValue);
    }
    
}
