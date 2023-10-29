/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jiot.spi_dev;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author comit4
 */
public class SPIUtils {
    public static void delay(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Logger.getLogger(SPIUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void delayNano(int millis, int nanos) {
        try {
            Thread.sleep(millis, nanos);
        } catch (InterruptedException ex) {
            Logger.getLogger(SPIUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static int asInt(byte b) {
        int i = b; 
        /*  
        byte : 8bits - unsigned int 0 ~ 255
        char : 8bits signed int -128 ~ 127
        */
        
        if (i < 0) {
           i += 256; 
        }
        return i;
    }
}
