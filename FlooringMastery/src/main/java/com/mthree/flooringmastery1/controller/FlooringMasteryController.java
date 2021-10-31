/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mthree.flooringmastery1.controller;

import com.mthree.flooringmastery1.dao.FlooringMasteryPersistenceException;
import com.mthree.flooringmastery1.dto.Order;
import com.mthree.flooringmastery1.dto.Product;
import com.mthree.flooringmastery1.dto.Tax;
import com.mthree.flooringmastery1.service.FlooringMasteryAreaBelowMinException;
import com.mthree.flooringmastery1.service.FlooringMasteryCustomerNameErrorException;
import com.mthree.flooringmastery1.service.FlooringMasteryDateErrorException;
import com.mthree.flooringmastery1.service.FlooringMasteryNoOrderNumException;
import com.mthree.flooringmastery1.service.FlooringMasteryNoOrdersException;
import com.mthree.flooringmastery1.service.FlooringMasteryOrderFileNotExistException;
import com.mthree.flooringmastery1.service.FlooringMasteryProductTypeNotFoundException;
import com.mthree.flooringmastery1.service.FlooringMasteryServiceLayer;
import com.mthree.flooringmastery1.service.FlooringMasteryStateNotFoundException;
import com.mthree.flooringmastery1.ui.FlooringMasteryView;
import com.mthree.flooringmastery1.ui.UserIO;
import com.mthree.flooringmastery1.ui.UserIOConsoleImpl;
import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author salon
 */
public class FlooringMasteryController {

    private UserIO io = new UserIOConsoleImpl();
    private FlooringMasteryView view;
    private FlooringMasteryServiceLayer service;

    public FlooringMasteryController(FlooringMasteryView view, FlooringMasteryServiceLayer service) {
        this.view = view;
        this.service = service;
    }

    public void run() {
        boolean keepGoing = true;
        int menuSelection;
        while (keepGoing) {
            try {

                menuSelection = getMenuSelection();
                //menuSelection = 1;
                switch (menuSelection) {
                    case 1:
                        listOrders();
                        break;
                    case 2:
                        createOrder();
                        break;
                    case 3:
                        editOrder();
                        break;
                    case 4:
                        removeOrder();
                        break;
                    case 5:
                        exportAllData();
                        break;
                    case 6:
                        keepGoing = false;
                        break;
                    default:
                        unknownCommand();
                }
            } catch (FlooringMasteryPersistenceException
                    | FlooringMasteryNoOrdersException
                    | FlooringMasteryDateErrorException
                    | FlooringMasteryCustomerNameErrorException
                    | FlooringMasteryStateNotFoundException
                    | FlooringMasteryProductTypeNotFoundException
                    | FlooringMasteryAreaBelowMinException
                    | FlooringMasteryNoOrderNumException
                    | FlooringMasteryOrderFileNotExistException e) {
                view.displayErrorMessage(e.getMessage());
            }

        }
        exitMessage();
    }

    private int getMenuSelection() {
        return view.printMenuAndGetSelection();
    }

    private void listOrders() throws FlooringMasteryNoOrdersException, FlooringMasteryPersistenceException {
        view.displayListOrdersBanner();
        boolean hasErrors = false;
        List<Order> orderList = null;
        do {
            try {
                //get the order date from the user
                LocalDate wantedOrderDate = view.getOrderDateListOrders();
                //Check orders exist for that date and return an order list
                orderList = service.getOrderList(wantedOrderDate);
                view.displayOrderListBanner(wantedOrderDate);
                //String enter = view.displayOrderList(orderList);
                hasErrors = false;
            } catch (DateTimeException | FlooringMasteryNoOrdersException | FlooringMasteryPersistenceException e) {
                hasErrors = true;
                view.displayErrorMessage(e.getMessage());
            }
        } while (hasErrors);
        view.displayOrderList(orderList);

//    String fileWithDate = service.createOrderFileNameFromDate(wantedOrderDate);
//    service.checkOrderFileExists(fileWithDate);
//    List<Order> orderList = service.getAllOrders(fileWithDate);
    }

