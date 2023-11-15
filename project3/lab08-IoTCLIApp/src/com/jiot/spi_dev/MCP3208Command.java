/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.jiot.spi_dev;

/**
 *
 * @author yjkim
 */
public enum MCP3208Command {
    
    READ_CHANNEL(0x060000);
    
    private int cmd;
    
    private MCP3208Command(int cmd) {
        this.cmd = cmd;
    }
    
    public int get(int channel) {
        return (channel << 14) | cmd;
    }
    
}
