/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mthree.flooringmastery1;

import com.mthree.flooringmastery1.controller.FlooringMasteryController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author salon
 */
public class App {
     public static void main(String[] args) {
//    UserIO io = new UserIOConsoleImpl();
//    FlooringMasteryView view = new FlooringMasteryView(io);
//    FlooringMasteryDao dao = new FlooringMasteryDaoFileImpl();
//    FlooringMasteryServiceLayer service = new FlooringMasteryServiceLayerImpl(dao);
//    
//    FlooringMasteryController controller = new FlooringMasteryController(view, service);
//    
//    controller.run();

    
    ApplicationContext ctx = 
            new ClassPathXmlApplicationContext("applicationContext.xml");
    FlooringMasteryController controller = 
            ctx.getBean("controller", FlooringMasteryController.class);
    controller.run();
    }
}
