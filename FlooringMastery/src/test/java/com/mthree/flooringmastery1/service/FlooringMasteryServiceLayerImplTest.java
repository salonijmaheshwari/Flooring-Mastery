/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mthree.flooringmastery1.service;

import com.mthree.flooringmastery1.dao.FlooringMasteryOrderDao;

import com.mthree.flooringmastery1.dao.FlooringMasteryPersistenceException;
import com.mthree.flooringmastery1.dao.FlooringMasteryProductDao;

import com.mthree.flooringmastery1.dao.FlooringMasteryTaxDao;

import com.mthree.flooringmastery1.dto.Order;
import com.mthree.flooringmastery1.dto.Product;
import com.mthree.flooringmastery1.dto.Tax;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author salon
 */
public class FlooringMasteryServiceLayerImplTest {

    private final FlooringMasteryServiceLayer testService;

    //Wire the service layer with the stub implementations of the Dao 
    FlooringMasteryOrderDao testOrderDao;
    FlooringMasteryProductDao testProductDao;
    FlooringMasteryTaxDao testTaxDao;
    String testDataExportFile = "BackupTest\\DataExportTest.txt";
    String testOrderFolder = "OrdersTest";
    String testProductsFile = "DataTest\\ProductsTest.txt";
    String testTaxesFile = "DataTest\\TaxesTest.txt";

    public FlooringMasteryServiceLayerImplTest() {
    testProductDao = new FlooringMasteryProductDaoStubImpl(testProductsFile);
    testTaxDao = new FlooringMasteryTaxDaoStubImpl(testTaxesFile);
    testOrderDao = new FlooringMasteryOrderDaoStubImpl(testDataExportFile,testOrderFolder);
    testService = new FlooringMasteryServiceLayerImpl(testOrderDao, testProductDao,testTaxDao);
    }
    
   /* public FlooringMasteryServiceLayerImplTest() {

        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        testService = ctx.getBean("serviceLayer", FlooringMasteryServiceLayer.class);
    }*/

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
    public void testCreateOrderFileNameFromDate() {
        //ARRANGE
        LocalDate date = LocalDate.parse("2000-12-06");

        //ACT
        String orderFileName = testService.createOrderFileNameFromDate(date);

        //ASSERT
        assertEquals(orderFileName, "Orders_12062000.txt", "The order file name generated was incorrect");
    }

    @Test
    public void testCheckOrderFileExists() throws FlooringMasteryPersistenceException {
        //ARRANGE
        int orderNum2 = 3;
        Order order2 = new Order(orderNum2);
        order2.stateTax = new Tax("WA");
        order2.product = new Product("Wood");
        order2.setCustomerName("Albert Einstein");
        order2.stateTax.setStateAbbr("WA");
        order2.product.setProductType("Wood");
        order2.stateTax.setTaxRate(new BigDecimal("9.25"));
        order2.setArea(new BigDecimal("243.00"));
        order2.product.setCostPerSquareFoot(new BigDecimal("5.15"));
        order2.product.setLaborCostPerSquareFoot(new BigDecimal("4.75"));
        order2.setMaterialCost(new BigDecimal("871.50"));
        order2.setLaborCost(new BigDecimal("1033.33"));
        order2.setTax(new BigDecimal("476.21"));
        order2.setTotal(new BigDecimal("2381.06"));
        String orderFileName = "Orders_06032013.txt";
        //ACT - add order to new file
        testOrderDao.addOrderToNewFile(orderFileName, orderNum2, order2);
        //ASSERT
        try {
            testService.checkOrderFileExists(orderFileName);
        } catch (FlooringMasteryNoOrdersException e) {
            //If an exception was thrown, then the test will fail, if not then it passed.
            fail("Order file did exist. No Exception have been thrown");
        }
        //TEAR DOWN 
        File directory = new File(testOrderFolder);
        File[] files = directory.listFiles();
        for (File file : files) {
            file.delete();
        }

    }

    @Test
    public void testCheckOrderFileDoesNotExist() {
        //ACT - add order to new file
        String orderFileName = "Orders_06032030.txt";

        try {
            testService.checkOrderFileExists(orderFileName);
            //the file does not exist so an exception should be thrown.
            //if the below line is reached, then an exception was not thrown and the test should fail.
            fail("Order file did not exist. Exception shuld have been thrown");
        } catch (FlooringMasteryNoOrdersException e) {
        }
    }

