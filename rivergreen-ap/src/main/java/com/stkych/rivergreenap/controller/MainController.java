package com.stkych.rivergreenap.controller;

import com.stkych.rivergreenap.RiverGreenDB;
// import com.stkych.rivergreenap.RiverGreenDBOld; // Commented out as we're using RiverGreenDB instead
import com.stkych.rivergreenap.SceneSwitcher;
import com.stkych.rivergreenap.model.TreatmentPlanProcedure;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Controller for the main scene of the application.
 * Handles user interactions and navigation from the main screen.
 */
public class MainController extends Controller {

    @FXML
    private Button menuButton;

    @FXML
    private Button configButton;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;

    @FXML
    private TableView<TreatmentPlanProcedure> leftTableView;

    @FXML
    private TableView<?> rightTableView;

    @FXML
    private TableColumn<TreatmentPlanProcedure, String> priorityColumn;

    @FXML
    private TableColumn<TreatmentPlanProcedure, String> toothColumn;

    @FXML
    private TableColumn<TreatmentPlanProcedure, String> surfaceColumn;

    @FXML
    private TableColumn<TreatmentPlanProcedure, String> codeColumn;

    @FXML
    private TableColumn<TreatmentPlanProcedure, String> diagnosisColumn;

    @FXML
    private TableColumn<TreatmentPlanProcedure, String> descriptionColumn;

    @FXML
    private TableColumn<TreatmentPlanProcedure, Double> feeColumn;

    /**
     * Initializes the controller.
     * This method is called automatically after the FXML file has been loaded.
     * 
     * @param location The location used to resolve relative paths for the root object
     * @param resources The resources used to localize the root object
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up the table columns
        setupTableColumns();

        try {
            // Get the patient number from the data cache
            Integer patientNumber = (Integer) SceneSwitcher.getData("patientNumber");

            if (patientNumber != null) {
                System.out.println("Loading procedures for patient " + patientNumber);
                // Load procedures for the patient using the new RiverGreenDB class
                loadProceduresForPatient(patientNumber);
            } else {
                System.out.println("No patient number found, adding dummy data for testing");
                // Add dummy data if no patient number is found
                addDummyDataForTesting();
            }

            // If no procedures were loaded, add some dummy data for testing
            if (leftTableView.getItems() == null || leftTableView.getItems().isEmpty()) {
                System.out.println("No procedures loaded, adding dummy data for testing");
                addDummyDataForTesting();
            }
        } catch (SQLException e) {
            handleError(e);
        }
    }

    /**
     * Adds dummy data to the table view for testing purposes.
     * This helps verify if the issue is with data retrieval or UI display.
     */
    private void addDummyDataForTesting() {
        ObservableList<TreatmentPlanProcedure> dummyProcedures = FXCollections.observableArrayList();

        // Add a few dummy procedures
        dummyProcedures.add(new TreatmentPlanProcedure("High", "1", "MOD", "D2150", "K02.9", "Amalgam - Two Surfaces", 150.0));
        dummyProcedures.add(new TreatmentPlanProcedure("Medium", "2", "O", "D2140", "K02.9", "Amalgam - One Surface", 100.0));
        dummyProcedures.add(new TreatmentPlanProcedure("Low", "30", "B", "D2330", "K02.9", "Resin - One Surface, Anterior", 120.0));

        System.out.println("Added " + dummyProcedures.size() + " dummy procedures for testing");

        // Set the dummy data in the table view
        leftTableView.setItems(dummyProcedures);
        System.out.println("Set dummy items in the table view");

        // Force the table to refresh
        leftTableView.refresh();
        System.out.println("Refreshed table view with dummy data");
    }

    /**
     * Loads procedures for a patient using the RiverGreenDB class.
     * This method retrieves all procedures from the patient's treatment plans
     * using the SQL query specified in the requirements.
     *
     * @param patientNumber The patient number
     * @throws SQLException If a database error occurs
     */
    private void loadProceduresForPatient(int patientNumber) throws SQLException {
        System.out.println("Loading procedures for patient " + patientNumber + " using RiverGreenDB");

        // Get procedures for the patient using the new RiverGreenDB class
        ObservableList<TreatmentPlanProcedure> procedures = RiverGreenDB.getProceduresForPatientObservable(patientNumber);
        System.out.println("Retrieved " + procedures.size() + " procedures for patient " + patientNumber);

        // Print the first few procedures for debugging
        for (int i = 0; i < Math.min(3, procedures.size()); i++) {
            TreatmentPlanProcedure procedure = procedures.get(i);
            System.out.println("Added procedure to UI: Tooth=" + procedure.getToothNumber() + 
                              ", Code=" + procedure.getProcedureCode() + 
                              ", Desc=" + procedure.getDescription());
        }

        // Set the items in the table view
        leftTableView.setItems(procedures);
        System.out.println("Set " + procedures.size() + " items in the table view");

        // Check if the table view is visible and has the correct column count
        System.out.println("TableView visible: " + leftTableView.isVisible());
        System.out.println("TableView column count: " + leftTableView.getColumns().size());

        // Force the table to refresh
        leftTableView.refresh();
    }

