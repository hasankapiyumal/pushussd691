/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.main;

import com.connection.SQLManager;
import com.dao.NumberDataDAO;
import com.service.ApplicationProcess;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Hasanka
 */
public class Main {

    public static void main(String[] args) {
        //This is used for create and handle Thread pool. Also there are 10 threads working 
        ExecutorService executorService = Executors.newFixedThreadPool(10);

       //NumberDataDAO arraylists are 
        List<NumberDataDAO> numberDetrails = ApplicationProcess.getNumberDetrails();
        for (NumberDataDAO dataDAO : numberDetrails) {
            //start the threads for the process
           executorService.submit(()->ApplicationProcess.startProcess(dataDAO));
            
            
        }
       
        //after completing all process the stops the threads
        executorService.shutdown();
        
        
    }

}


