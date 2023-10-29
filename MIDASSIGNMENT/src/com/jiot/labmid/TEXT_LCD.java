/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jiot.labmid;

import java.io.IOException;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;

/**
 *
 * @author comit4
 */
public class TEXT_LCD {
    
    private GPIOPin rsPin = null;
    private GPIOPin pwPin = null;
    private GPIOPin ePin = null;
    private GPIOPin d6Pin = null;
    private GPIOPin d7Pin = null;
    private GPIOPin d0Pin = null;
    private GPIOPin d1Pin = null;
    private GPIOPin d2Pin = null;
    private GPIOPin d3Pin = null;
    private GPIOPin d4Pin = null;
    private GPIOPin d5Pin = null;
    
    // 추가: LCD 화면의 가로 길이와 세로 길이
    private int lcdWidth;
    private int lcdHeight;

    // 추가: 현재 커서 위치
    private int cursorX;
    private int cursorY;
    
    public TEXT_LCD(String rsName, String pwName, String eName, String d0Name, String d1Name, String d2Name, String d3Name, String d4Name, String d5Name, String d6Name, String d7Name) throws IOException {
        rsPin = DeviceManager.open(rsName, GPIOPin.class);
        pwPin = DeviceManager.open(pwName, GPIOPin.class);
        ePin = DeviceManager.open(eName, GPIOPin.class);
        
        d0Pin = DeviceManager.open(d0Name, GPIOPin.class);
        d1Pin = DeviceManager.open(d1Name, GPIOPin.class);
        d2Pin = DeviceManager.open(d2Name, GPIOPin.class);
        d3Pin = DeviceManager.open(d3Name, GPIOPin.class);
        d4Pin = DeviceManager.open(d4Name, GPIOPin.class);
        d5Pin = DeviceManager.open(d5Name, GPIOPin.class);
        d6Pin = DeviceManager.open(d6Name, GPIOPin.class);
        d7Pin = DeviceManager.open(d7Name, GPIOPin.class); 
    }
    
