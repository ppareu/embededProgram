/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jiot.labmid;

import static com.jiot.labmid.LABMID_TEST.CHANNEL_PSD;
import com.jiot.spi_dev.SPIRPI;
import com.jiot.spi_dev.driver.MCP3208Device;
import java.io.IOException;

/**
 *
 * @author comit4
 */
public class PSD {
    
    private MCP3208Device adcDev = null;

    public PSD() throws IOException {
        adcDev = new MCP3208Device(SPIRPI.CE1);
    }
    
    public void close() throws IOException {
        adcDev.close(); 
    }
   
    public double getDistance() throws IOException {
        double psdDist = (27.61 / (adcDev.convertVolts(adcDev.analogRead(CHANNEL_PSD)) - 0.1696));

        psdDist = Math.round(psdDist * 10.0) / 10.0;
        return psdDist;
    }
}
