/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jiot.things;

import com.jiot.uart_dev.UARTRPI;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yjkim
 */
public class SerialOutControlPoint extends OutputControlPoint {

    private final String response = "OK";

    private UARTRPI uartDev = null;
    private String command;
    private int outValue = -1;
    private boolean isOpen = false;

    class SerialOutDataListener implements UARTRPI.UARTDataListener{

        @Override
        public boolean processReceivedData(String data) {
            boolean processed = false;
            String[] tokens = data.split("=|\\n");
            if (tokens[0].equals(response)) {
                presentValue.set(outValue);
                processed = true;
            }
            return processed;
        }
        
    }
    
    public SerialOutControlPoint(String command) {
        super();
        this.command = command;
    }

    @Override
    public void open() {
        try {
            uartDev = UARTRPI.getInstance();
            uartDev.addDataListener(command, new SerialOutDataListener());
            isOpen = true;
            setName("SerialOut_" + command.split("\n")[0]);
        } catch (IOException ex) {
            Logger.getLogger(SerialOutControlPoint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void close() {
        if (isEnabled()) {
            try {
                uartDev.removeDataListener(command);
                uartDev.close();
                uartDev = null;
                isOpen = false;
            } catch (IOException ex) {
                Logger.getLogger(SerialOutControlPoint.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return (uartDev != null && isOpen);
    }

    @Override
    public Type getType() {
        return Type.SO;
    }
    
    @Override
    public void setValue(int value) {
        outValue = value;
        try {
            int oldValue = getValue();
            String cmd = command.split("\n")[0] + "+" + value + "\n";
            if (uartDev.sendSync(cmd)) {
                if (oldValue != getValue()) {
                    fireChanged();
                }
            }
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(SerialOutControlPoint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
