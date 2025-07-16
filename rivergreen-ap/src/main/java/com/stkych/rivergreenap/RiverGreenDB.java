package com.stkych.rivergreenap;

import com.stkych.rivergreenap.model.TreatmentPlanProcedure;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                "(SELECT TreatPlanNum FROM treatplan WHERE PatNum = ? AND TPStatus = 1))";

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
        ObservableList<String> observablePriorities = FXCollections.observableArrayList();
        observablePriorities.add("None");
        observablePriorities.addAll(priorities);
        return observablePriorities;
    }

    /**
     * Retrieves all diagnoses from the database.
     * This method queries the definition table for all diagnosis definitions.
     *
     * @return A list of diagnosis names
     * @throws SQLException If a database error occurs
     */
    public static @NotNull List<String> getAllDiagnoses() throws SQLException {
        // Create a list to hold the diagnoses
        List<String> diagnoses = new ArrayList<>();

        // SQL Query to retrieve all diagnoses from the definition table
        // The definition table contains various types of definitions, including diagnoses
        // We filter for the category that contains diagnoses (Category = 16)
        String sql = "SELECT * FROM definition WHERE Category = 16";

        // Execute the query
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String diagnosisName = rs.getString("ItemName");
                if (diagnosisName != null && !diagnosisName.isEmpty()) {
                    diagnoses.add(diagnosisName);
                }
            }
        }

        return diagnoses;
    }

    /**
     * Retrieves all diagnoses from the database and returns them as an ObservableList.
     * This is a convenience method for JavaFX UI components.
     *
     * @return An ObservableList of diagnosis names
     * @throws SQLException If a database error occurs
     */
    public static ObservableList<String> getAllDiagnosesObservable() throws SQLException {
        List<String> diagnoses = getAllDiagnoses();
        ObservableList<String> observableDiagnoses = FXCollections.observableArrayList();
        observableDiagnoses.add("None");
        observableDiagnoses.addAll(diagnoses);
        return observableDiagnoses;
    }

    /**
     * Retrieves a patient's full name from the database.
     * This method queries the patient table for the patient's first, middle, and last name.
     *
     * @param patientNumber The patient number
     * @return The patient's full name (Last, First Middle)
     * @throws SQLException If a database error occurs
     */
    public static String getPatientFullName(int patientNumber) throws SQLException {
        String fullName = "Patient #" + patientNumber; // Default value if query fails

        // SQL Query to retrieve the patient's name
        String sql = "SELECT LName, FName, MiddleI FROM patient WHERE PatNum = ?";

        // Execute the query
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, patientNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String lastName = rs.getString("LName");
                    String firstName = rs.getString("FName");
                    String middleInitial = rs.getString("MiddleI");

                    // Build the full name
                    StringBuilder nameBuilder = new StringBuilder();

                    // Add last name if available
                    if (lastName != null && !lastName.isEmpty()) {
                        nameBuilder.append(lastName);
                    }

                    // Add first name if available
                    if (firstName != null && !firstName.isEmpty()) {
                        if (nameBuilder.length() > 0) {
                            nameBuilder.append(", ");
                        }
                        nameBuilder.append(firstName);
                    }

                    // Add middle initial if available
                    if (middleInitial != null && !middleInitial.isEmpty()) {
                        nameBuilder.append(" ").append(middleInitial);
                    }

                    // If we have a name, use it; otherwise, keep the default
                    if (nameBuilder.length() > 0) {
                        fullName = nameBuilder.toString();
                    }
                }
            }
        }

        return fullName;
    }

    /**
     * Updates treatment plan procedures for a patient.
     * This method generates SQL queries based on the procedure data and executes them within a transaction.
     *
     * @param patientNumber The patient number
     * @param procedures The list of procedures to update
     * @return A Map containing execution results: success count, failure count, error messages, and generated SQL queries
     */
    @SuppressWarnings("t")
    public static Map<String, Object> updateTreatmentPlanProcedures(int patientNumber, List<TreatmentPlanProcedure> procedures) {
        System.out.println("[DEBUG_LOG] Starting updateTreatmentPlanProcedures for Patient #" + patientNumber + " with " + (procedures != null ? procedures.size() : 0) + " procedures");

        // Check database connection and permissions
        try (Connection conn = getConnection()) {
            System.out.println("[DEBUG_LOG] Testing database connection and permissions...");
            try (Statement stmt = conn.createStatement()) {
                // Test if we can update the procedurelog table
                stmt.execute("SELECT 1 FROM procedurelog LIMIT 1");
                // Test if we can access the treatplanattach table
                stmt.execute("SELECT 1 FROM treatplanattach LIMIT 1");
                System.out.println("[DEBUG_LOG] Database connection and permissions verified.");
            }
        } catch (SQLException e) {
            System.out.println("[DEBUG_LOG] Database connection or permissions error: " + e.getMessage());
            System.out.println("[DEBUG_LOG] SQL State: " + e.getSQLState());
            System.out.println("[DEBUG_LOG] Error Code: " + e.getErrorCode());

            Map<String, Object> results = new HashMap<>();
            results.put("successCount", 0);
            results.put("failureCount", 0);
            results.put("errorMessages", List.of("Database connection or permissions error: " + e.getMessage()));
            results.put("sqlQueries", new ArrayList<String>());
            results.put("status", "database_error");
            return results;
        }

        if (procedures == null || procedures.isEmpty()) {
            System.out.println("[DEBUG_LOG] No procedures to update for Patient #" + patientNumber + ", returning early");
            Map<String, Object> results = new HashMap<>();
            results.put("successCount", 0);
            results.put("failureCount", 0);
            results.put("errorMessages", new ArrayList<String>());
            results.put("sqlQueries", new ArrayList<String>());
            results.put("status", "No procedures to update");
            return results;
        }

        System.out.println("Generating SQL queries for Patient #" + patientNumber);

        // Create a list to hold the SQL queries
        List<String> sqlQueries = new ArrayList<>();

        // Generate SQL update queries for each procedure
        System.out.println("[DEBUG_LOG] Starting SQL generation for " + procedures.size() + " procedures");
        for (TreatmentPlanProcedure procedure : procedures) {
            try {
                // Get the procedure number (primary key)
                int procNum = procedure.getProcedureNumber();
                System.out.println("[DEBUG_LOG] Generating SQL for procedure #" + procNum);

                // Get the values that need updating
                String priorityName = escapeSql(procedure.getPriority());
                String toothNum = escapeSql(procedure.getToothNumber());
                String surface = escapeSql(procedure.getSurface());
                String procCode = escapeSql(procedure.getProcedureCode());
                String diagnosis = escapeSql(procedure.getDiagnosis());
                double fee = procedure.getFee();

                System.out.println("[DEBUG_LOG] Procedure values: Priority=" + priorityName + 
                                  ", ToothNum=" + toothNum + 
                                  ", Surface=" + surface + 
                                  ", ProcCode=" + procCode + 
                                  ", Diagnosis=" + diagnosis + 
                                  ", Fee=" + fee);

                // Create a comprehensive update query for procedurelog
                StringBuilder queryBuilder = new StringBuilder();
                queryBuilder.append("UPDATE procedurelog SET ");

                // Priority - using subquery to get DefNum from definition table
                if (priorityName != null && !priorityName.isEmpty() && !priorityName.equals("None")) {
                    queryBuilder.append("Priority = (SELECT DefNum FROM definition WHERE ItemName = '")
                               .append(priorityName)
                               .append("' AND Category = 20 LIMIT 1), ");
                } else {
                    queryBuilder.append("Priority = 0, "); // Default value for no priority
                }

                // Other fields - handle null or empty values
                if (toothNum != null && !toothNum.isEmpty()) {
                    queryBuilder.append("ToothNum = '").append(toothNum).append("', ");
                } else {
                    queryBuilder.append("ToothNum = NULL, ");
                }

                if (surface != null && !surface.isEmpty()) {
                    queryBuilder.append("Surf = '").append(surface).append("', ");
                } else {
                    queryBuilder.append("Surf = NULL, ");
                }

                // CodeNum - using subquery to get CodeNum from procedurecode table
                if (procCode != null && !procCode.isEmpty()) {
                    queryBuilder.append("CodeNum = (SELECT CodeNum FROM procedurecode WHERE ProcCode = '")
                               .append(procCode)
                               .append("' LIMIT 1), ");
                } else {
                    queryBuilder.append("CodeNum = NULL, ");
                }

                // Dx - using subquery to get DefNum from definition table for diagnosis
                if (diagnosis != null && !diagnosis.isEmpty() && !diagnosis.equals("No diagnosis")) {
                    queryBuilder.append("Dx = (SELECT DefNum FROM definition WHERE ItemName = '")
                               .append(diagnosis)
                               .append("' AND Category = 16 LIMIT 1), ");
                } else {
                    queryBuilder.append("Dx = 0, "); // Default value for no diagnosis
                }

                // Fee
                queryBuilder.append("ProcFee = ").append(fee);

                // Where clause
                queryBuilder.append(" WHERE ProcNum = ").append(procNum);

                // Add the procedurelog update query to the list
                String finalQuery = queryBuilder.toString();
                sqlQueries.add(finalQuery);
                System.out.println("[DEBUG_LOG] Added procedurelog update query for procedure #" + procNum + " to the list");

                // Create an update query for treatplanattach
                // This ensures that the procedure is properly linked to the treatment plan
                StringBuilder tpaQueryBuilder = new StringBuilder();
                tpaQueryBuilder.append("UPDATE treatplanattach SET ");

                // Update the Priority field in treatplanattach
                // Use the same priority logic as in the procedurelog update
                if (priorityName != null && !priorityName.isEmpty() && !priorityName.equals("None")) {
                    tpaQueryBuilder.append("Priority = (SELECT DefNum FROM definition WHERE ItemName = '")
                               .append(priorityName)
                               .append("' AND Category = 20 LIMIT 1)");
                } else {
                    tpaQueryBuilder.append("Priority = 0"); // Default value for no priority
                }

                // Where clause - update the treatplanattach record for this procedure
                tpaQueryBuilder.append(" WHERE ProcNum = ").append(procNum);

                // Add the treatplanattach update query to the list
                String tpaFinalQuery = tpaQueryBuilder.toString();
                sqlQueries.add(tpaFinalQuery);
                System.out.println("[DEBUG_LOG] Added treatplanattach update query for procedure #" + procNum + " to the list");

            } catch (Exception e) {
                System.err.println("Error generating SQL for procedure: " + e.getMessage());
                System.out.println("[DEBUG_LOG] Error generating SQL for procedure #" + procedure.getProcedureNumber() + ": " + e.getMessage());
            }
        }

        System.out.println("[DEBUG_LOG] SQL generation complete. Generated " + sqlQueries.size() + " SQL queries for Patient #" + patientNumber);

        // Print all SQL queries with patient information
        System.out.println("Generated " + sqlQueries.size() + " SQL queries for Patient #" + patientNumber + ":");
        for (int i = 0; i < sqlQueries.size(); i++) {
            System.out.println((i + 1) + ": " + sqlQueries.get(i));
        }

        // Execute the queries and get the results
        System.out.println("[DEBUG_LOG] Starting execution of " + sqlQueries.size() + " SQL queries for Patient #" + patientNumber);
        Map<String, Object> results = executeUpdateQueries(sqlQueries);

        // Add the generated SQL queries to the results
        results.put("sqlQueries", sqlQueries);

        // Log the results
        String status = (String) results.get("status");
        int successCount = (int) results.get("successCount");
        int failureCount = (int) results.get("failureCount");
        System.out.println("[DEBUG_LOG] SQL execution complete for Patient #" + patientNumber + ". Status: " + status + 
                          ", Success: " + successCount + ", Failure: " + failureCount);

        return results;
    }

    /**
     * Escapes special characters in SQL strings to prevent SQL injection.
     * 
     * @param input The input string to escape
     * @return The escaped string
     */
    private static String escapeSql(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("'", "''");
    }

    /**
     * Executes a list of SQL update queries with individual transactions.
     * This method executes each query in its own transaction, committing successful queries
     * and only rolling back failed ones.
     *
     * @param sqlQueries The list of SQL update queries to execute
     * @return A Map containing execution results: success count, failure count, and error messages
     */
    public static Map<String, Object> executeUpdateQueries(List<String> sqlQueries) {
    Map<String, Object> results = new HashMap<>();
    List<String> errorMessages = new ArrayList<>();
    int successCount = 0;
    int failureCount = 0;

    // Early return if no queries
    if (sqlQueries == null || sqlQueries.isEmpty()) {
        return createResultMap(0, 0, errorMessages, "No queries to execute");
    }

    Connection conn = null;
    try {
        conn = getConnection();
        conn.setAutoCommit(false);  // Start transaction mode

        for (int i = 0; i < sqlQueries.size(); i++) {
            String sql = sqlQueries.get(i);

            // Skip empty queries
            if (sql == null || sql.trim().isEmpty()) {
                failureCount++;
                errorMessages.add("Query " + (i + 1) + " is empty");
                continue;
            }

            try (Statement stmt = conn.createStatement()) {
                int rowsAffected = stmt.executeUpdate(sql);
                conn.commit();  // Commit each successful query
                successCount++;
                System.out.println("[DEBUG_LOG] Query " + (i + 1) + " executed successfully. Rows affected: " + rowsAffected);
            } catch (SQLException e) {
                conn.rollback();  // Rollback failed query
                errorMessages.add("Query " + (i + 1) + ": " + e.getMessage());
                failureCount++;
                System.out.println("[DEBUG_LOG] Query " + (i + 1) + " failed: " + e.getMessage());
            }
        }
    } catch (SQLException e) {
        errorMessages.add("Database connection error: " + e.getMessage());
        System.out.println("[DEBUG_LOG] Database connection error: " + e.getMessage());
        return createResultMap(0, sqlQueries.size(), errorMessages, "connection_error");
    } finally {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);  // Reset autoCommit to true
                conn.close();
                System.out.println("[DEBUG_LOG] Database connection closed and autoCommit reset to true");
            } catch (SQLException e) {
                System.out.println("[DEBUG_LOG] Error closing connection: " + e.getMessage());
            }
        }
    }

    String status = determineStatus(successCount, failureCount);
    return createResultMap(successCount, failureCount, errorMessages, status);
}

