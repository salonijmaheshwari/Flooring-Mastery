/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mthree.flooringmastery1.dao;

import com.mthree.flooringmastery1.dto.Product;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author salon
 */
public class FlooringMasteryProductDaoFileImplTest {
    
    //To test product and tax DAO, as these files are only read from I have copied the files to a new test folder
    //to read from.
    
    String testProductsFile = "DataTest\\ProductsTest.txt";
    FlooringMasteryProductDao testProductDao = new FlooringMasteryProductDaoFileImpl(testProductsFile);
    
    
    public FlooringMasteryProductDaoFileImplTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testSomeMethod() {
        
    }
    
     @Test
    public void testGetProduct() throws FlooringMasteryPersistenceException{
        //ARRANGE
        
        
        Product laminate = new Product("Laminate");
        laminate.setCostPerSquareFoot(new BigDecimal ("1.75"));
        laminate.setLaborCostPerSquareFoot(new BigDecimal ("2.10"));
        
        //ACT
        Product retrievedProduct = testProductDao.getProduct("Laminate");
        
        //ASSERT
        assertEquals(retrievedProduct,laminate,"The retrieved product should be equal to the carpet product.");
        
    }
    
    @Test
    public void testGetAllProducts() throws FlooringMasteryPersistenceException {
        //ARRANGE
        Product carpet = new Product("Carpet");
        carpet.setCostPerSquareFoot(new BigDecimal ("2.25"));
        carpet.setLaborCostPerSquareFoot(new BigDecimal ("2.10"));
        
        Product laminate = new Product("Laminate");
        laminate.setCostPerSquareFoot(new BigDecimal ("1.75"));
        laminate.setLaborCostPerSquareFoot(new BigDecimal ("2.10"));
        
        Product tile = new Product("Tile");
        tile.setCostPerSquareFoot(new BigDecimal ("3.50"));
        tile.setLaborCostPerSquareFoot(new BigDecimal ("4.15"));
        
        Product wood = new Product("Wood");
        wood.setCostPerSquareFoot(new BigDecimal ("5.15"));
        wood.setLaborCostPerSquareFoot(new BigDecimal ("4.75"));
        
        
        //TESTs = getAllProducts()
        List<Product> allProducts = testProductDao.getAllProducts();

        //ASSERT
        assertTrue(allProducts.contains(carpet) && allProducts.contains(laminate)&& allProducts.contains(tile) && allProducts.contains(wood),"The "
                + "all products list should contain carpet, laminate, tile and wood");
        assertEquals(allProducts.size(),4,"The all products list should contain 4 products");
    }
    
}
