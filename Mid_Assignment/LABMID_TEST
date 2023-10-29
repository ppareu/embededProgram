/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.jiot.labmid;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author comit4 201995039
 */
public class LABMID_TEST implements Runnable{
    
    // 초음파 센서
    static final String TRIGPIN = "GPIO19";
    static final String ECHOPIN = "GPIO26";
    private Ultrasonic ultrasonic = null;
    
    // 적외선 센서
    static final int CHANNEL_PSD = 1;
    private PSD psd = null;
    
    // 부저 센서
    static final String BUZZER_PIN = "GPIO21";
    private BUZZER buzzer = null;
    boolean isBuzzerOn = false;
    
    // Text LCD
    static final String PSPIN = "GPIO02";
    static final String RWPIN = "GPIO03";
    static final String EPIN  = "GPIO04";
    static final String D6PIN  = "GPIO17";
    static final String D7PIN  = "GPIO27";
    
    static final String D0PIN  = "GPIO10";
    static final String D1PIN  = "GPIO09";
    static final String D2PIN  = "GPIO11";
    static final String D3PIN  = "GPIO05";
    static final String D4PIN  = "GPIO06";
    static final String D5PIN  = "GPIO13";
    
    
    public LABMID_TEST() throws IOException {
        ultrasonic = new Ultrasonic(TRIGPIN, ECHOPIN);
        psd = new PSD();
        buzzer = new BUZZER(BUZZER_PIN);
    }
    
    public void run() {
        while (true) {            
            try {
                double UltraDist = ultrasonic.getDistance();
                String ulDist = String.format("%.2f", UltraDist);
                System.out.println("초음파 센서 거리 : " + ulDist + " cm");
                
                double PSDDist = psd.getDistance();
                System.out.println("적외선 센서 거리 : " + PSDDist + " cm");

                if (PSDDist <= 15.0) {
                    if (!isBuzzerOn) {
                        buzzer.play();
                        isBuzzerOn = true;
                    }
                } else {
                    if (isBuzzerOn) {
                        buzzer.stop();
                        isBuzzerOn = false;
                    }
                }
                Thread.sleep(1000);
            } catch (IOException ex) {
                Logger.getLogger(LABMID_TEST.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(LABMID_TEST.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void main(String[] args) {
        try {
            Thread t = new Thread(new LABMID_TEST());
            t.start();
        } catch (IOException ex) {
            Logger.getLogger(LABMID_TEST.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
   
}