    /**
     * Shows treatment plans for the current patient in the table view.
     * This is useful for viewing all plans for the current patient.
     * 
     * Note: This method is currently disabled as it depends on RiverGreenDBOld.
     * Use loadProceduresForPatient instead.
     */
    public void showPatientTreatmentPlans() {
        System.out.println("showPatientTreatmentPlans is disabled. Use loadProceduresForPatient instead.");

        // Get the patient number from the data cache
        Integer patientNumber = (Integer) SceneSwitcher.getData("patientNumber");

        if (patientNumber != null) {
            try {
                // Load procedures for the patient using the new RiverGreenDB class
                loadProceduresForPatient(patientNumber);
            } catch (SQLException e) {
                System.err.println("Error loading procedures for patient: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("No patient number found in data cache");
        }
    }

    /**
     * Converts a TPStatus code to a human-readable status text.
     * 
     * @param status The TPStatus code
     * @return A human-readable status text
     */
    private String getStatusText(int status) {
        switch (status) {
            case 0: return "Inactive";
            case 1: return "Active";
            case 2: return "Completed";
            case 3: return "Archived";
            default: return "Unknown (" + status + ")";
        }
    }

    /**
     * Sets up the table columns for the leftTableView.
     */
    private void setupTableColumns() {
        // Set up the cell value factories for each column
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        toothColumn.setCellValueFactory(new PropertyValueFactory<>("toothNumber"));
        surfaceColumn.setCellValueFactory(new PropertyValueFactory<>("surface"));
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("procedureCode"));
        diagnosisColumn.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        feeColumn.setCellValueFactory(new PropertyValueFactory<>("fee"));

        // Log the column setup for debugging
        System.out.println("Table columns setup complete");
        System.out.println("Priority column: " + (priorityColumn != null ? "OK" : "NULL"));
        System.out.println("Tooth column: " + (toothColumn != null ? "OK" : "NULL"));
        System.out.println("Surface column: " + (surfaceColumn != null ? "OK" : "NULL"));
        System.out.println("Code column: " + (codeColumn != null ? "OK" : "NULL"));
        System.out.println("Diagnosis column: " + (diagnosisColumn != null ? "OK" : "NULL"));
        System.out.println("Description column: " + (descriptionColumn != null ? "OK" : "NULL"));
        System.out.println("Fee column: " + (feeColumn != null ? "OK" : "NULL"));

        // Make sure columns are visible and have appropriate widths
        priorityColumn.setVisible(true);
        toothColumn.setVisible(true);
        surfaceColumn.setVisible(true);
        codeColumn.setVisible(true);
        diagnosisColumn.setVisible(true);
        descriptionColumn.setVisible(true);
        feeColumn.setVisible(true);
    }

    /**
     * Loads the treatment plan for the specified patient.
     * 
     * Note: This method is currently disabled as it depends on RiverGreenDBOld.
     * Use loadProceduresForPatient instead.
     * 
     * @param patientNumber The patient number
     * @throws SQLException If a database error occurs
     */
    private void loadTreatmentPlan(int patientNumber) throws SQLException {
        System.out.println("loadTreatmentPlan is disabled. Use loadProceduresForPatient instead.");
        loadProceduresForPatient(patientNumber);
    }

    /**
     * Loads a treatment plan directly using its treatment plan number.
     * This method is used to load a specific treatment plan regardless of the patient.
     * 
     * Note: This method is currently disabled as it depends on RiverGreenDBOld.
     * Use loadProceduresForPatient instead.
     * 
     * @param treatPlanNum The treatment plan number
     * @throws SQLException If a database error occurs
     */
    private void loadTreatmentPlanDirectly(int treatPlanNum) throws SQLException {
        System.out.println("loadTreatmentPlanDirectly is disabled. Use loadProceduresForPatient instead.");

        // Since we can't load by treatment plan number directly with the new implementation,
        // we'll just add some dummy data for testing
        addDummyDataForTesting();
    }

    /**
     * Loads all procedures from treatment plan 22147.
     * This method is specifically designed to retrieve all 18 procedures from treatment plan 22147,
     * regardless of whether they are saved or not.
     * 
     * Note: This method is currently disabled as it depends on RiverGreenDBOld.
     * Use loadProceduresForPatient instead.
     * 
     * @throws SQLException If a database error occurs
     */
    private void loadAllProceduresFromTreatmentPlan22147() throws SQLException {
        System.out.println("loadAllProceduresFromTreatmentPlan22147 is disabled. Use loadProceduresForPatient instead.");

        // Since we can't load from treatment plan 22147 directly with the new implementation,
        // we'll just add some dummy data for testing
        addDummyDataForTesting();
    }

    /**
     * Handles the menu button click event.
     * This would typically open a menu or navigate to a menu screen.
     */
    @FXML
    private void handleMenuButtonAction() {
        // Handle menu button click
        System.out.println("Menu button clicked");
        // In a real application, this might open a dropdown menu or navigate to a menu screen
    }

    /**
     * Handles the config button click event.
     * Navigates to the configuration screen.
     */
    @FXML
    private void handleConfigButtonAction() {
        try {
            navigateToConfig();
        } catch (IOException e) {
            handleError(e);
        }
    }

    /**
     * Handles the show plans button click event.
     * Shows all treatment plans for the current patient in the table view.
     */
    @FXML
    private void handleShowPlansButtonAction() {
        System.out.println("Show Patient Plans button clicked");
        showPatientTreatmentPlans();
    }

    /**
     * Handles the OK button click event.
     * This would typically save changes and close the screen or navigate to another screen.
     */
    @FXML
    private void handleOkButtonAction() {
        // Handle OK button click
        System.out.println("OK button clicked");
        // In a real application, this might save changes and close the screen
    }

    /**
     * Handles the Cancel button click event.
     * This would typically discard changes and close the screen or navigate to another screen.
     */
    @FXML
    private void handleCancelButtonAction() {
        // Handle Cancel button click
        System.out.println("Cancel button clicked");
        // In a real application, this might discard changes and close the screen
    }
}
