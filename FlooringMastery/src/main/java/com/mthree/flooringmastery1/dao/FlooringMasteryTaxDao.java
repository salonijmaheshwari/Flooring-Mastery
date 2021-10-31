/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mthree.flooringmastery1.dao;

import com.mthree.flooringmastery1.dto.Tax;
import java.util.List;

/**
 *
 * @author salon
 */
public interface FlooringMasteryTaxDao {

    List<Tax> getAllTaxes() throws FlooringMasteryPersistenceException;

    Tax getTax(String stateAbbreviationInput) throws FlooringMasteryPersistenceException;
}
