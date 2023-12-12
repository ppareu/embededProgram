/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.jiot.iotcliapp;

import com.jiot.cli.CLIConsole;
import com.jiot.things.ControlPointContainer;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 *
 * @author yjkim
 */
public class IoTCLIApp {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ControlPointContainer cpContainer = ControlPointContainer.getInstance();
        cpContainer.start();
        
        try {
            CLIConsole console = new CLIConsole(null);
            
            cpContainer.getControlPoints().forEach((cp) -> { 
                cp.addObserver(console);
            });

            console.displayChangeLog(true);
            console.run();          
        } catch (IOException ex) {
            Logger.getLogger(IoTCLIApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        cpContainer.stop();
    }
    
}