    @Test
    public void testCheckDateIsInFuture() {
        //ARRANGE
        LocalDate todaysDate = LocalDate.now();
        LocalDate aFutureDate = todaysDate.plusDays(10);
        LocalDate aPastDate = todaysDate.minusDays(10);

        //ACT - valid date
        try {
            testService.checkDateIsInFuture(aFutureDate);
        } catch (FlooringMasteryDateErrorException e) {
            //ASSERT
            fail("This date was in the future, no exception should have been thrown.");
        }
        //ACT - invalid date
        try {
            testService.checkDateIsInFuture(aPastDate);
            //ASSERT
            fail("This date was in the past, an exception should have been thrown.");
        } catch (FlooringMasteryDateErrorException e) {
        }
    }

    @Test
    public void testInvalidCustomerName() {
        //ARRANGE
        //First, test invalid customer names
        String customerName = "";

        //ACT
        try {
            testService.validateCustomerName(customerName);
            //ASSERT
            fail("Customer's name is invalid. Exception should have been thrown.");
        } catch (FlooringMasteryCustomerNameErrorException e) {
        }
    }

    @Test
    public void testInvalidCustomerName2() {
        //ARRANGE
        //First, test invalid customer names
        String customerName = "        ";

        //ACT
        try {
            testService.validateCustomerName(customerName);
            //ASSERT
            fail("Customer's name is invalid. Exception should have been thrown.");
        } catch (FlooringMasteryCustomerNameErrorException e) {
        }
    }

    @Test
    public void testValidCustomerName2() {
        //ARRANGE
        //First, test invalid customer names
        String customerName = "Chloe";

        //ACT
        try {
            testService.validateCustomerName(customerName);
        } catch (FlooringMasteryCustomerNameErrorException e) {
            //ASSERT
            fail("Customer's name is valid. Exception should not have been thrown.");
        }
    }

    @Test
    public void testGetCustomerNamePlaceHolder() {
        //ARRANGE
        String name = "Esme, Inc";
        //ACT
        String nameWithPlaceHolder = testService.getCustomerNamePlaceHolder(name);
        //ASSERT
        assertEquals(nameWithPlaceHolder, "Esme#*~ Inc", "The name should include the placeholder in "
                + "replace of comma");
    }

    @Test
    public void testCheckValidStateAgainstTaxFile() throws FlooringMasteryPersistenceException {
        //ARRANGE
        String stateAbbr = "TX";

        try {
            //ACT
            testService.checkStateAgainstTaxFile(stateAbbr);
        } catch (FlooringMasteryStateNotFoundException e) {
            fail("The state with abbreviation TX exists. Exception should not have"
                    + "been thrown.");
        }
    }

    @Test
    public void testCheckInvalidStateAgainstTaxFile() throws FlooringMasteryPersistenceException {
        //ARRANGE
        String stateAbbr = "NY";

        try {
            //ACT
            testService.checkStateAgainstTaxFile(stateAbbr);
            fail("The state with abbreviation NY does not exist. Exception should have"
                    + "been thrown.");
        } catch (FlooringMasteryStateNotFoundException e) {
        }
    }

    @Test
    public void testGetAllProducts() throws FlooringMasteryPersistenceException {
        //ARRANGE
        Product carpetClone = new Product("Carpet");
        carpetClone.setCostPerSquareFoot(new BigDecimal("2.25"));
        carpetClone.setLaborCostPerSquareFoot(new BigDecimal("2.10"));

        //ACT & ASSERT
        assertEquals(4, testService.getAllProducts().size(), "Should have 4 products");
        assertTrue(testService.getAllProducts().contains(carpetClone), "Product list should contain Kentucky.");
    }

    @Test
    public void testCheckValidProductTypeAgainstProductsFile() throws FlooringMasteryPersistenceException {
        //ARRANGE
        String productType = "Wood";

        //ACT
        try {
            testService.checkProductTypeAgainstProductsFile(productType);
        } catch (FlooringMasteryProductTypeNotFoundException e) {
            //ASSERT
            fail("Wood does exist, exception should not have been thrown.");
        }
    }

    @Test
    public void testCheckInvalidProductTypeAgainstProductsFile() throws FlooringMasteryPersistenceException {
        //ARRANGE
        String productType = "Luxury carpet";

        //ACT
        try {
            testService.checkProductTypeAgainstProductsFile(productType);
            //ASSERT
            fail("Luxury carpet does not exist, exception should have been thrown.");
        } catch (FlooringMasteryProductTypeNotFoundException e) {
        }
    }

