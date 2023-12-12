/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jiot.coapiotservice;

import com.jiot.things.ControlPoint;
import com.jiot.things.ControlPointContainer;
import java.util.Collection;
import org.eclipse.californium.core.CoapServer;

/**
 *
 * @author yjkim
 */
public class ControlPointCoAPServer extends CoapServer {
    private ControlPointContainer cpContainer = null;

    @Override
    public synchronized void start() {
        cpContainer = ControlPointContainer.getInstance();
        cpContainer.start();
        
        Collection<ControlPoint> points = cpContainer.getControlPoints();
        for (ControlPoint point : points) {
            this.add(new ControlPointResource(point));
        }
        
        super.start();
    }
    
    @Override
    public synchronized void stop() {
        if (cpContainer != null) {
            cpContainer.stop();
        }
        
        super.stop(); 
    }
    
}
