/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jiot.gpio_dev.driver;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.GPIOPinConfig;

/**
 *
 * @author yjkim
 */
// When the display powers up, it is configured as follows:
//
// 1. Display clear
// 2. Function set: 
//    DL = 1; 8-bit interface data 
//    N = 0; 1-line display 
//    F = 0; 5x8 dot character font 
// 3. Display on/off control: 
//    D = 0; Display off 
//    C = 0; Cursor off 
//    B = 0; Blinking off 
// 4. Entry mode set: 
//    I/D = 1; Increment by 1 
//    S = 0; No shift 
//
// Note, however, that resetting the Arduino doesn't reset the LCD, so we
// can't assume that it's in that state when a sketch starts (and the
// LiquidCrystal constructor is called).

public class TextLCDDevice {

    private GPIOPin rsPin = null;        // LOW: command. HIGH: character.
    private GPIOPin rwPin = null;        // LOW: write to LCD. HIGH: read from LCD.
    private GPIOPin enablePin = null;    // activated by a HIGH pulse.
    private GPIOPin[] dataPins;

    private int displayfunction;
    private int displaycontrol;
    private int displaymode;

    private int numlines;
    private int[] row_offsets;


    public TextLCDDevice(int rs, int rw, int enable,
                  int d0, int d1, int d2, int d3,
                  int d4, int d5, int d6, int d7) throws IOException {
        init(false, rs, rw, enable, d0, d1, d2, d3, d4, d5, d6, d7);
    }

    public TextLCDDevice(int rs, int enable,
                  int d0, int d1, int d2, int d3,
                  int d4, int d5, int d6, int d7) throws IOException {
        init(false, rs, 0, enable, d0, d1, d2, d3, d4, d5, d6, d7);
    }

    public TextLCDDevice(int rs, int rw, int enable,
                  int d0, int d1, int d2, int d3) throws IOException {
        init(true, rs, rw, enable, d0, d1, d2, d3, 0, 0, 0, 0);
    }

    public TextLCDDevice(int rs, int enable,
                  int d0, int d1, int d2, int d3) throws IOException {
        init(true, rs, 0, enable, d0, d1, d2, d3, 0, 0, 0, 0);
    }
    
    public void init(boolean fourbitmode, int rs, int rw, int enable,
                    int d0, int d1, int d2, int d3,
                    int d4, int d5, int d6, int d7) throws IOException
    {
        rsPin = openByDeviceConfig(rs, false);
        rwPin = (rw != 0) ? openByDeviceConfig(rw, false) : null;
        enablePin = openByDeviceConfig(enable, false);

        dataPins = new GPIOPin[8];
        if (fourbitmode) {
            dataPins[0] = (d0 != 0) ? openByDeviceConfig(d0, false) : null;
            dataPins[1] = (d1 != 0) ? openByDeviceConfig(d1, false) : null;
            dataPins[2] = (d2 != 0) ? openByDeviceConfig(d2, false) : null;
            dataPins[3] = (d3 != 0) ? openByDeviceConfig(d3, false) : null;
            dataPins[4] = null;
            dataPins[4] = null;
            dataPins[6] = null;
            dataPins[7] = null;

            displayfunction = TextLCDCommand.BITMODE4.get() | 
                              TextLCDCommand.LINE1.get() | 
                              TextLCDCommand.DOTS5x8.get();
        }
        else {
            dataPins[0] = (d0 != 0) ? openByDeviceConfig(d0, false) : null;
            dataPins[1] = (d1 != 0) ? openByDeviceConfig(d1, false) : null;
            dataPins[2] = (d2 != 0) ? openByDeviceConfig(d2, false) : null;
            dataPins[3] = (d3 != 0) ? openByDeviceConfig(d3, false) : null;
            dataPins[4] = (d4 != 0) ? openByDeviceConfig(d4, false) : null;
            dataPins[4] = (d5 != 0) ? openByDeviceConfig(d5, false) : null;
            dataPins[6] = (d6 != 0) ? openByDeviceConfig(d6, false) : null;
            dataPins[7] = (d7 != 0) ? openByDeviceConfig(d7, false) : null;

            displayfunction = TextLCDCommand.BITMODE8.get() | 
                              TextLCDCommand.LINE1.get() | 
                              TextLCDCommand.DOTS5x8.get();
        }
        
        row_offsets = new int[4];

        begin(16, 1);  
    }
    
    public void close() throws IOException {
        if (rsPin != null && rsPin.isOpen())  rsPin.close();
        if (rwPin != null && rwPin.isOpen())  rwPin.close();
        if (enablePin != null && enablePin.isOpen())  enablePin.close();
        
        for (GPIOPin pin : dataPins) {
            if (pin != null && pin.isOpen())  pin.close();
        }
    }
    
    public void begin(int cols, int lines) throws IOException {
        begin(cols, lines, TextLCDCommand.DOTS5x8.get());
    }
    
