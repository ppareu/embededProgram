/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jiot.uart_dev.driver;

import java.io.IOException;
import com.jiot.uart_dev.UARTCommCommand;
import com.jiot.uart_dev.UARTRPI;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author yjkim
 */
public class SHT11Device {
    
    public static final String DEV_NAME = "SHT11";
    
    private UARTRPI uart = null;
    private double temperature = 0;
    private double humidity = 0;
    private double psdDist = 0;
    private boolean active = false;
    
    class SHT11DataListener implements UARTRPI.UARTDataListener {

        @Override
        public boolean processReceivedData(String data) {
            boolean processed = true;
            
//            System.out.println("Received data: " + data);
            
            String[] tokens = data.split("=|\\n");
            if (tokens[0].equals("H")) {
                humidity = Double.parseDouble(tokens[1]);
            }
            else if (tokens[0].equals("T")) {
                temperature = Double.parseDouble(tokens[1]);
            } else if (tokens[0].equals("P")) {
                // psd distance
                psdDist = Double.parseDouble(tokens[1]);
            }
            else if (tokens[0].equals("OK")) {
                active = true;
            }
            else {
                processed = false;
            }
            
            return processed;
        }
        
    };

    public SHT11Device() throws IOException {
        uart = UARTRPI.getInstance();
        uart.addDataListener(DEV_NAME, new SHT11DataListener());
    }
    
    public void close() throws IOException {
        if (uart != null) {
            uart.removeDataListener(DEV_NAME);
            uart.close();
        }
    }
    
    public boolean isActive() {
        active = false;
        try {
            UARTCommCommand.GET_ACK.send(uart);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(SHT11Device.class.getName()).log(Level.SEVERE, null, ex);
        }
        return active;
    }
    
    public double getTemperature() {
        try {
            UARTCommCommand.GET_TMP.send(uart);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(SHT11Device.class.getName()).log(Level.SEVERE, null, ex);
        }
        return temperature;
    }
    
    public double getHumidity() {
        try {
            UARTCommCommand.GET_HMD.send(uart);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(SHT11Device.class.getName()).log(Level.SEVERE, null, ex);
        }
        return humidity;
    }
    
    // getPsdDist
    public double getPsdDist() {
        try {
            UARTCommCommand.GET_PSD.send(uart);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(SHT11Device.class.getName()).log(Level.SEVERE, null, ex);
        }
        return psdDist;
        
    }

}
