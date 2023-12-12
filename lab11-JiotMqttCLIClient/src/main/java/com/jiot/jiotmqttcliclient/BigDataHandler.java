/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jiot.jiotmqttcliclient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author yjkim
 */
public class BigDataHandler {
    
    private List<ChangeOfValue> storage = 
            Collections.synchronizedList(new ArrayList<ChangeOfValue>());
    
    public void saveData(ChangeOfValue cov) {
        storage.add(cov);
    }
    
    public void displayData() {
        for (ChangeOfValue cov : storage) {
            System.err.println(cov);
        }
    }
    
    public void clearData() {
        storage.clear();
    }

}
