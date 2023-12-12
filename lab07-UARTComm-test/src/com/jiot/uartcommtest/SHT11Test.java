/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.jiot.uartcommtest;

import com.jiot.uart_dev.driver.SHT11Device;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yjkim
 */
public class SHT11Test {

    private SHT11Device sht11Dev = null;

    public SHT11Test() throws IOException {
        sht11Dev = new SHT11Device();
    }
    
    public void run() throws InterruptedException, IOException {
        while (!sht11Dev.isActive()) {
            System.out.println("SHT11 device not active yet...");
            Thread.sleep(5000);
        }
        System.out.println("SHT11 device is active...");
        
        for (int i=0; i<10; i++) {
            System.out.println("Temperature = " + sht11Dev.getTemperature());
            Thread.sleep(1000);
            System.out.println("Humidity = " + sht11Dev.getHumidity() + "\n");
            Thread.sleep(1000);
        }
        
        sht11Dev.close();        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            System.out.println("Program start...");
            SHT11Test test = new SHT11Test();
            test.run();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(SHT11Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