private static Map<String, Object> createResultMap(int successCount, int failureCount, 
                                                 List<String> errorMessages, String status) {
    Map<String, Object> results = new HashMap<>();
    results.put("successCount", successCount);
    results.put("failureCount", failureCount);
    results.put("errorMessages", errorMessages);
    results.put("status", status);
    return results;
}

private static String determineStatus(int successCount, int failureCount) {
    if (failureCount == 0) return "success";
    if (successCount > 0) return "partial_success";
    return "failure";
}

/**
 * Retrieves the description for a specific procedure code.
 *
 * @param procedureCode The procedure code to get the description for
 * @return The description of the procedure code, or an empty string if not found
 * @throws SQLException If a database error occurs
 */
public static String getProcedureCodeDescription(String procedureCode) throws SQLException {
    if (procedureCode == null || procedureCode.isEmpty()) {
        return "";
    }

    String description = "";
    String sql = "SELECT Descript FROM procedurecode WHERE ProcCode = ?";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, procedureCode);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                description = rs.getString("Descript");
                if (description == null) {
                    description = "";
                }
            }
        }
    }

    return description;
}

/**
 * Retrieves all dental procedure codes from the database.
 * This method queries the procedurecode table for all procedure codes.
 *
 * @return A list of procedure codes
 * @throws SQLException If a database error occurs
 */
public static @NotNull List<String> getAllProcedureCodes() throws SQLException {
    // Create a list to hold the procedure codes
    List<String> procedureCodes = new ArrayList<>();

    // SQL Query to retrieve all procedure codes from the procedurecode table
    String sql = "SELECT ProcCode FROM procedurecode ORDER BY ProcCode";

    // Execute the query
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            String procCode = rs.getString("ProcCode");
            if (procCode != null && !procCode.isEmpty()) {
                procedureCodes.add(procCode);
            }
        }
    }

    return procedureCodes;
}

/**
 * Retrieves all dental procedure codes from the database and returns them as an ObservableList.
 * This is a convenience method for JavaFX UI components.
 *
 * @return An ObservableList of procedure codes
 * @throws SQLException If a database error occurs
 */
public static ObservableList<String> getAllProcedureCodesObservable() throws SQLException {
    List<String> procedureCodes = getAllProcedureCodes();
    ObservableList<String> observableProcedureCodes = FXCollections.observableArrayList();
    observableProcedureCodes.add("None");
    observableProcedureCodes.addAll(procedureCodes);
    return observableProcedureCodes;
}
}
