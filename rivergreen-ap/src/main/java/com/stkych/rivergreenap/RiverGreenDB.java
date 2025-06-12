package com.stkych.rivergreenap;

import com.stkych.rivergreenap.model.TreatmentPlanProcedure;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Database utility class for the RiverGreen Dental Application.
 * Provides methods to connect to the MySQL database and retrieve treatment plan procedures.
 */
public class RiverGreenDB {
    // Database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/opendental";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "test";

    /**
     * Gets a connection to the MySQL database.
     *
     * @return A Connection object
     * @throws SQLException If a database error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Retrieves procedures for a patient's treatment plans.
     * Uses the SQL query:
     * SELECT pl.*, pc.ProcCode, pc.Descript, d.ItemName AS PriorityName FROM procedurelog pl 
     * LEFT JOIN procedurecode pc ON pl.CodeNum = pc.CodeNum 
     * LEFT JOIN definition d ON pl.Priority = d.DefNum
     * WHERE pl.ProcNum IN 
     * (SELECT ProcNum FROM treatplanattach WHERE TreatPlanNum IN 
     * (SELECT TreatPlanNum FROM treatplan WHERE PatNum = ?))
     *
     * @param patientNumber The patient number
     * @return A list of TreatmentPlanProcedure objects
     * @throws SQLException If a database error occurs
     */
    public static List<TreatmentPlanProcedure> getProceduresForPatient(int patientNumber) throws SQLException {
        List<TreatmentPlanProcedure> procedures = new ArrayList<>();

        String sql = "SELECT pl.*, pc.ProcCode, pc.Descript, d.ItemName AS PriorityName FROM procedurelog pl " +
                     "LEFT JOIN procedurecode pc ON pl.CodeNum = pc.CodeNum " +
                     "LEFT JOIN definition d ON pl.Priority = d.DefNum " +
                     "WHERE pl.ProcNum IN " +
                     "(SELECT ProcNum FROM treatplanattach WHERE TreatPlanNum IN " +
                     "(SELECT TreatPlanNum FROM treatplan WHERE PatNum = ?))";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, patientNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Extract data from the result set
                    String toothNum = rs.getString("ToothNum");
                    String surface = rs.getString("Surf");
                    String procCode = rs.getString("ProcCode");
                    String diagnosis = rs.getString("Dx");
                    String description = rs.getString("Descript");
                    double fee = rs.getDouble("ProcFee");

                    // Get the priority from the result set
                    int priorityNum = rs.getInt("Priority");
                    String priority;

                    // Handle the special case where priority is 0
                    if (priorityNum == 0) {
                        priority = "N/A";
                    } else {
                        // Get the priority name from the result set
                        priority = rs.getString("PriorityName");
                        // If priority name is null, use "No priority" as default
                        if (priority == null) {
                            priority = "N/A";
                        }
                    }

                    // Create a new TreatmentPlanProcedure object
                    TreatmentPlanProcedure procedure = new TreatmentPlanProcedure(
                            priority, toothNum, surface, procCode, diagnosis, description, fee);

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
}
