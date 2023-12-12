/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jiot.things;

import com.jiot.spi_dev.SPIRPI;
import com.jiot.spi_dev.driver.MCP3208Device;
import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yjkim
 */
public class AnalogInControlPoint extends ControlPoint {
    private static AtomicReference<MCP3208Device> adcDevRef =
            new AtomicReference<MCP3208Device>();
    
    private static MCP3208Device getAdcDevice() {
        if (adcDevRef.get() == null) {
            try {
                adcDevRef.set(new MCP3208Device(SPIRPI.CE1));
            } catch (IOException ex) {
                Logger.getLogger(AnalogInControlPoint.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return adcDevRef.get();
    }
    
    private static AtomicInteger OPEN_COUNT = new AtomicInteger(0);
    private int channel;
    private Future pollingFuture = null;

    public AnalogInControlPoint(int channel) {
        super();
        this.channel = channel;
    }

    public void open(boolean polling) {
        OPEN_COUNT.incrementAndGet();
        setName("AI_channel-" + channel);
        
        if (polling) {
            pollingFuture = POLLING.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    try {
                        int oldValue = presentValue.get();
                        int newValue = getAdcDevice().analogRead(channel);
//                        if (oldValue != newValue) {
                        if (Math.abs(oldValue - newValue) > 40) {
                            presentValue.set(newValue);
                            fireChanged();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(AnalogInControlPoint.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }, 0, 1, TimeUnit.SECONDS);
        }
    }
    
    @Override
    public void open() {
        open(true);
    }

    @Override
    public void close() {
        int ref_count = OPEN_COUNT.decrementAndGet();
        if (ref_count >= 0) {
            if (pollingFuture != null) {
                pollingFuture.cancel(false);
                pollingFuture = null;
            }
            if (ref_count == 0) {
                try {
                    getAdcDevice().close();
                    adcDevRef.set(null);
                } catch (IOException ex) {
                    Logger.getLogger(AnalogInControlPoint.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        else {
            OPEN_COUNT.set(0);
        }
    }

    @Override
    public boolean isEnabled() {
        return getAdcDevice().isOpen();
    }

    @Override
    public Type getType() {
        return Type.AI;
    }
    
}
