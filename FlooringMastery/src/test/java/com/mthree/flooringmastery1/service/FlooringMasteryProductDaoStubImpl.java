/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mthree.flooringmastery1.service;

import com.mthree.flooringmastery1.dao.FlooringMasteryPersistenceException;
import com.mthree.flooringmastery1.dao.FlooringMasteryProductDao;
import com.mthree.flooringmastery1.dto.Product;
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
public class FlooringMasteryProductDaoStubImpl implements FlooringMasteryProductDao {
     private Map<String, Product> products = new HashMap<>();
    private final String DELIMITER = ",";
    private final String PRODUCTS_FILE;
    
    
               
    //Constructor for testing
    public FlooringMasteryProductDaoStubImpl (String productsTextFile) {
        this.PRODUCTS_FILE = productsTextFile;
    }    
    
    @Override
    public List<Product> getAllProducts()throws FlooringMasteryPersistenceException {
        loadProducts();
        return new ArrayList(products.values());
    }    
    
    @Override 
    public Product getProduct (String productType) throws FlooringMasteryPersistenceException {
        loadProducts();
        return products.get(productType);
    }
    
    
    private String marshallProduct(Product aProduct) {
        //Get product type, cost per sq ft and labor cost per sq ft.
        String productAsText = aProduct.getProductType();
        productAsText += aProduct.getCostPerSquareFoot().toString();
        productAsText += aProduct.getLaborCostPerSquareFoot().toString();
        return productAsText;
    }
    
    /**
     * UnmarshallTax translates a line of text into a order object. 
     * @param 
     * @return 
     */
    private Product unmarshallProduct(String productAsText) {
        //This line is then split at the DELIMITER (,) leaving an array of Strings,
        //stored as orderTokens, which should look like this:
        //__________________
        //|      |    |    |  
        //|Carpet|2.25|2.10|
        //|      |    |    |  
        //------------------
        //   [0]  [1]  [2]  
        String [] productTokens = productAsText.split(DELIMITER);
        
        String productType = productTokens[0];
        Product productFromFile = new Product(productType);
        
        productFromFile.setCostPerSquareFoot(new BigDecimal(productTokens[1]));
        productFromFile.setLaborCostPerSquareFoot(new BigDecimal(productTokens[2]));

        return productFromFile;
    }
    
    private void loadProducts() throws FlooringMasteryPersistenceException {
        //Open File:
        Scanner scanner;
        try {
            scanner = new Scanner(
            new BufferedReader(
            new FileReader(PRODUCTS_FILE)));
        } catch (FileNotFoundException e) {
            throw new FlooringMasteryPersistenceException(
            "-_- Could not load product data into memory",e);
        }
        
        //Read from file
        String currentLine; //holds the most recent line read from the file
        Product currentProduct;  //holds the most recent unmarshalled order
        
        int iteration = 0;
        
        while (scanner.hasNextLine()) {
            //on the first iteration do not try to unmarshall order, as the first 
            //line is the headings.
            if (iteration == 0) {
                String headings = scanner.nextLine();
                iteration ++;
                continue;
            }
            //get the next line in the file
            currentLine = scanner.nextLine();
            //unmarshall the line into an order
            currentProduct = unmarshallProduct(currentLine);
            
            products.put(currentProduct.getProductType(), currentProduct);
            iteration++;
        }
        //Clean up/close file
        scanner.close();
    }
}