    private void createOrder() throws
            FlooringMasteryDateErrorException,
            FlooringMasteryCustomerNameErrorException,
            FlooringMasteryPersistenceException,
            FlooringMasteryStateNotFoundException,
            FlooringMasteryProductTypeNotFoundException,
            FlooringMasteryAreaBelowMinException {
        view.displayCreateOrderBanner();
        boolean hasErrors = false;

        do {
            try {
                //ORDER DATE
                //get the order date from the user and valid the format
                LocalDate orderDateInput = view.getOrderDateCreateOrder();
                //Check that the order date is is in the future
                service.checkDateIsInFuture(orderDateInput);

                //CUSTOMER NAME
                //Get the customer's name
                String customerNameInput = view.getCustomerName();  //Special chars allowed **************************************************
                //Validate that the name is not null/empty
                service.validateCustomerName(customerNameInput);//count the number of commas?
                //replace any commas with a placeholder
                String customerNameInputPh = service.getCustomerNamePlaceHolder(customerNameInput);

                //STATE
                //Get the state abbreviation
                String stateAbbreviationInput = view.getStateAbbreviation();
                //Check that the state exists in the taxes file
                service.checkStateAgainstTaxFile(stateAbbreviationInput);

                //PRODUCT TYPE 
                //Show list of available products and pricing to choose from and get selection
                List<Product> availableProductsAndPricing = service.getAllProducts();
                //String productTypeInput = "Laminate";
                String productTypeInput = view.displayAvailableProductsAndGetSelection(availableProductsAndPricing);
                //Check that the product type selected is in the options
                service.checkProductTypeAgainstProductsFile(productTypeInput);
                Product productSelected = service.getProduct(productTypeInput);
               
                //AREA
                //Get the area, minimum order size is 100 sqft
                //BigDecimal areaInput = new BigDecimal("102");
                BigDecimal areaInput = view.getArea();
                //check the area is over 100 sqft
                service.checkAreaOverMinOrder(areaInput);

                //CALCULATIONS - MaterialCost, LaborCost, Tax, Total
                BigDecimal materialCost = service.calculateMaterialCost(areaInput, productSelected.getCostPerSquareFoot());
                BigDecimal laborCost = service.calculateLaborCost(areaInput, productSelected.getLaborCostPerSquareFoot());

                //use the stateAbbreviation to get the tax rate
                Tax taxObj = service.getTax(stateAbbreviationInput);
                BigDecimal tax = service.calculateTax(materialCost, laborCost, taxObj.getTaxRate());

                BigDecimal total = service.calculateTotal(materialCost, laborCost, tax);

                //Generate a new order number based on the max+1 of the order numbers already in the order files
                int orderNumber = service.generateNewOrderNum();

                //Display the order summary and obtain a Y/N if they want to place the order
                String verifyOrder = view.displayNewOrderSummary(orderDateInput, orderNumber, customerNameInput, stateAbbreviationInput,
                        productTypeInput, areaInput, materialCost, laborCost, tax, total);

                //If "Y", order will be placed and added to in-memory storage. It not, the user will be returned to the main 
                //menu        
                service.createNewOrderIfRequired(verifyOrder, orderDateInput, orderNumber, customerNameInputPh, stateAbbreviationInput,
                        tax, productTypeInput, areaInput, materialCost, laborCost, materialCost, laborCost, tax, total);

                view.displayCreateSuccessBanner(verifyOrder);

                hasErrors = false;
            } catch (FlooringMasteryDateErrorException
                    | FlooringMasteryCustomerNameErrorException
                    | FlooringMasteryPersistenceException
                    | FlooringMasteryStateNotFoundException
                    | FlooringMasteryProductTypeNotFoundException
                    | FlooringMasteryAreaBelowMinException e) {
                hasErrors = true;
                view.displayErrorMessage(e.getMessage());
            }
        } while (hasErrors);
    }

    private void removeOrder() throws FlooringMasteryNoOrdersException, FlooringMasteryPersistenceException, FlooringMasteryNoOrderNumException, FlooringMasteryOrderFileNotExistException {
        view.displayRemoveOrderBanner();
        boolean hasErrors = false;
        do {
            //Prompt user for the date & order number
            LocalDate orderDateInput = view.getOrderDateRemoveOrder();
            int orderNumberInput = view.getOrderNumberRemoveOrder();
            try {
                //Check that the order exists
                //First, check for the file using the orderDateInput
                String orderFileName = service.createOrderFileNameFromDate(orderDateInput);
                service.checkOrderFileExists(orderFileName);

                //If the file exists, check if the order with the specified order number exists in the file
                int orderNumToRemove = service.checkOrderNumExists(orderFileName, orderNumberInput);
                //If it doesn't exist, an exception is thrown. If it is, then remove the order.
                Order orderToRemove = service.getOrder(orderFileName, orderNumToRemove);

                //Display the order information
                view.displayOrderInformation(orderDateInput, orderToRemove);

                //Prompt the user if they are sure they want to remove the order
                String removeConfirmation = view.getRemoveConfirmation();

                //If they are sure, remove the order, if not return to menu. removedOrder will be null if no order to be removed.
                Order removedOrder = service.removeOrderIfConfirmed(removeConfirmation, orderFileName, orderNumberInput);

                view.displayRemoveSuccessBanner(removedOrder);
                hasErrors = false;
            } catch (FlooringMasteryNoOrdersException | FlooringMasteryPersistenceException | FlooringMasteryNoOrderNumException e) {
                hasErrors = true;
                view.displayErrorMessage(e.getMessage());
            }
        } while (hasErrors);
    }

