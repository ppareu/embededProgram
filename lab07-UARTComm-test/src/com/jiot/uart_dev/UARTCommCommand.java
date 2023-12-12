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
    
    GET_TMP("TMP\n"),
    GET_HMD("HMD\n"),
    GET_ACK("ACK\n");
    
    public String cmd;
    
    private UARTCommCommand(String cmd) {
        this.cmd = cmd;
    }
    
    public String get() {
        return this.cmd;
    }
    
    public void send(UARTRPI uart) throws IOException, InterruptedException {
        if (!uart.sendSync(this.cmd)) {
            throw new IOException("Semaphore Acquire Timeout.");
        }
    }

}
