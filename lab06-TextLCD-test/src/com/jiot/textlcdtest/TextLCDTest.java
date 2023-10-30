/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.jiot.textlcdtest;

import com.jiot.gpio_dev.driver.ButtonDevice;
import com.jiot.gpio_dev.driver.LEDDevice;
import com.jiot.gpio_dev.driver.TextLCDDevice;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author yjkim
 */
public class TextLCDTest {

    private TextLCDDevice textLcd = null;
    private LEDDevice led1 = null;
    private ButtonDevice btn1 = null;
    
    public TextLCDTest() throws IOException {
        textLcd = new TextLCDDevice(19, 26, 12, 16, 20, 21);  
        
        textLcd.begin(16, 2);
        textLcd.cursor();
//        textLcd.print("Hello, World!");
        textLcd.print("IP:");
        textLcd.print(getLocalIPAddress());
        
        led1 = new LEDDevice(17, true);
        led1.blink(500);
        
        btn1 = new ButtonDevice(23);
    }
    
    public void close() {
        try {
            if (textLcd != null) textLcd.close();
        } catch (IOException ex) {
            Logger.getLogger(TextLCDTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void run() throws IOException, InterruptedException {
        long count = 0;
        
        while (true) {
            textLcd.setCursor(0, 1);        
            textLcd.print(count++);
            textLcd.print("  ");
            textLcd.print(btn1.read() ? "Released" : "Pressed");
            Thread.sleep(1000);
        }
    }
    
    public String getLocalIPAddress() {
        String addr = "127.0.0.1";
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("google.com", 80));
            addr = socket.getLocalAddress().getHostAddress();
        } catch (IOException ex) {
            Logger.getLogger(TextLCDTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return addr;
    }
         
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            TextLCDTest test = new TextLCDTest();
            test.run();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(TextLCDTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
