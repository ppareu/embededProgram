/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jiot.labmid;

/**
 *
 * @author comit4
 */
public class TEXT_LCD_Command {
    // commands
    public static final int LCD_CLEARDISPLAY = 0x01;
    public static final int LCD_RETURNHOME = 0x02;
    public static final int LCD_ENTRYMODESET = 0x04;
    public static final int LCD_DISPLAYCONTROL = 0x08;
    public static final int LCD_CURSORSHIFT = 0x10;
    public static final int LCD_FUNCTIONSET = 0x20;
    public static final int LCD_SETCGRAMADDR = 0x40;
    public static final int LCD_SETDDRAMADDR = 0x80;

    // flags for display entry mode
    public static final int LCD_ENTRYRIGHT = 0x00;
    public static final int LCD_ENTRYLEFT = 0x02;
    public static final int LCD_ENTRYSHIFTINCREMENT = 0x01;
    public static final int LCD_ENTRYSHIFTDECREMENT = 0x00;

    // flags for display on/off control
    public static final int LCD_DISPLAYON = 0x04;
    public static final int LCD_DISPLAYOFF = 0x00;
    public static final int LCD_CURSORON = 0x02;
    public static final int LCD_CURSOROFF = 0x00;
    public static final int LCD_BLINKON = 0x01;
    public static final int LCD_BLINKOFF = 0x00;

    // flags for display/cursor shift
    public static final int LCD_DISPLAYMOVE = 0x08;
    public static final int LCD_CURSORMOVE = 0x00;
    public static final int LCD_MOVERIGHT = 0x04;
    public static final int LCD_MOVELEFT = 0x00;

    // flags for function set
    public static final int LCD_8BITMODE = 0x10;
    public static final int LCD_4BITMODE = 0x00;
    public static final int LCD_2LINE = 0x08;
    public static final int LCD_1LINE = 0x00;
    public static final int LCD_5x10DOTS = 0x04;
    public static final int LCD_5x8DOTS = 0x00;

    // flags for backlight control
    public static final int LCD_BACKLIGHT = 0x08;
    public static final int LCD_NOBACKLIGHT = 0x00;
    
//    public static final int En = B00000100;  // Enable bit
//    public static final int Rw = B00000010; // Read/Write bit
//    public static final int Rs = B00000001;
    
    public void begin() {
        
    }
    public void clear() {
        
    }
    public void home() {
        
    }
    public void noDisplay() {
        
    }
    public void display() {
        
    }
    public void noBlink() {
        
    }
    public void blink() {
        
    }
    public void noCursor() {
        
    }
    public void cursor() {
        
    }
    public void scrollDisplayLeft() {
        
    }
    public void scrollDisplayRight(){
        
    }
    public void printLeft() {
        
    }
    public void printRight() {
        
    }
    public void leftToRight() {
        
    }
    public void rightToLeft() {
        
    }
    public void shiftIncrement() {
        
    }
    public void shiftDecrement() {
        
    }
    public void noBacklight() {
        
    }
    public void backlight() {
        
    }
    public void autoscroll() {
        
    }
    public void noAutoscroll() {
    
}
    public void createChar() {
        
    }
    public void setCursor() {
        
    }
    public void setDelayTime(int t) {
        
    }
    public void printHangul() {
    
}
}
