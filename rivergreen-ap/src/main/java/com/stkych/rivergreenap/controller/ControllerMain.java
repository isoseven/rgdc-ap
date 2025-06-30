package com.stkych.rivergreenap.controller;

import com.stkych.rivergreenap.RiverGreenDB;
import com.stkych.rivergreenap.SceneSwitcher;
import com.stkych.rivergreenap.controller.archive.ControllerOld;
import com.stkych.rivergreenap.model.RulesetItem;
import com.stkych.rivergreenap.model.TreatmentPlanProcedure;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for the news.fxml scene of the application.
 * This is an iteration of the MainController with UI changes.
 */
public class ControllerMain extends ControllerOld {

    // Custom DataFormat for drag and drop operations
    private static final DataFormat PROCEDURE_FORMAT = new DataFormat("application/x-treatmentplanprocedure");

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;

    @FXML
    private ListView<TreatmentPlanProcedure> listView;

    @FXML
    private ListView<String> priorityListView;

    @FXML
    private Label patientNameLabel;

    @FXML
    private MenuButton doctorSelectMenuButton;

    @FXML
    private MenuButton rulesetSelectMenuButton;

    private ObservableList<TreatmentPlanProcedure> procedures = FXCollections.observableArrayList();

    private Map<String, List<RulesetItem>> rulesets = new HashMap<>();
    private String currentRuleset = "";

    /**
     * Initializes the controller after the root element has been completely processed.
     * Sets up the list views, retrieves patient data, and loads procedures if available.
     *
     * @param location The URL location used to resolve relative paths for the root object, or null if not known.
     * @param resources The ResourceBundle used to localize the root object, or null if not applicable.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize the list view with the procedures list
        listView.setItems(procedures);

        // Set the cell factory for the list view to use the custom cell factory
        listView.setCellFactory(new TreatmentPlanProcedureCellFactory());

        // Enable multiple selection
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Disable selection for the header (first item)
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (listView.getSelectionModel().getSelectedIndices().contains(0)) {
                // If the header is selected, clear the selection and reselect all other selected items
                List<Integer> selectedIndices = new ArrayList<>(listView.getSelectionModel().getSelectedIndices());
                selectedIndices.remove(Integer.valueOf(0)); // Remove the header index

                listView.getSelectionModel().clearSelection();

                // Reselect all other items
                for (Integer index : selectedIndices) {
                    listView.getSelectionModel().select(index);
                }
            }
        });

        // Set up drag and drop for the list view
        setupDragAndDrop();

        // Set up multi-select functionality
        setupMultiSelect();

        setupPriorityListView();

        // Load rulesets and set up the ruleset selection menu
        loadRulesets();

        try {
            // Get the patient number from the data cache
            Integer patientNumber = (Integer) SceneSwitcher.getData("patientNumber");

            if (patientNumber != null) {
                // Load procedures for the patient using the RiverGreenDB class
                loadProceduresForPatient(patientNumber);

                // Set patient name (using patient number as placeholder since getPatientName doesn't exist)
                patientNameLabel.setText("Patient #" + patientNumber);
            } else {
                System.out.println("No patient number found");
                // Create an empty observable list with just the header
                procedures = FXCollections.observableArrayList();
                // Add the header item
                TreatmentPlanProcedure headerItem = new TreatmentPlanProcedure(
                    "Priority", "Tth", "Surf", "Code", "Diagnosis", "Description", 0.0, 0);
                procedures.add(headerItem);
                // Set the items in the list view
                listView.setItems(procedures);
            }
        } catch (SQLException e) {
            handleError(e);
            // If there's an error, still add the header so the UI is usable
            System.out.println("Error loading procedures, adding header only");
            procedures = FXCollections.observableArrayList();
            // Add the header item
            TreatmentPlanProcedure headerItem = new TreatmentPlanProcedure(
                "Priority", "Tth", "Surf", "Code", "Diagnosis", "Description", 0.0, 0);
            procedures.add(headerItem);
            listView.setItems(procedures);
        }
    }

    /**
     * Loads and displays procedures for a specified patient in the list view.
     * This method retrieves the procedures from the database and populates the ListView.
     *
     * @param patientNumber The unique identifier of the patient whose procedures are to be loaded
     * @throws SQLException If a database error occurs during the data retrieval process
     */
    private void loadProceduresForPatient(int patientNumber) throws SQLException {
        System.out.println("Loading procedures for patient " + patientNumber + " using RiverGreenDB");

        // Get procedures for the patient using the RiverGreenDB class
        ObservableList<TreatmentPlanProcedure> loadedProcedures = RiverGreenDB.getProceduresForPatientObservable(patientNumber);
        System.out.println("Retrieved " + loadedProcedures.size() + " procedures for patient " + patientNumber);

        // Create a new list with the header item at the beginning
        ObservableList<TreatmentPlanProcedure> proceduresWithHeader = FXCollections.observableArrayList();

        // Add the header item
        TreatmentPlanProcedure headerItem = new TreatmentPlanProcedure(
            "Priority", "Tth", "Surf", "Code", "Diagnosis", "Description", 0.0, 0);
        proceduresWithHeader.add(headerItem);

        // Add all the loaded procedures
        proceduresWithHeader.addAll(loadedProcedures);

        // Set the items in the list view
        procedures.setAll(proceduresWithHeader);
    }

