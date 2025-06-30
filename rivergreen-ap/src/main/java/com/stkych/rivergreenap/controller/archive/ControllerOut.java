package com.stkych.rivergreenap.controller.archive;

import com.stkych.rivergreenap.RiverGreenDB;
// import com.stkych.rivergreenap.RiverGreenDBOld; // Commented out as we're using RiverGreenDB instead
import com.stkych.rivergreenap.SceneSwitcher;
import com.stkych.rivergreenap.model.TreatmentPlanProcedure;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the main scene of the application.
 * Handles user interactions and navigation from the main screen.
 */
public class ControllerOut extends ControllerOld {

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
    private TableView<TreatmentPlanProcedure> rightTableView;

    // List to track the order of procedures in the right table view
    private ObservableList<TreatmentPlanProcedure> selectedProcedures = FXCollections.observableArrayList();

    // Custom DataFormat for drag and drop operations
    private static final DataFormat PROCEDURE_FORMAT = new DataFormat("application/x-treatmentplanprocedure");

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

    // Right table view columns
    @FXML
    private TableColumn<TreatmentPlanProcedure, String> rightPriorityColumn;

    @FXML
    private TableColumn<TreatmentPlanProcedure, String> rightToothColumn;

    @FXML
    private TableColumn<TreatmentPlanProcedure, String> rightSurfaceColumn;

    @FXML
    private TableColumn<TreatmentPlanProcedure, String> rightCodeColumn;

    @FXML
    private TableColumn<TreatmentPlanProcedure, String> rightDiagnosisColumn;

    @FXML
    private TableColumn<TreatmentPlanProcedure, String> rightDescriptionColumn;

    @FXML
    private TableColumn<TreatmentPlanProcedure, Double> rightFeeColumn;

    @FXML
    private ListView<String> priorityListView;

    private static final String DROP_TARGET_STYLE = "-fx-border-color: #007ad7; -fx-border-width: 0 0 2 0; -fx-padding: 0;";

    /**
     * Initializes the controller after the root element has been completely processed.
     * Sets up the table columns, retrieves patient data, loads procedures if available,
     * and adds testing data if necessary.
     *
     * @param location The URL location used to resolve relative paths for the root object, or null if not known.
     * @param resources The ResourceBundle used to localize the root object, or null if not applicable.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up the table columns
        setupTableColumns();

        // Set the TableView selection mode to allow multiple selection
        leftTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        rightTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Initialize the right table view with the selected procedures list
        rightTableView.setItems(selectedProcedures);

        // Set up drag-and-drop for the right table view (for reordering only)
        setupDragAndDrop();

        setupPriorityListView();

        try {
            // Get the patient number from the data cache
            Integer patientNumber = (Integer) SceneSwitcher.getData("patientNumber");

            if (patientNumber != null) {
                // Load procedures for the patient using the RiverGreenDB class
                loadProceduresForPatient(patientNumber);
            } else {
                System.out.println("No patient number found");
                // Create an empty observable list and add dummy procedures
                ObservableList<TreatmentPlanProcedure> procedures = FXCollections.observableArrayList();
                // Set the items in the left table view
                leftTableView.setItems(procedures);
                // Force the table to refresh
                leftTableView.refresh();

                // Copy all items from left table to right table
                copyAllItemsToRightTable();
            }
        } catch (SQLException e) {
            handleError(e);
            // If there's an error, still add dummy data so the UI is usable
            System.out.println("Error loading procedures, adding dummy data for testing");
            ObservableList<TreatmentPlanProcedure> procedures = FXCollections.observableArrayList();
            leftTableView.setItems(procedures);
            leftTableView.refresh();

            // Copy all items from left table to right table
            copyAllItemsToRightTable();
        }
    }

    /**
     * Sets up drag-and-drop functionality for the right table view.
     * This allows users to reorder procedures in the right table view by dragging and dropping rows.
     * Also highlights where the item will be placed.
     * <p>
     * Note: yeah... lets try not to touch this. that's a lot of functions.
     */
    private void setupDragAndDrop() {
        rightTableView.setRowFactory(tv -> {
            TableRow<TreatmentPlanProcedure> row = new TableRow<>();
            setupDragDetection(row);
            setupDragOver(row);
            setupDragExit(row);
            setupDrop(row);
            setupDragDone(row);
            return row;
        });
    }

