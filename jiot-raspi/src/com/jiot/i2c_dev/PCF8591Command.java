/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.jiot.i2c_dev;

/**
 *
 * @author yjkim
 */
public enum PCF8591Command {
    
    // Analog Input Channel 0
    AIN0(0x00),
    
    // Analog Input Channel 1
    AIN1(0x01),
    
    // Analog Input Channel 2
    AIN2(0x02),
    
    // Analog Input Channel 3
    AIN3(0x03),
    
    // Analog Output 
    AOUT(0x40);
    
    private int cmd;
    
    private PCF8591Command(int cmd) {
        this.cmd = cmd;
    }
    
    public int get() {
        return this.cmd;
    }
    
}