    /**
     * Sets up the priority list view with priorities from the database and configures its click handler.
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
            ObservableList<TreatmentPlanProcedure> selectedProcedures = listView.getSelectionModel().getSelectedItems();

            if (selectedPriority != null && !selectedProcedures.isEmpty()) {
                // Store the selected indices before updating
                List<Integer> selectedIndices = listView.getSelectionModel().getSelectedIndices();

                // Update all selected procedures with the new priority
                for (TreatmentPlanProcedure procedure : selectedProcedures) {
                    procedure.setPriority(selectedPriority);
                }

                // Refresh the list view to show the updated priorities
                listView.refresh();

                // Restore the selection
                listView.getSelectionModel().clearSelection();
                for (Integer index : selectedIndices) {
                    listView.getSelectionModel().select(index);
                }

                // Reset drag selection tracking variables
                dragStartIndex = -1;
                isDragging = false;
                dragStartY = -1;
            }
        });
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
     * Handles the action event triggered by the "Ok" button.
     * Reads all list items, updates the procedures in the database, and prints the results.
     */
    @FXML
    private void handleOkButtonAction() {
        System.out.println("OK button clicked");

        // Get the patient number from the data cache
        Integer patientNumber = (Integer) SceneSwitcher.getData("patientNumber");
        if (patientNumber == null) {
            System.out.println("No patient number found. Cannot update procedures.");
            return;
        }

        // Get all items from the list view (excluding the header)
        List<TreatmentPlanProcedure> allProcedures = new ArrayList<>();
        for (int i = 1; i < listView.getItems().size(); i++) { // Start from 1 to skip header
            allProcedures.add(listView.getItems().get(i));
        }

        if (allProcedures.isEmpty()) {
            System.out.println("No procedures found for Patient #" + patientNumber);
            return;
        }

        // Call the RiverGreenDB method to update the procedures
        Map<String, Object> results = RiverGreenDB.updateTreatmentPlanProcedures(patientNumber, allProcedures);

        // Extract results
        int successCount = (int) results.get("successCount");
        int failureCount = (int) results.get("failureCount");
        @SuppressWarnings("unchecked")
        List<String> errorMessages = (List<String>) results.get("errorMessages");
        String status = (String) results.get("status");
        @SuppressWarnings("unchecked")
        List<String> sqlQueries = (List<String>) results.get("sqlQueries");

        // Store the queries in the data cache for potential future use
        SceneSwitcher.putData("pendingSqlQueries", sqlQueries);

        // Print summary
        System.out.println("\nExecution summary for Patient #" + patientNumber + ":");
        System.out.println("Status: " + status);
        System.out.println("Total queries: " + (sqlQueries != null ? sqlQueries.size() : 0));
        System.out.println("Successful: " + successCount);
        System.out.println("Failed: " + failureCount);

        if (errorMessages != null && !errorMessages.isEmpty()) {
            System.out.println("\nError details:");
            for (String error : errorMessages) {
                System.out.println("- " + error);
            }
        }
    }