    public void close() throws IOException {
        if (rsPin != null && rsPin.isOpen())  rsPin.close();
        if (pwPin != null && pwPin.isOpen())  pwPin.close();
        if (ePin != null && ePin.isOpen())  ePin.close();
        if (d0Pin != null && d0Pin.isOpen())  d0Pin.close();
        if (d1Pin != null && d1Pin.isOpen())  d1Pin.close();
        if (d2Pin != null && d2Pin.isOpen())  d2Pin.close();
        if (d3Pin != null && d3Pin.isOpen())  d3Pin.close();
        if (d4Pin != null && d4Pin.isOpen())  d4Pin.close();
        if (d5Pin != null && d5Pin.isOpen())  d5Pin.close();
        if (d6Pin != null && d6Pin.isOpen())  d6Pin.close();
        if (d7Pin != null && d7Pin.isOpen())  d7Pin.close();
    }
    
//    private void sendCommand(int command) throws IOException {
//        // RS (Register Select) 핀을 낮은 값으로 설정하여 명령이 전송되는 것을 나타냅니다.
//        rsPin.setValue(false);
//
//        // 명령을 전송하기 위해 데이터 핀(D0부터 D7)을 설정합니다.
//        setD0ToD7Pins(command);
//
//        // 명령을 보내기 위해 Enable (E) 핀을 트리거합니다.
//        triggerEnablePin();
//
//        // 명령이 처리되기를 기다리기 위해 작은 지연을 추가하는 것이 일반적입니다.
//        // 필요한 경우 이 위치에 작은 지연을 추가할 수 있습니다.
//
//        // 데이터 핀을 초기화합니다.
//        clearDataPins();
//    }
//
//    private void setD0ToD7Pins(int data) throws IOException {
//        // 'data'의 이진값에 따라 데이터 핀 (D0에서 D7)을 설정합니다.
//        d0Pin.setValue((data & 0x01) == 0x01);
//        d1Pin.setValue((data & 0x02) == 0x02);
//        d2Pin.setValue((data & 0x04) == 0x04);
//        d3Pin.setValue((data & 0x08) == 0x08);
//        d4Pin.setValue((data & 0x10) == 0x10);
//        d5Pin.setValue((data & 0x20) == 0x20);
//        d6Pin.setValue((data & 0x40) == 0x40);
//        d7Pin.setValue((data & 0x80) == 0x80);
//    }
//
//    private void triggerEnablePin() throws IOException {
//        // Enable (E) 핀을 펄스로 설정하여 데이터를 전송합니다.
//        ePin.setValue(true);
//        // LCD가 펄스를 인식할 수 있도록 작은 지연을 추가해야 할 수도 있습니다.
//        ePin.setValue(false);
//    }
//
//    private void clearDataPins() throws IOException {
//        // 데이터 핀 (D0에서 D7)을 초기화합니다.
//        d0Pin.setValue(false);
//        d1Pin.setValue(false);
//        d2Pin.setValue(false);
//        d3Pin.setValue(false);
//        d4Pin.setValue(false);
//        d5Pin.setValue(false);
//        d6Pin.setValue(false);
//        d7Pin.setValue(false);
//    }
//    
//    private void sendData(char data) throws IOException {
//        // RS (Register Select) 핀을 높은 값으로 설정하여 데이터를 전송하는 것을 나타냅니다.
//        rsPin.setValue(true);
//
//        // 문자를 전송하기 위해 데이터 핀(D0부터 D7)을 설정합니다.
//        setD0ToD7Pins(data);
//
//        // 데이터를 보내기 위해 Enable (E) 핀을 트리거합니다.
//        triggerEnablePin();
//
//        // 데이터가 처리되기를 기다리기 위해 작은 지연을 추가하는 것이 일반적입니다.
//        // 필요한 경우 이 위치에 작은 지연을 추가할 수 있습니다.
//
//        // 데이터 핀을 초기화합니다.
//        clearDataPins();
//    }
//    
//    public void print(String text) throws IOException {
//        // 문자열을 LCD에 출력하는 명령을 전송합니다.
//        // 문자열을 한 문자씩 처리하며 LCD에 출력합니다.
//        for (char c : text.toCharArray()) {
//            if (c == '\n') {
//                // 개행 문자일 경우 다음 줄로 이동
//                cursorX = 0;
//                cursorY++;
//                // 커서 위치가 화면을 벗어나면 필요한 동작을 수행하세요.
//            } else {
//                // 일반 문자일 경우 문자를 출력하고 커서를 이동
//                sendData(c);
//                cursorX++;
//                // 커서 위치가 화면을 벗어나면 필요한 동작을 수행하세요.
//            }
//        }
//    }
//    
//    public void clear() throws IOException {
//        // LCD 화면을 지우는 명령을 LCD에 전송합니다.
//        sendCommand(TEXT_LCD_Command.LCD_CLEARDISPLAY);
//
//        // 일반적으로 화면을 지운 후에는 LCD가 동작을 완료할 수 있도록
//        // 작은 지연(delay)를 주는 것이 일반적입니다.
//        // 필요한 경우 이 위치에 작은 지연을 추가할 수 있습니다.
//
//        // 이제 LCD 화면이 지워져야 합니다.
//        // 커서 위치 초기화
//        cursorX = 0;
//        cursorY = 0;
//    }
//    
//    public void setCursor(int col, int row) throws IOException {
//        int[] rowOffsets = {0x00, 0x40, 0x14, 0x54};
//
//        if (row >= lcdHeight) {
//            row = lcdHeight - 1;
//        }
//
//        int command = TEXT_LCD_Command.LCD_SETDDRAMADDR | (col + rowOffsets[row]);
//        sendCommand(command);
//    }
//
//    public void init() throws IOException {
//
//    }
}