    /**
     * Configures drag detection for the specified table row.
     * When a drag is detected on a non-empty row, initiates a drag-and-drop operation.
     *
     * @param row The TableRow of type TreatmentPlanProcedure for which drag detection is to be set up
     */
    private void setupDragDetection(TableRow<TreatmentPlanProcedure> row) {
        row.setOnDragDetected(event -> {
            if (!row.isEmpty()) {
                Integer index = row.getIndex();
                Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                db.setDragView(row.snapshot(null, null));
                ClipboardContent cc = new ClipboardContent();
                cc.put(PROCEDURE_FORMAT, index);
                db.setContent(cc);
                event.consume();
            }
        });
    }

    /**
     * Configures drag-over behavior for a given table row to highlight it as a potential drop target.
     * This enables the user to see where dragged items can be dropped during a drag-and-drop operation.
     *
     * @param row The TableRow of type TreatmentPlanProcedure for which drag-over behavior is to be set up
     */
    private void setupDragOver(TableRow<TreatmentPlanProcedure> row) {
        row.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasContent(PROCEDURE_FORMAT)) {
                if (row.getIndex() != ((Integer) db.getContent(PROCEDURE_FORMAT)).intValue()) {
                    clearRowStyles();
                    row.setStyle(DROP_TARGET_STYLE);
                    event.acceptTransferModes(TransferMode.MOVE);
                    event.consume();
                }
            }
        });
    }

    private void setupDragExit(TableRow<TreatmentPlanProcedure> row) {
        row.setOnDragExited(event -> row.setStyle(null));
    }

    private void setupDrop(TableRow<TreatmentPlanProcedure> row) {
        row.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasContent(PROCEDURE_FORMAT)) {
                int draggedIndex = (Integer) db.getContent(PROCEDURE_FORMAT);
                TreatmentPlanProcedure draggedProcedure = selectedProcedures.remove(draggedIndex);

                int dropIndex = row.isEmpty() ? selectedProcedures.size() : row.getIndex();
                selectedProcedures.add(dropIndex, draggedProcedure);

                row.setStyle(null);
                event.setDropCompleted(true);
                rightTableView.getSelectionModel().select(dropIndex);
                event.consume();
            }
        });
    }

    private void setupDragDone(TableRow<TreatmentPlanProcedure> row) {
        row.setOnDragDone(event -> clearRowStyles());
    }

    private void clearRowStyles() {
        rightTableView.lookupAll(".table-row-cell").forEach(node -> node.setStyle(null));
    }

    private void sortPriorities(ObservableList<String> priorities) {
        // Create a new list to append to
        ObservableList<String> sortedPriorities = FXCollections.observableArrayList();
        // Start by appending "Next"
        sortedPriorities.add("Next");
        // Add all numbered priorities
        for (int i = 1; i <= 10; i++) {
            sortedPriorities.add(String.valueOf(i));
            sortedPriorities.add(i + "A");
            sortedPriorities.add(i + "B");
            sortedPriorities.add(i + "C");
            sortedPriorities.add(i + " Wait");
            sortedPriorities.add(i + " Decline");
        }
        // Put all other priorities in
        for (String priority : priorities) {
            if (!sortedPriorities.contains(priority)) {
                sortedPriorities.add(priority);
            }
        }

        // Update the original list with sorted priorities
        priorities.setAll(sortedPriorities);
    }

    /**
     * Loads and displays procedures for a specified patient in the table view.
     * This method retrieves the procedures from the database,
     * populates leftTableView, and refreshes the TableView.
     * It also copies all procedures to the right table view.
     *
     * @param patientNumber The unique identifier of the patient whose procedures are to be loaded
     * @throws SQLException If a database error occurs during the data retrieval process
     */
    private void loadProceduresForPatient(int patientNumber) throws SQLException {
        System.out.println("Loading procedures for patient " + patientNumber + " using RiverGreenDB");

        // Get procedures for the patient using the new RiverGreenDB class
        ObservableList<TreatmentPlanProcedure> procedures = RiverGreenDB.getProceduresForPatientObservable(patientNumber);
        System.out.println("Retrieved " + procedures.size() + " procedures for patient " + patientNumber);

        // Set the items in the table view
        leftTableView.setItems(procedures);

        // Force the table to refresh
        leftTableView.refresh();

        // Copy all items from left table to right table
        copyAllItemsToRightTable();
    }

    /**
     * Copies all items from the left table view to the right table view.
     * This is called during initialization to ensure all procedures are available in the right table.
     */
    private void copyAllItemsToRightTable() {
        // Clear the right table view first
        selectedProcedures.clear();

        // Get all items from the left table view
        ObservableList<TreatmentPlanProcedure> leftItems = leftTableView.getItems();

        // Copy each item to the right table view
        for (TreatmentPlanProcedure procedure : leftItems) {
            // Create a copy of the procedure
            TreatmentPlanProcedure copy = new TreatmentPlanProcedure(
                    procedure.getPriority(),
                    procedure.getToothNumber(),
                    procedure.getSurface(),
                    procedure.getProcedureCode(),
                    procedure.getDiagnosis(),
                    procedure.getDescription(),
                    procedure.getFee(),
                    procedure.getProcedureNumber()
            );

            // Add to the right table
            selectedProcedures.add(copy);
        }

        System.out.println("Copied " + selectedProcedures.size() + " procedures from left to right table");
    }


    /**
     * Configures the columns for the table view by setting up cell value factories
     * and ensuring their visibility.
     * <p>
     * This method initializes the value factories for each column in the table, which
     * maps property names to their corresponding columns.
     * It also logs the setup status of each column to assist in debugging and ensures
     * that all columns are visible.
     * <p></p>
     * These columns are configured:<p>
     * - priorityColumn: Priority level of the entry.<p>
     * - toothColumn: Tooth number.<p>
     * - surfaceColumn: Surface involved.<p>
     * - codeColumn: Procedure code.<p>
     * - diagnosisColumn: Diagnosis information.<p>
     * - descriptionColumn: Description of the procedure.<p>
     * - feeColumn: Fee associated with the procedure.
     */
    private void setupTableColumns() {
        // Set up the cell value factories for each column in the left table view
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        toothColumn.setCellValueFactory(new PropertyValueFactory<>("toothNumber"));
        surfaceColumn.setCellValueFactory(new PropertyValueFactory<>("surface"));
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("procedureCode"));
        diagnosisColumn.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        feeColumn.setCellValueFactory(new PropertyValueFactory<>("fee"));

        // Set up the cell value factories for each column in the right table view
        rightPriorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        rightToothColumn.setCellValueFactory(new PropertyValueFactory<>("toothNumber"));
        rightSurfaceColumn.setCellValueFactory(new PropertyValueFactory<>("surface"));
        rightCodeColumn.setCellValueFactory(new PropertyValueFactory<>("procedureCode"));
        rightDiagnosisColumn.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));
        rightDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        rightFeeColumn.setCellValueFactory(new PropertyValueFactory<>("fee"));