    public void begin(int cols, int lines, int dotsize) throws IOException {
        if (lines > 1) {
          displayfunction |= TextLCDCommand.LINE2.get();
        }
        numlines = lines;

        setRowOffsets(0x00, 0x40, 0x00 + cols, 0x40 + cols);  

        // for some 1 line displays you can select a 10 pixel high font
        if ((dotsize != TextLCDCommand.DOTS5x8.get()) && (lines == 1)) {
          displayfunction |= TextLCDCommand.DOTS5x10.get();
        }

        // SEE PAGE 45/46 FOR INITIALIZATION SPECIFICATION!
        // according to datasheet, we need at least 40 ms after power rises above 2.7 V
        // before sending commands. Arduino can turn on way before 4.5 V so we'll wait 50
        delayMicroseconds(50000);
        
        // Now we pull both RS and R/W low to begin commands
        rsPin.setValue(false);
        enablePin.setValue(false);
        if (rwPin != null) {
            rwPin.setValue(false);
        }

        //put the LCD into 4 bit or 8 bit mode
        if ((displayfunction & TextLCDCommand.BITMODE8.get()) == 0) {
            // this is according to the Hitachi HD44780 datasheet
            // figure 24, pg 46

            // we start in 8bit mode, try to set 4 bit mode
            write4bits(0x03);
            delayMicroseconds(4500); // wait min 4.1ms

            // second try
            write4bits(0x03);
            delayMicroseconds(4500); // wait min 4.1ms

            // third go!
            write4bits(0x03); 
            delayMicroseconds(150);

            // finally, set to 4-bit interface
            write4bits(0x02); 
        } else {
            // this is according to the Hitachi HD44780 datasheet
            // page 45 figure 23

            // Send function set command sequence
            command(TextLCDCommand.FUNCTIONSET.get() | displayfunction);
            delayMicroseconds(4500);  // wait more than 4.1 ms

            // second try
            command(TextLCDCommand.FUNCTIONSET.get() | displayfunction);
            delayMicroseconds(150);

            // third go
            command(TextLCDCommand.FUNCTIONSET.get() | displayfunction);
        }

        // finally, set # lines, font size, etc.
        command(TextLCDCommand.FUNCTIONSET.get() | displayfunction);  

        // turn the display on with no cursor or blinking default
        displaycontrol = TextLCDCommand.DISPLAYON.get() | TextLCDCommand.CURSOROFF.get() | TextLCDCommand.BLINKOFF.get();  
        display();

        // clear it off
        clear();

        // Initialize to default text direction (for romance languages)
        displaymode = TextLCDCommand.ENTRYLEFT.get() | TextLCDCommand.ENTRYSHIFTDECREMENT.get();
        // set the entry mode
        command(TextLCDCommand.ENTRYMODESET.get() | displaymode);

    }

    void setRowOffsets(int row0, int row1, int row2, int row3)
    {
        row_offsets[0] = row0;
        row_offsets[1] = row1;
        row_offsets[2] = row2;
        row_offsets[3] = row3;
    }
    
    /********** high level commands, for the user! */
    public void clear() throws IOException
    {
      command(TextLCDCommand.CLEARDISPLAY.get());  // clear display, set cursor position to zero
      delayMicroseconds(2000);  // this command takes a long time!
    }

    public void home() throws IOException
    {
      command(TextLCDCommand.RETURNHOME.get());  // set cursor position to zero
      delayMicroseconds(2000);  // this command takes a long time!
    }

    public void setCursor(int col, int row)  throws IOException
    {
      int max_lines = row_offsets.length;
      if ( row >= max_lines ) {
        row = max_lines - 1;    // we count rows starting w/ 0
      }
      if ( row >= numlines ) {
        row = numlines - 1;    // we count rows starting w/ 0
      }

      command(TextLCDCommand.SETDDRAMADDR.get() | (col + row_offsets[row]));
    }

    // Turn the display on/off (quickly)
    public void noDisplay() throws IOException {
      displaycontrol &= ~TextLCDCommand.DISPLAYON.get();
      command(TextLCDCommand.DISPLAYCONTROL.get() | displaycontrol);
    }
    
    public void display() throws IOException {
      displaycontrol |= TextLCDCommand.DISPLAYON.get();
      command(TextLCDCommand.DISPLAYCONTROL.get() | displaycontrol);
    }

    // Turns the underline cursor on/off
    public void noCursor() throws IOException {
      displaycontrol &= ~TextLCDCommand.CURSORON.get();
      command(TextLCDCommand.DISPLAYCONTROL.get() | displaycontrol);
    }
    
    public void cursor() throws IOException {
      displaycontrol |= TextLCDCommand.CURSORON.get();
      command(TextLCDCommand.DISPLAYCONTROL.get() | displaycontrol);
    }

    // Turn on and off the blinking cursor
    public void noBlink() throws IOException {
      displaycontrol &= ~TextLCDCommand.BLINKON.get();
      command(TextLCDCommand.DISPLAYCONTROL.get() | displaycontrol);
    }
    
