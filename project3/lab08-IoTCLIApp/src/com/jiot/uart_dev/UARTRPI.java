/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jiot.uart_dev;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import jdk.dio.DeviceManager;
import jdk.dio.uart.UART;
import jdk.dio.uart.UARTConfig;
import jdk.dio.uart.UARTEvent;
import jdk.dio.uart.UARTEventListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yjkim
 */
public class UARTRPI {
    private static final String UART_CONTROLLER = "ttyAMA0";
    private static final int UART_BAUDRATE = 115200;
    
    private static AtomicReference<UARTRPI> instance = 
            new AtomicReference<UARTRPI>();
    
    public static UARTRPI getInstance() throws IOException {
        if (instance.get() == null) {
            instance.set(new UARTRPI(UART_CONTROLLER, UART_BAUDRATE));
        }
        return instance.get();
    }

    public static UARTRPI getInstance(String controllerName, int baudrate) throws IOException {
        if (instance.get() == null) {
            instance.set(new UARTRPI(controllerName, baudrate));
        }
        return instance.get();
    }
    
    
    public interface UARTDataListener {
        boolean processReceivedData(String data);
    }
    
    private final Map<String, UARTDataListener> dataListenerList =
            new HashMap<String, UARTDataListener>();
    
    public void addDataListener(String devName, UARTDataListener listener) {
        dataListenerList.put(devName, listener);
    }

    public void removeDataListener(String devName) {
        dataListenerList.remove(devName);
    }

    private UART port = null;
    private final Semaphore dataReceived = new Semaphore(0, true);
    
    
    public UARTRPI(String controllerName, int baudrate) throws IOException {
        UARTConfig config = new UARTConfig.Builder()
            .setControllerName(controllerName)
            .setChannelNumber(1)
            .setBaudRate(baudrate)
            .setDataBits(UARTConfig.DATABITS_8)
            .setStopBits(UARTConfig.STOPBITS_1)
            .setParity(UARTConfig.PARITY_NONE)
            .setFlowControlMode(UARTConfig.FLOWCONTROL_NONE)
            .build();
        
        port = DeviceManager.open(config);
        
        port.setEventListener(UARTEvent.INPUT_DATA_AVAILABLE, new UARTEventListener(){
            @Override
            public void eventDispatched(UARTEvent uarte) {
                if (uarte.getID() == UARTEvent.INPUT_DATA_AVAILABLE) {
                    ByteBuffer buffer = ByteBuffer.allocateDirect(100);
                    
                    try {
                        int length = port.read(buffer);
                        byte[] bytes = new byte[length];
                        buffer.flip();
                        buffer.get(bytes);
                        String response = new String(bytes);
//                        System.out.println(">>> " + response);
                        
                        boolean release = false;
                        Iterator<String> keys = dataListenerList.keySet().iterator();
                        while (keys.hasNext()) {
                            release = dataListenerList.get(keys.next()).processReceivedData(response);
                            if (release)  break;
                        }
                        
                        if (release) {
                            dataReceived.release();
                        }                        
                    } catch(IOException ex) {
                        Logger.getLogger(UARTRPI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else {
                    throw new UnsupportedOperationException("Not supported event yet.");
                }
            }
        });
    }
    
    public void close() throws IOException {
        if (port != null && dataListenerList.isEmpty()) {
            port.close();
            instance.set(null);
            port = null;
        }
    }
    
    public UART getPort() {
        return this.port;
    }
    
    public void send(ByteBuffer data) throws IOException {
        port.write(data);
    }
    
    public void send(byte[] data, int length) throws IOException {
        ByteBuffer out = ByteBuffer.allocateDirect(length);
        out.put(data);
        out.flip();
        port.write(out);
    }
    
    public void send(String data) throws IOException {
        ByteBuffer out = ByteBuffer.allocateDirect(data.length());
        out.put(data.getBytes());
        out.flip();
        port.write(out);
    }
    
    public boolean sendSync(String data) throws IOException, InterruptedException {
        send(data);
        return dataReceived.tryAcquire(60000, TimeUnit.MILLISECONDS);
    }
    
    public int readData(ByteBuffer buffer) throws IOException {
        return port.read(buffer);
    }
    
}
