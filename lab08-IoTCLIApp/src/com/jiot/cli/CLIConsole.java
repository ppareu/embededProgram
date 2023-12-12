/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jiot.cli;

import com.jiot.things.ControlPoint;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.nio.channels.Channels;
import java.util.Observable;
import java.util.Observer;
import jdk.dio.DeviceManager;
import jdk.dio.uart.UART;
import jdk.dio.uart.UARTConfig;

/**
 *
 * @author yjkim
 */
public class CLIConsole implements Observer {
   
    private UART uart = null;
    private BufferedReader in = null;
    private BufferedWriter out = null;
    private boolean change_log = true;
    
    public CLIConsole(UARTConfig config) throws IOException {
        if (config != null) {
            uart = (UART)DeviceManager.open(config);
            in = new BufferedReader(Channels.newReader(uart, "UTF-8"));
            out = new BufferedWriter(Channels.newWriter(uart, "UTF-8"));
            uart.setReceiveTimeout(100);
        }
        else {
            in = new BufferedReader(new InputStreamReader(System.in));
            out = new BufferedWriter(new OutputStreamWriter(System.out));
        }
    }

    private void write(String result) {
        try {
            out.write(result);
            out.newLine();
            out.write("Console >> ");
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(CLIConsole.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    private void close() {
        try {
            in.close();
            out.close();
            if (uart != null)  uart.close();
        } catch (IOException ex) {
            Logger.getLogger(CLIConsole.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() throws IOException {
        write("Please input command...");
        
        CommandInterpreter interpreter = CommandInterpreter.getInstance();
        
        for (String line = in.readLine();
                line == null || (!line.equals("quit") && !line.equals("exit"));
                line = in.readLine())
        {
            if (line == null)  continue;
            
            String args[] = line.split(" ");
            String result = interpreter.execute(args);
            if (result == null)  result = "";
            write(result);
        }
        
        write("Good bye!");
        close();
    }
    
    public void displayChangeLog(boolean display) {
       change_log = display;
    }

    @Override
    public void update(Observable obj, Object arg) {
        if (obj instanceof ControlPoint && change_log == true) {
            ControlPoint cp = (ControlPoint)obj;
            if (arg == null) {
                write("[Observer] Changed value(" + cp.getName() + "): " + cp.getValue());
            }
            else {
                if (arg.toString().equals("name")) {
                    write("[Observer] Changed name(id=" + cp.getId() + "): " + cp.getName());
                }
                else {
                    write("[Observer] Changed(" + cp.getName() + "): " + arg);
                }
            }
        }
    }
    
}