    @Test
    public void testGetProduct() throws FlooringMasteryPersistenceException {
        //ARRANGE
        String productType = "Carpet";
        Product carpetClone = new Product("Carpet");
        carpetClone.setCostPerSquareFoot(new BigDecimal("2.25"));
        carpetClone.setLaborCostPerSquareFoot(new BigDecimal("2.10"));
       
        //ACT
        Product retrievedProduct = testService.getProduct(productType);

        //ASSERT
        assertEquals(retrievedProduct, carpetClone, "The product retrieved should be"
                + "Carpet");
    }

    @Test
    public void testCalculateMaterialCost() {
        //ARRANGE
        BigDecimal area = new BigDecimal("4");
        BigDecimal costPerSquareFoot = new BigDecimal("10");

        //ACT
        BigDecimal materialCost = testService.calculateMaterialCost(area, costPerSquareFoot);

        //ASSERT
        assertEquals(materialCost, new BigDecimal("40.00"), "Material cost should be 40.00");
    }

    @Test
    public void testCalculateLaborCost() {
        //ARRANGE
        BigDecimal area = new BigDecimal("4");
        BigDecimal laborCostPerSquareFoot = new BigDecimal("50");

        //ACT
        BigDecimal laborCost = testService.calculateMaterialCost(area, laborCostPerSquareFoot);

        //ASSERT
        assertEquals(laborCost, new BigDecimal("200.00"), "labor cost should be 200.00");
    }

    @Test
    public void testCalculateTax() {
        //ARRANGE
        BigDecimal materialCost = new BigDecimal("4");
        BigDecimal laborCost = new BigDecimal("50");
        BigDecimal taxRate = new BigDecimal("7.50");

        //ACT
        BigDecimal tax = testService.calculateTax(materialCost, laborCost, taxRate);

        //ASSERT
        assertEquals(tax, new BigDecimal("4.05"), "Tax should be 4.05");
    }

    @Test
    public void testCalculateTotal() {
        //ARRANGE
        BigDecimal materialCost = new BigDecimal("5.00");
        BigDecimal laborCost = new BigDecimal("75.25");
        BigDecimal tax = new BigDecimal("9.25");

        //ACT
        BigDecimal total = testService.calculateTotal(materialCost, laborCost, tax);

        //ASSERT
        assertEquals(total, new BigDecimal("89.50"), "The total should be 89.50");

    }

    @Test
    public void testCheckAreaOverMinOrder() {
        //ARRANGE
        BigDecimal areaOverMin = new BigDecimal("101");
        BigDecimal areaBelowMin = new BigDecimal("99");
        BigDecimal areaMin = new BigDecimal("100");

        //ACT - invalid area (below the min)
        try {
            testService.checkAreaOverMinOrder(areaBelowMin);
            fail("The area is below the minimum, exception should have been"
                    + "thrown");
        } catch (FlooringMasteryAreaBelowMinException e) {
        }

        //ACT - valid area (over the min)
        try {
            testService.checkAreaOverMinOrder(areaOverMin);
        } catch (FlooringMasteryAreaBelowMinException e) {
            fail("The area is above the minimum, exception should not have been"
                    + "thrown");
        }
        //ACT - valid area (the min)
        try {
            testService.checkAreaOverMinOrder(areaMin);
        } catch (FlooringMasteryAreaBelowMinException e) {
            fail("The area is the minimum, exception should not have been"
                    + "thrown");
        }
    }

    @Test
    public void testGetTax() throws FlooringMasteryPersistenceException {
        //ARRANGE
        Tax kentuckyClone = new Tax("KY");
        kentuckyClone.setStateName("Kentucky");
        kentuckyClone.setTaxRate(new BigDecimal("6.00"));

        Tax retrievedTax = testService.getTax("KY");

        //ASSERT
        assertEquals(retrievedTax, kentuckyClone, "The retrieved tax should be "
                + "kentucky");
    }

