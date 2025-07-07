package com.stkych.rivergreenap;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Simple test class to verify database connectivity.
 * This class can be used to test if the MySQL connection is working properly.
 */
public class DatabaseConnectionTest {

    /**
     * Main method to test the database connection.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {    
        System.out.println("Testing database connection...");
        
        try {
            // Attempt to get a connection
            Connection conn = RiverGreenDB.getConnection();
            
            // If we get here, the connection was successful
            System.out.println("Database connection successful!");
            System.out.println("Connection details: " + conn.getMetaData().getURL());
            System.out.println("Database product name: " + conn.getMetaData().getDatabaseProductName());
            System.out.println("Database product version: " + conn.getMetaData().getDatabaseProductVersion());
            
            // Close the connection
            conn.close();
            System.out.println("Connection closed successfully.");
            
        } catch (SQLException e) {
            // If we get here, the connection failed
            System.err.println("Database connection failed!");
            System.err.println("Error message: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error code: " + e.getErrorCode());
            
            // Print the stack trace for detailed debugging
            e.printStackTrace();
        }
    }
}