//        // Log the column setup for debugging
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

        // Make sure right table columns are visible
        rightPriorityColumn.setVisible(true);
        rightToothColumn.setVisible(true);
        rightSurfaceColumn.setVisible(true);
        rightCodeColumn.setVisible(true);
        rightDiagnosisColumn.setVisible(true);
        rightDescriptionColumn.setVisible(true);
        rightFeeColumn.setVisible(true);
    }


    /**
     * Handles the action event triggered by the "Config" button.
     * <p>
     * This method navigates to the configuration scene by invoking
     * {@code navigateToConfig()} and handles any {@link IOException} that might occur.
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
     * Handles the action event triggered by the "Ok" button.
     * <p>
     * This method prints the final order of procedures and could be extended
     * to save the order to a database or perform other actions.
     */
    @FXML
    private void handleOkButtonAction() {
        System.out.println("OK button clicked");

        // Print the final order of procedures
        System.out.println("Final order of procedures:");
        List<Integer> procedureNumbers = new ArrayList<>();
        for (int i = 0; i < selectedProcedures.size(); i++) {
            TreatmentPlanProcedure procedure = selectedProcedures.get(i);
            System.out.println(i + ": " + procedure.getProcedureCode() + " - " + procedure.getDescription());
            procedureNumbers.add(procedure.getProcedureNumber());
        }
        System.out.println("Final procedure numbers in order: " + procedureNumbers);

        // Here you would typically save the order to a database or perform other actions
    }

    /**
     * Handles the action event triggered by the "Cancel" button.
     * This method hasn't been implemented yet.
     */
    @FXML
    private void handleCancelButtonAction() {
        System.out.println("Cancel button clicked");
    }

    /**
     * Sets up the priority list view with priorities from the database and configures its click handler.
     * Initializes the priority list view using SQL query: SELECT * FROM definition WHERE Category = 20
     */
    private void setupPriorityListView() {
        try {
            ObservableList<String> priorities = RiverGreenDB.getAllPrioritiesObservable();
            sortPriorities(priorities);
            priorityListView.setItems(priorities);
        } catch (SQLException e) {
            handleError(e);
        }

        // Add event handler to update priority when a priority is clicked in the list view
        priorityListView.setOnMouseClicked(event -> {
            String selectedPriority = priorityListView.getSelectionModel().getSelectedItem();
            TreatmentPlanProcedure selectedProcedure = rightTableView.getSelectionModel().getSelectedItem();

            if (selectedPriority != null && selectedProcedure != null) {
                selectedProcedure.setPriority(selectedPriority);
                rightTableView.refresh();
            }
        });
    }


}
