/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jiot.spi_dev;

/**
 *
 * @author comit4
 */
public enum MCP3208Command {
    REDA_CHANNAL(0x060000);
    
    private int cmd;
    
    private MCP3208Command(int cmd) {
        this.cmd = cmd;
    }
    
    public int get(int channel) {
        return (channel << 14) | this.cmd;
    }
}
