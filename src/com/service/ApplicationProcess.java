/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.service;

import com.connection.SQLManager;
import com.dao.NumberDataDAO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.net.www.http.HttpClient;

/**
 *
 * @author Hasanka
 */
public class ApplicationProcess {

    private static Connection connection;
    private static int count=0;

    //get the CLI from data base for push 
    public static List<NumberDataDAO> getNumberDetrails() {
        List<NumberDataDAO> numberDetails = new ArrayList();
        try {
            connection = SQLManager.getConnection();
            PreparedStatement prepareStatement = connection.prepareStatement("SELECT sno, mobile FROM pushussd_numberdata WHERE status = 0 LIMIT 500 FOR UPDATE SKIP LOCKED");
            ResultSet resultSet = prepareStatement.executeQuery();
            while (resultSet.next()) {

                int sno = resultSet.getInt(1);
                String mobileNumber = resultSet.getString(2);
                NumberDataDAO numberDataDAO = new NumberDataDAO(sno, mobileNumber);
                //Get the 500 of user numbers and snos' add arraylist
                numberDetails.add(numberDataDAO);

            }

        } catch (SQLException ex) {
            Logger.getLogger(ApplicationProcess.class.getName()).log(Level.SEVERE, null, ex);
        }finally {

            if (connection != null) {

                try {
                    connection.close();
                } catch (SQLException ex) {
                    Logger.getLogger(ApplicationProcess.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return numberDetails;
    }
    //calling hutch API for push CLI
    public static boolean callApi(NumberDataDAO numberDataDAO) {

       // String baseURL = "http://192.168.148.37:5522/nipush.php?USER=WifiCRBT&PASSWORD=Hutch@123&MSISDN=" + numberDataDAO.getNumber() + "&MENU=691";
        String baseURL = "http://localhost:8080/api/v1/customer/get-by-id?id=1";

        try {
            URL url = new URL(baseURL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();
            System.out.println(responseCode);
            InputStream is = urlConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = br.readLine();
            String response = "";
            while (line != null) {
                line = br.readLine();
                response += line;
            }
            //Checking the response for identify if that reponse coming from hutch
            if (response.contains("ACCEPTED") || responseCode == 200) {
                 count=count+1;
                 System.out.println("hutch response count : "+count);
                return true;
            }
            System.out.println(response);

        } catch (MalformedURLException ex) {
            Logger.getLogger(ApplicationProcess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ApplicationProcess.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    //After successfully push the CLI for hutch API then update the CLI status for 1
    public static boolean updateNumberStatus(NumberDataDAO numberDataDAO) {

        try {
            connection = SQLManager.getConnection();
            PreparedStatement prepareStatement = connection.prepareStatement("update pushussd_numberdata set status=1,updateTime=now() where mobile=" + numberDataDAO.getNumber() + " and sno=" + numberDataDAO.getSno());
            int resultSet = prepareStatement.executeUpdate();
            if (resultSet == 1) {

                System.out.println("Number status set to 1");
                return true;
            }

        } catch (SQLException ex) {
            Logger.getLogger(ApplicationProcess.class.getName()).log(Level.SEVERE, null, ex);
        }finally {

            if (connection != null) {

                try {
                    connection.close();
                } catch (SQLException ex) {
                    Logger.getLogger(ApplicationProcess.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return false;
    }

    //This is the method get the all CLI and its' sno as a arraylist and pass for push for hutch API and set the status
    public static void startProcess(NumberDataDAO numberDataDAO) {
      
        if (callApi(numberDataDAO)) {

            updateNumberStatus(numberDataDAO);
            System.out.println("start process callApi");
        } else {

            System.out.println("API request Failed");

        }

    }

}
