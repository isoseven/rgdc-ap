package com.stkych.rivergreenap.archive.test;

import com.stkych.rivergreenap.RiverGreenDB;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Test class to verify that database updates are working correctly.
 * This class tests the executeUpdateQueries method in RiverGreenDB.
 */
public class DatabaseUpdateTest {

    public static void main(String[] args) {
        System.out.println("Starting DatabaseUpdateTest...");

        try {
            // Test database connection
            testDatabaseConnection();

            // Test a simple update query
            testSimpleUpdate();

            // Test updating a procedure in the procedurelog table
            testProcedureLogUpdate();

            // Test updating a procedure through treatplanattach
            testTreatPlanAttachUpdate();

            System.out.println("All tests completed successfully!");
        } catch (Exception e) {
            System.err.println("Test failed with exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Tests the database connection.
     */
    private static void testDatabaseConnection() throws SQLException {
        System.out.println("\nTesting database connection...");

        try (Connection conn = RiverGreenDB.getConnection()) {
            System.out.println("Database connection successful!");

            // Get database metadata
            String dbName = conn.getCatalog();
            System.out.println("Connected to database: " + dbName);

            // Test if we can query the procedurelog table
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM procedurelog")) {

                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("Number of records in procedurelog table: " + count);
                }
            }

            // Test if we can query the treatplanattach table
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM treatplanattach")) {

                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("Number of records in treatplanattach table: " + count);
                }
            }
        }
    }

    /**
     * Tests a simple update query.
     */
    private static void testSimpleUpdate() throws SQLException {
        System.out.println("\nTesting simple update query...");

        // Create a test query that updates a dummy value
        // This is just to test if the update mechanism works
        List<String> queries = new ArrayList<>();
        queries.add("UPDATE procedurelog SET ProcNote = 'Test note from DatabaseUpdateTest' WHERE ProcNum = (SELECT MIN(ProcNum) FROM procedurelog)");

        // Execute the query
        Map<String, Object> results = RiverGreenDB.executeUpdateQueries(queries);

        // Print results
        int successCount = (int) results.get("successCount");
        int failureCount = (int) results.get("failureCount");
        String status = (String) results.get("status");

        System.out.println("Update results:");
        System.out.println("Status: " + status);
        System.out.println("Success count: " + successCount);
        System.out.println("Failure count: " + failureCount);

        if (successCount > 0) {
            System.out.println("Simple update test passed!");
        } else {
            throw new SQLException("Simple update test failed: " + results.get("errorMessages"));
        }
    }

    /**
     * Tests updating a procedure in the procedurelog table.
     */
    private static void testProcedureLogUpdate() throws SQLException {
        System.out.println("\nTesting procedurelog update...");

        // Get the first procedure from the database
        int procNum;
        int originalPriority;

        try (Connection conn = RiverGreenDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ProcNum, Priority FROM procedurelog LIMIT 1")) {

            if (rs.next()) {
                procNum = rs.getInt("ProcNum");
                originalPriority = rs.getInt("Priority");
                System.out.println("Found procedure #" + procNum + " with priority: " + originalPriority);
            } else {
                throw new SQLException("No procedures found in the database");
            }
        }

        // Create an update query to change the priority
        List<String> queries = new ArrayList<>();
        int newPriority = 150; // Using 150 as a test priority as suggested in the issue description
        queries.add("UPDATE procedurelog SET Priority = " + newPriority + " WHERE ProcNum = " + procNum);

        // Execute the query
        Map<String, Object> results = RiverGreenDB.executeUpdateQueries(queries);

        // Print results
        int successCount = (int) results.get("successCount");
        int failureCount = (int) results.get("failureCount");
        String status = (String) results.get("status");

        System.out.println("Update results:");
        System.out.println("Status: " + status);
        System.out.println("Success count: " + successCount);
        System.out.println("Failure count: " + failureCount);

        // Verify that the update was successful
        try (Connection conn = RiverGreenDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Priority FROM procedurelog WHERE ProcNum = " + procNum)) {

            if (rs.next()) {
                int updatedPriority = rs.getInt("Priority");
                System.out.println("Updated priority: " + updatedPriority);

                if (updatedPriority == newPriority) {
                    System.out.println("Procedurelog update test passed!");
                } else {
                    throw new SQLException("Procedurelog update test failed: Priority was not updated");
                }
            } else {
                throw new SQLException("Procedure not found after update");
            }
        }

        // Reset the priority to its original value
        queries.clear();
        queries.add("UPDATE procedurelog SET Priority = " + originalPriority + " WHERE ProcNum = " + procNum);
        RiverGreenDB.executeUpdateQueries(queries);
    }

    /**
     * Tests updating a procedure through treatplanattach.
     */
    private static void testTreatPlanAttachUpdate() throws SQLException {
        System.out.println("\nTesting treatplanattach update...");

        // Find a procedure that is linked to a treatment plan
        int procNum;
        int treatPlanNum;
        int originalPriority;

        try (Connection conn = RiverGreenDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT p.ProcNum, p.Priority, t.TreatPlanNum " +
                 "FROM procedurelog p " +
                 "JOIN treatplanattach t ON p.ProcNum = t.ProcNum " +
                 "LIMIT 1")) {

            if (rs.next()) {
                procNum = rs.getInt("ProcNum");
                treatPlanNum = rs.getInt("TreatPlanNum");
                originalPriority = rs.getInt("Priority");
                System.out.println("Found procedure #" + procNum + " linked to treatment plan #" + treatPlanNum + " with priority: " + originalPriority);
            } else {
                System.out.println("No procedures linked to treatment plans found in the database. Skipping test.");
                return;
            }
        }

        // Create update queries for both procedurelog and treatplanattach
        List<String> queries = new ArrayList<>();
        int newPriority = 150; // Using 150 as a test priority as suggested in the issue description

        // Update procedurelog
        queries.add("UPDATE procedurelog SET Priority = " + newPriority + " WHERE ProcNum = " + procNum);

        // Update treatplanattach with the new priority
        queries.add("UPDATE treatplanattach SET Priority = " + newPriority + " WHERE ProcNum = " + procNum);

        // Execute the queries
        Map<String, Object> results = RiverGreenDB.executeUpdateQueries(queries);

        // Print results
        int successCount = (int) results.get("successCount");
        int failureCount = (int) results.get("failureCount");
        String status = (String) results.get("status");

        System.out.println("Update results:");
        System.out.println("Status: " + status);
        System.out.println("Success count: " + successCount);
        System.out.println("Failure count: " + failureCount);

        // Verify that the update was successful in procedurelog
        try (Connection conn = RiverGreenDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Priority FROM procedurelog WHERE ProcNum = " + procNum)) {

            if (rs.next()) {
                int updatedPriority = rs.getInt("Priority");
                System.out.println("Updated priority in procedurelog: " + updatedPriority);

                if (updatedPriority == newPriority) {
                    System.out.println("Procedurelog update test passed!");
                } else {
                    throw new SQLException("Procedurelog update test failed: Priority was not updated");
                }
            } else {
                throw new SQLException("Procedure not found after update");
            }
        }

        // Verify that the update was successful in treatplanattach
        try (Connection conn = RiverGreenDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Priority FROM treatplanattach WHERE ProcNum = " + procNum)) {

            if (rs.next()) {
                int updatedPriority = rs.getInt("Priority");
                System.out.println("Updated priority in treatplanattach: " + updatedPriority);

                if (updatedPriority == newPriority) {
                    System.out.println("Treatplanattach update test passed!");
                } else {
                    throw new SQLException("Treatplanattach update test failed: Priority was not updated");
                }
            } else {
                throw new SQLException("Procedure not found in treatplanattach after update");
            }
        }

        // Reset the priority to its original value in both tables
        queries.clear();
        queries.add("UPDATE procedurelog SET Priority = " + originalPriority + " WHERE ProcNum = " + procNum);
        queries.add("UPDATE treatplanattach SET Priority = " + originalPriority + " WHERE ProcNum = " + procNum);
        RiverGreenDB.executeUpdateQueries(queries);
    }
}
