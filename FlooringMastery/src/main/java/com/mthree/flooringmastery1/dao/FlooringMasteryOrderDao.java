/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mthree.flooringmastery1.dao;

import com.mthree.flooringmastery1.dto.Order;
import com.mthree.flooringmastery1.service.FlooringMasteryOrderFileNotExistException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author salon
 */
public interface FlooringMasteryOrderDao {
    
    List <Order> getAllOrdersForADate(String orderFile)throws FlooringMasteryPersistenceException;
    
    List<Integer> getAllOrderNumsForADate(String orderFile) throws FlooringMasteryPersistenceException;
    
    Order getOrder (String orderFile, int orderNum) throws FlooringMasteryPersistenceException;
    
    Order addOrderToExistingFile(String orderFile, int orderNumber, Order order) throws FlooringMasteryPersistenceException;
    
    Order addOrderToNewFile(String orderFile, int orderNumber, Order order) throws FlooringMasteryPersistenceException;
    
    Order removeOrder(String orderFile, int orderNumber) throws FlooringMasteryPersistenceException;

    String [] listAllOrderFiles();
    
    List<Order> getAllOrders() throws FlooringMasteryPersistenceException;

    List<Integer> getAllOrderNums() throws FlooringMasteryPersistenceException;
    
    Order editOrder(String orderFile, Order updatedOrder)throws FlooringMasteryPersistenceException;
    
    Map<String,List<Order>> getExportData() throws FlooringMasteryPersistenceException;
    
    void exportAllData() throws FlooringMasteryPersistenceException;
    
    public void deleteOrderFile(String fileName)throws FlooringMasteryOrderFileNotExistException;
}