    /**
     * Handles the action event triggered by the "Cancel" button.
     * Navigates back to the main scene.
     */
    @FXML
    private void handleCancelButtonAction() {
        System.out.println("Cancel button clicked");
        try {
            navigateToMain();
        } catch (IOException e) {
            handleError(e);
        }
    }

    /**
     * Handles the ruleset button action.
     * Opens the ruleset configuration window.
     */
    @FXML
    private void handleRulesetButtonAction() {
        System.out.println("Ruleset button clicked");
        try {
            SceneSwitcher.switchScene("dredit", "Ruleset Configuration");
        } catch (IOException e) {
            handleError(e);
        }
    }

    /**
     * Loads all rulesets from CSV files.
     * Creates default rulesets if the files don't exist.
     */
    private void loadRulesets() {
        System.out.println("Loading rulesets");

        // Load ruleset A
        List<RulesetItem> rulesetA = loadRulesetFromFile("rulesetA.csv");
        if (rulesetA.isEmpty()) {
            // Create a default ruleset A
            rulesetA.add(new RulesetItem("Header", "Header"));
            rulesetA.add(new RulesetItem("1", "D2140"));
            rulesetA.add(new RulesetItem("2", "D2150"));
            rulesetA.add(new RulesetItem("3", "D2160"));
        }
        rulesets.put("A", rulesetA);

        // Load ruleset B
        List<RulesetItem> rulesetB = loadRulesetFromFile("rulesetB.csv");
        if (rulesetB.isEmpty()) {
            // Create a default ruleset B
            rulesetB.add(new RulesetItem("Header", "Header"));
            rulesetB.add(new RulesetItem("1", "D2330"));
            rulesetB.add(new RulesetItem("2", "D2331"));
            rulesetB.add(new RulesetItem("3", "D2332"));
        }
        rulesets.put("B", rulesetB);

        // Set up the ruleset selection menu
        setupRulesetSelectMenu();
    }

