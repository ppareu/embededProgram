/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.jiot.jiotmqttcliclient;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 *
 * @author yjkim
 */
public class JiotMqttCLIClient {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.err.print("Input client ID: ");
        String clientId = scanner.nextLine();
        System.err.print("Input CLI Handler ID: ");
        String cliHandlerId = scanner.nextLine();
        
        BigDataHandler bdHandler = new BigDataHandler();
        
        try {
            System.err.print("MQTT CLI console connecting...");
            MqttCLIConsole console = new MqttCLIConsole(clientId, cliHandlerId, bdHandler);
            console.setPrompt("Input command or 'q' to exit: ");
            console.displayPrompt();
            for (String line = scanner.nextLine();
                    !line.trim().equals("q");
                    line = scanner.nextLine()) {
                
                if (line.isBlank()) continue;
                
                line = line.trim();
                if (line.equals("display")) {
                    bdHandler.displayData();
                    console.displayPrompt();
                }
                else if (line.equals("clear")) {
                    bdHandler.clearData();
                    console.displayPrompt();
                }
                else {
                    console.publish(line, 0);
                    System.out.println("Waiting result...");
                }
            }
            
            console.close();
        } catch (MqttException ex) {
            Logger.getLogger(JiotMqttCLIClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
