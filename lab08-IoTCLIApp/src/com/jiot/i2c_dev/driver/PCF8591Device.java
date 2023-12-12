/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jiot.i2c_dev.driver;

import java.io.IOException;
import com.jiot.i2c_dev.PCF8591Command;
import com.jiot.i2c_dev.I2CRPI;

/**
 *
 * @author yjkim
 */
public class PCF8591Device extends I2CRPI {
    
    private static final int PCF8591_ADDR = 0x48;
    
    public PCF8591Device() throws IOException {
        super(PCF8591_ADDR);
        write(0x00);
    }
    
    public int analogRead(int pin) throws IOException {
        int value = -1;
        
        switch (pin) {
        case 0: 
            value = read(PCF8591Command.AIN0.get()); break;
        case 1: 
            value = read(PCF8591Command.AIN1.get()); break;
        case 2: 
            value = read(PCF8591Command.AIN2.get()); break;
        case 3: 
            value = read(PCF8591Command.AIN3.get()); break;
        }
        
        return value;
    }
    
    public void analogWrite(int pwm) throws IOException {
        write(PCF8591Command.AOUT.get(), (byte)pwm);
    }
    
    public float convertVolts(int value) {
        return (float)(value * 3.3) / (float)255;
    }

}
