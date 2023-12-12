/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jiot.i2c_dev;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yjkim
 */
public class I2CUtils {

    public static void delay(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Logger.getLogger(I2CUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void delayNano(int millis, int nanos) {
        try {
            Thread.sleep(millis, nanos);
        } catch (InterruptedException ex) {
            Logger.getLogger(I2CUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static int asInt(byte b) {
        int i = b;
        if (i < 0) {    // -128 <= b < 0 ==> 128 <= i < 256
            i += 256;
        }
        return i;
    }
    
}
