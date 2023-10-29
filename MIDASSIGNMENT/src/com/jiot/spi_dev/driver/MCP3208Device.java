/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jiot.spi_dev.driver;

import com.jiot.spi_dev.MCP3208Command;
import com.jiot.spi_dev.SPIRPI;
import com.jiot.spi_dev.SPIUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import jdk.dio.Device;

/**
 *
 * @author comit4
 */
public class MCP3208Device extends SPIRPI {
    
    public MCP3208Device(int address) throws IOException {
        super(address, Device.BIG_ENDIAN);
    }
    
    public int analogRead(int channel) throws IOException {
        int value = -1;
        
        if(channel < 0 || channel > 7) return value;
        
        ByteBuffer out = ByteBuffer.allocate(3);
        ByteBuffer in = ByteBuffer.allocate(3);
        
        int cmd = MCP3208Command.REDA_CHANNAL.get(channel);
        out.put((byte)((cmd >> 16) & 0xff));
        out.put((byte)((cmd >> 8) & 0xff));
        out.put((byte)(cmd & 0xff));
        out.flip();
        
        writeAndRead(out, in);
        
        value = ((in.get(1) & 0x1f) << 8) | SPIUtils.asInt(in.get(2));
        
        return value;
    }
    
    public float convertVolts(int value) {
        return (float)(value * 5.0) / (float)(4095.0);
    }
}