    public void blink() throws IOException {
      displaycontrol |= TextLCDCommand.BLINKON.get();
      command(TextLCDCommand.DISPLAYCONTROL.get() | displaycontrol);
    }

    // These commands scroll the display without changing the RAM
    public void scrollDisplayLeft() throws IOException {
      command(TextLCDCommand.CURSORSHIFT.get() | TextLCDCommand.DISPLAYMOVE.get() | TextLCDCommand.MOVELEFT.get());
    }
    
    public void scrollDisplayRight() throws IOException {
      command(TextLCDCommand.CURSORSHIFT.get() | TextLCDCommand.DISPLAYMOVE.get() | TextLCDCommand.MOVERIGHT.get());
    }

    // This is for text that flows Left to Right
    public void leftToRight() throws IOException {
      displaymode |= TextLCDCommand.ENTRYLEFT.get();
      command(TextLCDCommand.ENTRYMODESET.get() | displaymode);
    }

    // This is for text that flows Right to Left
    public void rightToLeft() throws IOException {
      displaymode &= ~TextLCDCommand.ENTRYLEFT.get();
      command(TextLCDCommand.ENTRYMODESET.get() | displaymode);
    }

    // This will 'right justify' text from the cursor
    public void autoscroll() throws IOException {
      displaymode |= TextLCDCommand.ENTRYSHIFTINCREMENT.get();
      command(TextLCDCommand.ENTRYMODESET.get() | displaymode);
    }

    // This will 'left justify' text from the cursor
    public void noAutoscroll() throws IOException {
      displaymode &= ~TextLCDCommand.ENTRYSHIFTINCREMENT.get();
      command(TextLCDCommand.ENTRYMODESET.get() | displaymode);
    }

    // Allows us to fill the first 8 CGRAM locations
    // with custom characters
    public void createChar(int location, int charmap[]) throws IOException {
      location &= 0x7; // we only have 8 locations 0-7
      command(TextLCDCommand.SETCGRAMADDR.get() | (location << 3));
      for (int i=0; i<8; i++) {
        write(charmap[i]);
      }
    }

    /*********** output stream interfaces */

    public int print(String str) throws IOException {
        int cnt = 0;
        for (char ch : str.toCharArray()) {
            cnt += write((int)ch);
        }
        return cnt;
    }
    
    public int print(int value) throws IOException {
        return print(String.valueOf(value));
    }
    
    public int print(long value) throws IOException {
        return print(String.valueOf(value));
    }
    
    public int print(float value) throws IOException {
        return print(String.valueOf(value));
    }
    
    public int print(double value) throws IOException {
        return print(String.valueOf(value));
    }
    
    public int put(char ch) throws IOException {
        return write((int)ch);
    }
    
    /*********** mid level commands, for sending data/cmds */

    public void command(int value) throws IOException {
        send(value, false);
    }

    public int write(int value) throws IOException {
        send(value, true);
        return 1; // assume success
    }

    /************ low level data pushing commands **********/

    // write either command or data, with automatic 4/8-bit selection
    private void send(int value, boolean mode) throws IOException {
        rsPin.setValue(mode);

        // if there is a RW pin indicated, set it low to Write
        if (rwPin != null) {
            rwPin.setValue(false);
        }

        if ((displayfunction & TextLCDCommand.BITMODE8.get()) != 0) {
            write8bits(value); 
        } else {
            write4bits(value>>4);
            write4bits(value);
        }
    }

    private void pulseEnable() throws IOException {
        enablePin.setValue(false);
        delayMicroseconds(1);    
        enablePin.setValue(true);
        delayMicroseconds(1);       // enable pulse must be >450 ns
        enablePin.setValue(false);
        delayMicroseconds(100);     // commands need >37 us to settle
    }

    private void write4bits(int value) throws IOException {
        for (int i = 0; i < 4; i++) {
            dataPins[i].setValue(((value >> i) & 0x01) != 0 ? true : false);
        }

        pulseEnable();
    }

    private void write8bits(int value) throws IOException {
        for (int i = 0; i < 8; i++) {
            dataPins[i].setValue(((value >> i) & 0x01) != 0 ? true : false);
        }

        pulseEnable();
    }
    
    /************ utility methods **********/

    private GPIOPin openByDeviceConfig(int pinNum, boolean initValue) throws IOException {
        GPIOPinConfig gpioConfig = new GPIOPinConfig.Builder()
                .setControllerNumber(0)
                .setPinNumber(pinNum)
                .setDirection(GPIOPinConfig.DIR_OUTPUT_ONLY)
                .setDriveMode(GPIOPinConfig.MODE_OUTPUT_PUSH_PULL)
                .setInitValue(initValue)
                .build();
        
        return DeviceManager.open(gpioConfig);      
    }
    
    private void delay(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Logger.getLogger(TextLCDDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
  
    private void delayMicroseconds(int micros) {
        try {
            Thread.sleep((micros/1000), (micros%1000)*1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(TextLCDDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
  
}
