/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.jiot.uart_dev;

import java.io.IOException;

/**
 *
 * @author yjkim
 */
public enum UARTCommCommand {
    
    GET_TMP("TMP\n", "T"),
    GET_HMD("HMD\n", "H"),
    GET_ACK("ACK\n", "OK");
    
    private final String cmd;
    private final String responsePrefix;
    
    private UARTCommCommand(String cmd, String responsePrefix) {
        this.cmd = cmd;
        this.responsePrefix = responsePrefix;
    }
    
    public String get() {
        return this.cmd;
    }
    
    public String getResponsePrefix() {
        return this.responsePrefix;
    }
    
    public void send(UARTRPI uart) throws IOException, InterruptedException {
        if (!uart.sendSync(this.cmd)) {
            throw new IOException("Semaphore Acquire Timeout.");
        }
    }

}