    /**
     * Loads a ruleset from a CSV file.
     *
     * @param filename The name of the CSV file
     * @return The list of ruleset items
     */
    private List<RulesetItem> loadRulesetFromFile(String filename) {
        List<RulesetItem> items = new ArrayList<>();
        File file = new File(filename);

        if (!file.exists()) {
            // Add a header item
            items.add(new RulesetItem("Header", "Header"));
            return items;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Add a header item
            items.add(new RulesetItem("Header", "Header"));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    items.add(new RulesetItem(parts[0], parts[1]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return items;
    }

    /**
     * Sets up the ruleset selection menu.
     * Adds menu items for each ruleset and sets up the event handlers.
     */
    private void setupRulesetSelectMenu() {
        if (rulesetSelectMenuButton == null) {
            System.out.println("Ruleset select menu button is null");
            return;
        }

        rulesetSelectMenuButton.getItems().clear();

        // Add menu items for the default rulesets
        javafx.scene.control.MenuItem itemA = new javafx.scene.control.MenuItem("Ruleset A");
        javafx.scene.control.MenuItem itemB = new javafx.scene.control.MenuItem("Ruleset B");

        // Set up event handlers for the menu items
        itemA.setOnAction(event -> {
            currentRuleset = "A";
            rulesetSelectMenuButton.setText("Ruleset A");
            applyRuleset(currentRuleset);
        });

        itemB.setOnAction(event -> {
            currentRuleset = "B";
            rulesetSelectMenuButton.setText("Ruleset B");
            applyRuleset(currentRuleset);
        });

        // Add the menu items to the menu button
        rulesetSelectMenuButton.getItems().addAll(itemA, itemB);
        rulesetSelectMenuButton.setText("Select Ruleset");
    }

    /**
     * Applies the selected ruleset to the procedures list.
     * Updates the priorities of the procedures based on the ruleset.
     *
     * @param rulesetName The name of the ruleset to apply
     */
    private void applyRuleset(String rulesetName) {
        System.out.println("Applying ruleset " + rulesetName);

        if (!rulesets.containsKey(rulesetName)) {
            System.out.println("Ruleset " + rulesetName + " not found");
            return;
        }

        List<RulesetItem> ruleset = rulesets.get(rulesetName);

        // Skip the header item (index 0)
        for (int i = 1; i < ruleset.size(); i++) {
            RulesetItem item = ruleset.get(i);
            String procedureCode = item.getProcedureCode();
            String priority = item.getPriority();

            // Update the priority of all procedures with matching procedure code
            for (int j = 1; j < procedures.size(); j++) { // Skip the header item (index 0)
                TreatmentPlanProcedure procedure = procedures.get(j);
                if (procedure.getProcedureCode().equals(procedureCode)) {
                    procedure.setPriority(priority);
                }
            }
        }

        // Refresh the list view to show the updated priorities
        listView.refresh();
    }

    /**
     * Sets up drag and drop functionality for the list view.
     * This allows users to drag items from a multi-select table into the list view.
     */
    private void setupDragAndDrop() {
        // Set up the list view to accept drag and drop operations
        listView.setOnDragOver(this::handleDragOver);
        listView.setOnDragDropped(this::handleDragDropped);
    }

    // Variables to track drag selection
    private int dragStartIndex = -1;
    private boolean isDragging = false;
    private double dragStartY = -1; // Store the initial Y position when drag starts (for backward compatibility)

    /**
     * Gets the item index directly from the ListView at the specified mouse coordinates.
     * This is more accurate than calculating based on estimated item heights.
     * 
     * @param event The mouse event containing coordinates
     * @return The item index at the mouse position, or -1 if none
     */
    private int getListItemIndex(MouseEvent event) {
        // First try to use the built-in picking method of ListView
        javafx.scene.Node node = event.getPickResult().getIntersectedNode();

        // Traverse up until we find a ListCell
        while (node != null && !(node instanceof javafx.scene.control.ListCell)) {
            node = node.getParent();
        }

        // If we found a ListCell, get its index
        if (node instanceof javafx.scene.control.ListCell) {
            javafx.scene.control.ListCell<?> cell = (javafx.scene.control.ListCell<?>) node;
            return cell.getIndex();
        }

        // Fallback to the old method if we couldn't find a cell
        return getItemIndexAt(event.getY());
    }

    /**
     * Sets up multi-select functionality for the list view.
     * This allows users to select multiple items by dragging, Ctrl+Click, or Shift+Click.
     */
    private void setupMultiSelect() {
        System.out.println("[DEBUG] setupMultiSelect() called");
        // Set up mouse event handlers for drag selection
        listView.setOnMousePressed(this::handleMousePressed);
        listView.setOnMouseDragged(this::handleMouseDragged);
        listView.setOnMouseReleased(this::handleMouseReleased);
    }

    /**
     * Handles mouse pressed events to start drag selection.
     * 
     * @param event The mouse event
     */
    private void handleMousePressed(MouseEvent event) {
        System.out.println("[DEBUG] handleMousePressed() called, Y position: " + event.getY());

        // Get the item index at the mouse position using the direct method
        int itemIndex = getListItemIndex(event);
        System.out.println("[DEBUG] Item index at position (direct lookup): " + itemIndex);

        // Always skip the header item (index 0)
        if (itemIndex == 0) {
            System.out.println("[DEBUG] Skipping header item (index 0)");
            return;
        }

        if (itemIndex >= 0 && itemIndex < listView.getItems().size()) {
            // Start drag selection
            dragStartIndex = itemIndex;
            dragStartY = event.getY(); // Store the initial Y position for backward compatibility
            isDragging = true;
            System.out.println("[DEBUG] Starting drag selection at index: " + dragStartIndex);

            // If not holding Ctrl or Shift, clear previous selection
            if (!event.isControlDown() && !event.isShiftDown()) {
                System.out.println("[DEBUG] Clearing previous selection (no Ctrl/Shift)");
                listView.getSelectionModel().clearSelection();
            } else {
                System.out.println("[DEBUG] Keeping previous selection (Ctrl/Shift pressed)");
            }

            // Select the clicked item
            listView.getSelectionModel().select(itemIndex);
            System.out.println("[DEBUG] Selected item at index: " + itemIndex);

            // Consume the event to prevent default handling
            event.consume();
        } else {
            System.out.println("[DEBUG] Invalid item index: " + itemIndex);
        }
    }

    /**
     * Handles mouse dragged events to update selection during dragging.
     * 
     * @param event The mouse event
     */
    private void handleMouseDragged(MouseEvent event) {

        if (!isDragging || dragStartIndex < 0) {
            return;
        }

        // Handle auto-scrolling when dragging outside the visible area
        handleAutoScroll(event);

        // Get the current item index using direct lookup
        int currentIndex = getListItemIndex(event);

        // If we couldn't determine the current index (returned -1), try to estimate based on the last known position
        if (currentIndex < 0) {
            // Use the old method as fallback
            currentIndex = getItemIndexAt(event.getY());
        }

        // Ensure the current index is within bounds
        int originalIndex = currentIndex;
        currentIndex = Math.max(0, Math.min(currentIndex, listView.getItems().size() - 1));

        // Store the current selection
        List<Integer> currentSelection = new ArrayList<>(listView.getSelectionModel().getSelectedIndices());

        // Clear previous selection
        listView.getSelectionModel().clearSelection();

        // Select all items between start index and current index
        int startIdx = Math.min(dragStartIndex, currentIndex);
        int endIdx = Math.max(dragStartIndex, currentIndex);

        // Skip the header item (index 0) if it exists
        int selectedCount = 0;
        for (int i = startIdx; i <= endIdx; i++) {
            // Always skip index 0 (header)
            if (i == 0) {
                continue;
            }
            listView.getSelectionModel().select(i);
            selectedCount++;
            }

        // Consume the event to prevent default handling
        event.consume();
    }

    /**
     * Handles auto-scrolling when dragging outside the visible area.
     * 
     * @param event The mouse event
     */
    private void handleAutoScroll(MouseEvent event) {
        double mouseY = event.getY();
        double listHeight = listView.getHeight();

        // Get the vertical scroll bar
        javafx.scene.control.ScrollBar verticalScrollBar = (javafx.scene.control.ScrollBar) listView.lookup(".scroll-bar:vertical");
        if (verticalScrollBar == null || !verticalScrollBar.isVisible()) {
            return; // No scrolling needed if scroll bar is not visible
        }

        // Define the auto-scroll zones (top and bottom 10% of the list)
        double scrollZoneSize = listHeight * 0.1;
        double scrollSpeed = 0.05; // Adjust this value to control scroll speed

        // Check if mouse is in the top scroll zone
        if (mouseY < scrollZoneSize) {
            // Scroll up
            double newValue = Math.max(0, verticalScrollBar.getValue() - scrollSpeed);
            verticalScrollBar.setValue(newValue);
        } 
        // Check if mouse is in the bottom scroll zone
        else if (mouseY > listHeight - scrollZoneSize) {
            // Scroll down
            double newValue = Math.min(verticalScrollBar.getMax(), verticalScrollBar.getValue() + scrollSpeed);
            verticalScrollBar.setValue(newValue);
        }
    }

    /**
     * Handles mouse released events to finalize selection.
     * 
     * @param event The mouse event
     */
    private void handleMouseReleased(MouseEvent event) {
        System.out.println("[DEBUG] handleMouseReleased() called, Y position: " + event.getY());

        // Get the final item index at release position
        int finalIndex = getListItemIndex(event);
        System.out.println("[DEBUG] Final item index at release position: " + finalIndex);

        // End drag selection
        boolean wasDragging = isDragging;
        int previousDragStartIndex = dragStartIndex;

        isDragging = false;
        dragStartY = -1; // Reset the drag start Y position for backward compatibility

        System.out.println("[DEBUG] Drag selection ended. Was dragging: " + wasDragging + 
                           ", Start index: " + previousDragStartIndex + 
                           ", End index: " + finalIndex + 
                           ", Selected items count: " + listView.getSelectionModel().getSelectedIndices().size());

        // Consume the event to prevent default handling
        event.consume();
    }

    /**
     * Gets the item index at the specified y-coordinate.
     * 
     * @param y The y-coordinate
     * @return The item index at the specified y-coordinate, or -1 if none
     */
    private int getItemIndexAt(double y) {
        // Calculate the item index based on the y-coordinate
        double itemHeight = estimateItemHeight();

        // Get the scroll position
        double scrollOffset = 0.0;
        javafx.scene.control.ScrollBar verticalScrollBar = (javafx.scene.control.ScrollBar) listView.lookup(".scroll-bar:vertical");
        if (verticalScrollBar != null && verticalScrollBar.isVisible()) {
            scrollOffset = verticalScrollBar.getValue() * itemHeight;
        }

        // Calculate the visible item index (relative to what's visible)
        int visibleItemIndex = (int) Math.floor(y / itemHeight);

        // Calculate the actual item index (accounting for scrolling)
        int actualItemIndex = visibleItemIndex + (int) Math.floor(scrollOffset / itemHeight);

        // Ensure the index is within bounds
        if (actualItemIndex < 0) {
            return -1;
        } else if (actualItemIndex >= listView.getItems().size()) {
            return listView.getItems().size() - 1;
        }

        return actualItemIndex;
    }

    /**
     * Estimates the average item height in the list view.
     * 
     * @return The estimated item height
     */
    private double estimateItemHeight() {
        // If fixed cell size is set, use that
        if (listView.getFixedCellSize() > 0) {
            return listView.getFixedCellSize();
        }

        // Try to get the height from a visible cell
        for (javafx.scene.Node node : listView.lookupAll(".list-cell")) {
            if (node.isVisible() && node.getBoundsInLocal().getHeight() > 0) {
                return node.getBoundsInLocal().getHeight();
            }
        }

        // Default fallback
        return 25.0;
    }

    /**
     * Handles drag over events on the list view.
     * This method is called when a drag operation is ongoing and the mouse is over the list view.
     * 
     * @param event The drag event
     */
    private void handleDragOver(DragEvent event) {
        // Accept the drag if it contains TreatmentPlanProcedure data
        if (event.getDragboard().hasContent(PROCEDURE_FORMAT)) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    /**
     * Handles drag dropped events on the list view.
     * This method is called when a drag operation ends with a drop on the list view.
     * 
     * @param event The drag event
     */
    private void handleDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;

        if (db.hasContent(PROCEDURE_FORMAT)) {
            // Get the dragged items from the dragboard
            @SuppressWarnings("unchecked")
            List<TreatmentPlanProcedure> draggedItems = (List<TreatmentPlanProcedure>) db.getContent(PROCEDURE_FORMAT);

            // Add the dragged items to the list view
            for (TreatmentPlanProcedure procedure : draggedItems) {
                // Create a copy of the procedure to avoid reference issues
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

                // Add to the list view
                procedures.add(copy);
            }

            success = true;
        }

        event.setDropCompleted(success);
        event.consume();
    }
}
