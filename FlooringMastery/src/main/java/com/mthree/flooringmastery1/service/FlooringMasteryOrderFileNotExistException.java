/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mthree.flooringmastery1.service;

/**
 *
 * @author salon
 */
public class FlooringMasteryOrderFileNotExistException extends Exception {
    public FlooringMasteryOrderFileNotExistException(String message) {
        //calls matching constructor on the Exception class by calling super, so base constructors do
        //all the work of initialising our object.
        super(message);
    }
    public FlooringMasteryOrderFileNotExistException(String message, Throwable cause) {
        super(message, cause);
    }    
}
