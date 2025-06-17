package com.stkych.rivergreenap;

import com.stkych.rivergreenap.model.TreatmentPlanProcedure;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Database utility class for the RiverGreen Dental Application.
 * Provides methods to connect to the MySQL database and manipulate treatment plan procedures.
 */
public class RiverGreenDB {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/opendental?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "test";

    /**
     * Gets a connection to the MySQL database.
     *
     * @return A Connection object
     * @throws SQLException If a database error occurs
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Explicitly load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Attempt to establish connection
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
            throw new SQLException("MySQL JDBC Driver not found", e);
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Retrieves procedures for a patient's treatment plans.
     *
     * @param patientNumber The patient number
     * @return A list of TreatmentPlanProcedure objects
     * @throws SQLException If a database error occurs
     */
    public static @NotNull List<TreatmentPlanProcedure> getProceduresForPatient(int patientNumber) throws SQLException {
        // Create a list to hold the procedures for the patient
        List<TreatmentPlanProcedure> procedures = new ArrayList<>();

        // SQL Query retrieves:
        // - Tooth number
        // - Surface involved
        // - Procedure code (returns equivalent string)
        // - Diagnosis (returns equivalent string)
        // - Priority (returns equivalent string)
        // - Fee
        String sql = "SELECT pl.*, pc.ProcCode, pc.Descript, d1.ItemName AS PriorityName, d2.ItemName AS DiagnosisName FROM procedurelog pl " +
                     "LEFT JOIN procedurecode pc ON pl.CodeNum = pc.CodeNum " +
                     "LEFT JOIN definition d1 ON pl.Priority = d1.DefNum " +
                     "LEFT JOIN definition d2 ON pl.Dx = d2.DefNum " +
                     "WHERE pl.ProcNum IN " +
                     "(SELECT ProcNum FROM treatplanattach WHERE TreatPlanNum IN " +
                     "(SELECT TreatPlanNum FROM treatplan WHERE PatNum = ?))";

        // connection
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, patientNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Extract data from the result set
                    String toothNum = rs.getString("ToothNum");
                    String surface = rs.getString("Surf");
                    String procCode = rs.getString("ProcCode");
                    String description = rs.getString("Descript");
                    double fee = rs.getDouble("ProcFee");
                    int priorityNum = rs.getInt("Priority");
                    int procNum = rs.getInt("ProcNum");
                    String priority;

                    // Handle case where priority == 0
                    if (priorityNum == 0) {
                        priority = "None";
                    } else {
                        // Get the priority name from the result set
                        priority = rs.getString("PriorityName");
                        // If priority name is null, use "No priority" instead
                        if (priority == null) {
                            priority = "None";
                        }
                    }

                    // Get the diagnosis from the result set
                    int dxNum = rs.getInt("Dx");
                    String diagnosis;

                    // Handle case where Dx is 0
                    if (dxNum == 0) {
                        diagnosis = "No diagnosis";
                    } else {
                        // Get the diagnosis name from the result set
                        diagnosis = rs.getString("DiagnosisName");
                        // If diagnosis name is null, use the original Dx value as default
                        if (diagnosis == null) {
                            diagnosis = String.valueOf(dxNum);
                        }
                    }

                    // Create a new TreatmentPlanProcedure object, add it to the list
                    TreatmentPlanProcedure procedure = new TreatmentPlanProcedure(
                            priority, toothNum, surface, procCode, diagnosis, description, fee, procNum);
                    procedures.add(procedure);
                }
            }
        }

        return procedures;
    }

    /**
     * Retrieves procedures for a patient's treatment plans and returns them as an ObservableList.
     * This is a convenience method for JavaFX UI components.
     *
     * @param patientNumber The patient number
     * @return An ObservableList of TreatmentPlanProcedure objects
     * @throws SQLException If a database error occurs
     */
    public static ObservableList<TreatmentPlanProcedure> getProceduresForPatientObservable(int patientNumber) throws SQLException {
        List<TreatmentPlanProcedure> procedures = getProceduresForPatient(patientNumber);
        return FXCollections.observableArrayList(procedures);
    }

    /**
     * Retrieves all priorities from the database.
     * This method queries the definition table for all priority definitions.
     *
     * @return A list of priority names
     * @throws SQLException If a database error occurs
     */
    public static @NotNull List<String> getAllPriorities() throws SQLException {
        // Create a list to hold the priorities
        List<String> priorities = new ArrayList<>();

        // SQL Query to retrieve all priorities from the definition table
        // The definition table contains various types of definitions, including priorities
        // We filter for the category that contains priorities (Category = 20)
        String sql = "SELECT * FROM definition WHERE Category = 20";

        // Execute the query
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String priorityName = rs.getString("ItemName");
                if (priorityName != null && !priorityName.isEmpty()) {
                    priorities.add(priorityName);
                }
            }
        }

        return priorities;
    }

    /**
     * Retrieves all priorities from the database and returns them as an ObservableList.
     * This is a convenience method for JavaFX UI components.
     *
     * @return An ObservableList of priority names
     * @throws SQLException If a database error occurs
     */
    public static ObservableList<String> getAllPrioritiesObservable() throws SQLException {
        List<String> priorities = getAllPriorities();
        return FXCollections.observableArrayList(priorities);
    }
}