    private void exportAllData() throws FlooringMasteryPersistenceException {
        //Load all data from every order and export all of that data to the DataExport.txt file in the BackUp folder    
        boolean hasErrors = false;
        view.displayExportAllDataBanner();
        try {
            service.exportAllData();
        } catch (FlooringMasteryPersistenceException e) {
            hasErrors = true;
            view.displayErrorMessage(e.getMessage());
        }
    }

    private void unknownCommand() {
        view.displayUnknownCommandBanner();
    }

    private void exitMessage() {
        view.displayExitBanner();
    }

    private void editOrder() throws
            FlooringMasteryNoOrdersException,
            FlooringMasteryNoOrderNumException,
            FlooringMasteryStateNotFoundException,
            FlooringMasteryProductTypeNotFoundException,
            FlooringMasteryAreaBelowMinException {
        view.displayEditOrderBanner();
        boolean hasErrors = false;
        do {
            //Ask the user for a date and order number
            //Prompt user for the date & order number
            LocalDate orderDateInput = view.getOrderDateEditOrder();
            int orderNumberInput = view.getOrderNumberEditOrder();

            try {
                //Check that the order exists
                //First, check for the file using the orderDateInput
                String orderFileName = service.createOrderFileNameFromDate(orderDateInput);
                service.checkOrderFileExists(orderFileName);

                //If the file exists, check if the order with the specified order number exists in the file
                int orderNumToEdit = service.checkOrderNumExists(orderFileName, orderNumberInput);
                //If it doesn't exist, an exception is thrown. If it is, then get the order.
                Order orderToEdit = service.getOrder(orderFileName, orderNumToEdit);

                //NAME
                //Get the new edit information and display the old in brackets
                String updatedCustomerName = view.displayAndGetEditCustomerName(orderToEdit);
                //Evaluate whether the update is empty or contains new info. If it contains new
                //info store it in the string, store null otherwise.
                updatedCustomerName = service.checkForEdit(updatedCustomerName);

                //If the string contains new info, update and return the order object with the the
                //new information. If it is null, return the order object unedited.
                Order updatedOrder = service.updateOrderCustomerNameIfRequired(updatedCustomerName, orderToEdit);

                //STATE
                //Get the new edit information and display the old in brackets 
                String updatedStateAbbreviation = view.displayAndGetEditState(orderToEdit);
                updatedStateAbbreviation = service.checkForEdit(updatedStateAbbreviation);
                //Check that the selected state exists if it has been changed.   --------not supposed to put if statement here, but have done to speed up
                if (updatedStateAbbreviation != null) {
                    service.checkStateAgainstTaxFile(updatedStateAbbreviation);
                }
                updatedOrder = service.updateOrderStateIfRequired(updatedStateAbbreviation, orderToEdit);

                //PRODUCT TYPE
                String updatedProductType = view.displayAndGetEditProductType(orderToEdit);
                updatedProductType = service.checkForEdit(updatedProductType);
                //If the product type has been changed, check against the product types to ensure it exists
                if (updatedProductType != null) {
                    service.checkProductTypeAgainstProductsFile(updatedProductType);
                }
                updatedOrder = service.updateOrderProductTypeIfRequired(updatedProductType, orderToEdit);

                //AREA
                String updatedAreaString = view.displayAndGetEditArea(orderToEdit);
                BigDecimal updatedArea = service.checkForEditBigDecimal(updatedAreaString);
                //if the area has been changed, check it is about the min order
                if (updatedArea != null) {
                    service.checkAreaOverMinOrder(updatedArea);
                }
                updatedOrder = service.updateOrderAreaIfRequired(updatedArea, orderToEdit);

                //Update the calculations
                updatedOrder = service.updateOrderCalculations(updatedOrder);

                //Display a summary of the new order info
                view.displayEditedOrderSummary(orderDateInput, orderToEdit);

                //Prompt for whether the edit should be saved
                String toBeEdited = view.getSaveConfirmation();

                //Edit the order in memory if confirmed
                Order editedOrder = service.editOrderIfConfirmed(toBeEdited, orderFileName, updatedOrder);

                //If editedOrder is not null, display the success banner
                view.displayEditSucessBanner(editedOrder);

                hasErrors = false;
            } catch (FlooringMasteryNoOrdersException
                    | FlooringMasteryNoOrderNumException
                    | FlooringMasteryPersistenceException
                    | FlooringMasteryStateNotFoundException
                    | FlooringMasteryProductTypeNotFoundException
                    | FlooringMasteryAreaBelowMinException e) {
                hasErrors = true;
                view.displayErrorMessage(e.getMessage());
            }
        } while (hasErrors);
    }
}
