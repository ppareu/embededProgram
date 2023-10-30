/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.jiot.gpio_dev.driver;

/**
 *
 * @author yjkim
 */
public enum TextLCDCommand {
    
    CLEARDISPLAY(0x01),
    RETURNHOME(0x02),
    ENTRYMODESET(0x04),
    DISPLAYCONTROL(0x08),
    CURSORSHIFT(0x10),
    FUNCTIONSET(0x20),
    SETCGRAMADDR(0x40),
    SETDDRAMADDR(0x80),

    // flags for display entry mode
    ENTRYRIGHT(0x00),
    ENTRYLEFT(0x02),
    ENTRYSHIFTINCREMENT(0x01),
    ENTRYSHIFTDECREMENT(0x00),

    // flags for display on/off control
    DISPLAYON(0x04),
    DISPLAYOFF(0x00),
    CURSORON(0x02),
    CURSOROFF(0x00),
    BLINKON(0x01),
    BLINKOFF(0x00),

    // flags for display/cursor shift
    DISPLAYMOVE(0x08),
    CURSORMOVE(0x00),
    MOVERIGHT(0x04),
    MOVELEFT(0x00),

    // flags for function set
    BITMODE8(0x10),
    BITMODE4(0x00),
    LINE2(0x08),
    LINE1(0x00),
    DOTS5x10(0x04),
    DOTS5x8(0x00);

    final private int cmd;
    
    private TextLCDCommand(int cmd) {
        this.cmd = cmd;
    }
    
    public int get() {
        return this.cmd;
    }
}