    @Test
    public void testGenerateNewOrderNum() throws FlooringMasteryPersistenceException {
        //ARRANGE
        int orderNum = 1;
        Order order = new Order(orderNum);
        order.stateTax = new Tax("WA");
        order.product = new Product("Wood");
        order.setCustomerName("Albert Einstein");
        order.stateTax.setStateAbbr("WA");
        order.product.setProductType("Wood");
        order.stateTax.setTaxRate(new BigDecimal("9.25"));
        order.setArea(new BigDecimal("243.00"));
        order.product.setCostPerSquareFoot(new BigDecimal("5.15"));
        order.product.setLaborCostPerSquareFoot(new BigDecimal("4.75"));
        order.setMaterialCost(new BigDecimal("871.50"));
        order.setLaborCost(new BigDecimal("1033.33"));
        order.setTax(new BigDecimal("476.21"));
        order.setTotal(new BigDecimal("2381.06"));

        int orderNum2 = 2;
        Order order2 = new Order(orderNum2);
        order2.stateTax = new Tax("WA");
        order2.product = new Product("Wood");
        order2.setCustomerName("Albert Einstein");
        order2.stateTax.setStateAbbr("WA");
        order2.product.setProductType("Wood");
        order2.stateTax.setTaxRate(new BigDecimal("9.25"));
        order2.setArea(new BigDecimal("243.00"));
        order2.product.setCostPerSquareFoot(new BigDecimal("5.15"));
        order2.product.setLaborCostPerSquareFoot(new BigDecimal("4.75"));
        order2.setMaterialCost(new BigDecimal("871.50"));
        order2.setLaborCost(new BigDecimal("1033.33"));
        order2.setTax(new BigDecimal("476.21"));
        order2.setTotal(new BigDecimal("2381.06"));

        int orderNum3 = 3;
        Order order3 = new Order(orderNum3);
        order3.stateTax = new Tax("WA");
        order3.product = new Product("Wood");
        order3.setCustomerName("Albert Einstein");
        order3.stateTax.setStateAbbr("WA");
        order3.product.setProductType("Wood");
        order3.stateTax.setTaxRate(new BigDecimal("9.25"));
        order3.setArea(new BigDecimal("243.00"));
        order3.product.setCostPerSquareFoot(new BigDecimal("5.15"));
        order3.product.setLaborCostPerSquareFoot(new BigDecimal("4.75"));
        order3.setMaterialCost(new BigDecimal("871.50"));
        order3.setLaborCost(new BigDecimal("1033.33"));
        order3.setTax(new BigDecimal("476.21"));
        order3.setTotal(new BigDecimal("2381.06"));

        int orderNum4 = 4;
        Order order4 = new Order(orderNum4);
        order4.stateTax = new Tax("WA");
        order4.product = new Product("Wood");
        order4.setCustomerName("Albert Einstein");
        order4.stateTax.setStateAbbr("WA");
        order4.product.setProductType("Wood");
        order4.stateTax.setTaxRate(new BigDecimal("9.25"));
        order4.setArea(new BigDecimal("243.00"));
        order4.product.setCostPerSquareFoot(new BigDecimal("5.15"));
        order4.product.setLaborCostPerSquareFoot(new BigDecimal("4.75"));
        order4.setMaterialCost(new BigDecimal("871.50"));
        order4.setLaborCost(new BigDecimal("1033.33"));
        order4.setTax(new BigDecimal("476.21"));
        order4.setTotal(new BigDecimal("2381.06"));

        //Add orders 1 & 2 to the same file, then create new files for 3 & 4
        testOrderDao.addOrderToNewFile("Orders_06042013.txt", orderNum, order);
        testOrderDao.addOrderToExistingFile("Orders_06042013.txt", orderNum2, order2);
        testOrderDao.addOrderToNewFile("Orders_06052013.txt", orderNum3, order3);
        testOrderDao.addOrderToNewFile("Orders_06062013.txt", orderNum4, order4);

        //ACT
        int orderNumTest = testService.generateNewOrderNum();

        //ASSERT
        assertEquals(orderNumTest, 5, "The next order number should be 5.");

        //TEAR DOWN 
        File directory = new File(testOrderFolder);
        File[] files = directory.listFiles();
        for (File file : files) {
            file.delete();
        }
    }

    @Test
    public void testCheckForEdit() {
        //ARRANGE
        String updatedInfoEmpty = "";
        String updatedInfoNull = null;
        String updatedInfoBlank = "                ";
        String updatedInfoValid = "Saloni";

        //ACT
        String updated1 = testService.checkForEdit(updatedInfoEmpty);
        String updated2 = testService.checkForEdit(updatedInfoNull);
        String updated3 = testService.checkForEdit(updatedInfoBlank);
        String updated4 = testService.checkForEdit(updatedInfoValid);

        //ASSERT
        assertEquals(updated1, null, "updated1 should be null");
        assertEquals(updated2, null, "updated2 should be null");
        assertEquals(updated3, null, "updated3 should be null");
        assertEquals(updated4, updatedInfoValid, "updated4 should be Saloni");

    }

}
