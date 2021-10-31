/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mthree.flooringmastery1.service;

import com.mthree.flooringmastery1.dao.FlooringMasteryPersistenceException;
import com.mthree.flooringmastery1.dao.FlooringMasteryTaxDao;
import com.mthree.flooringmastery1.dto.Tax;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author salon
 */
public class FlooringMasteryTaxDaoStubImpl implements FlooringMasteryTaxDao {
    private Map <String, Tax> taxes = new HashMap<>();
    private final String DELIMITER = ",";
    private final String TAXES_FILE;
    
    
    //Constructor for testing
    public FlooringMasteryTaxDaoStubImpl (String taxesTextFile) {
        this.TAXES_FILE = taxesTextFile;
    }    
    
    @Override
    public List<Tax> getAllTaxes()throws FlooringMasteryPersistenceException {
        loadTaxes();
        return new ArrayList(taxes.values());
    }
    
    
    @Override 
    public Tax getTax(String stateAbbreviationInput)throws FlooringMasteryPersistenceException {
    loadTaxes();
        return taxes.get(stateAbbreviationInput);
    }
    
    private Tax unmarshallTax(String taxAsText) throws FlooringMasteryPersistenceException {
        //This line is then split at the DELIMITER (,) leaving an array of Strings,
        //stored as taxTokens, which should look like this:
        //_______________
        //|  |     |    |
        //|TX|Texas|4.45|
        //|  |     |    |   
        //---------------
        //[0]  [1]  [2]        
        
        String[] taxTokens = taxAsText.split(DELIMITER);
        String stateAbbreviation = taxTokens[0];
        
        Tax taxFromFile = new Tax(stateAbbreviation);
        
        taxFromFile.setStateName(taxTokens[1]);
        taxFromFile.setTaxRate(new BigDecimal(taxTokens[2]));
        return taxFromFile;
    }
    
    private void loadTaxes() throws FlooringMasteryPersistenceException {
        Scanner scanner;
        try {
            scanner = new Scanner(new BufferedReader(new FileReader(TAXES_FILE)));
        } catch (FileNotFoundException e) {
            throw new FlooringMasteryPersistenceException(
            "-_- Could not load tax data into memory.", e);
        }
        String currentLine;
        Tax currentTax;
        
        int iteration = 0;
        
        while (scanner.hasNextLine()) {
            //om the first iteration do not try to unmarshall tax, as the first
            //line is heading
            if (iteration == 0) {
                String headings = scanner.nextLine();
                iteration ++;
                continue;
            }
        
            currentLine = scanner.nextLine();
            currentTax = unmarshallTax(currentLine);
            
            
            taxes.put(currentTax.getStateAbbr(), currentTax);
            iteration++;
        }
        //Clean up/close file
        scanner.close();
        }
}
