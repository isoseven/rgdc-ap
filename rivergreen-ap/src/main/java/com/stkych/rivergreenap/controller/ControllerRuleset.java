package com.stkych.rivergreenap.controller;

import com.stkych.rivergreenap.RiverGreenDB;
import com.stkych.rivergreenap.SceneSwitcher;
import com.stkych.rivergreenap.controller.cells.RulesetItemCellFactory;
import com.stkych.rivergreenap.model.RulesetItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

/**
 * Controller for the ruleset window.
 * Handles the UI interactions and implements the business logic for the ruleset window.
 */
public class ControllerRuleset implements Initializable {

    @FXML
    private ListView<RulesetItem> listView;

    @FXML
    private Label patientNameLabel;

    @FXML
    private MenuButton rulesetSelectMenuButton;

    @FXML
    private Button newButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button okButton;

    private ObservableList<RulesetItem> rulesetItems = FXCollections.observableArrayList();
    private Map<String, List<RulesetItem>> rulesets = new HashMap<>();
    private String currentRuleset = "A";

    /**
     * Initializes the controller.
     * Sets up the UI components and loads the initial data.
     *
     * @param location The location used to resolve relative paths for the root object
     * @param resources The resources used to localize the root object
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up the ListView with the cell factory
        listView.setCellFactory(new RulesetItemCellFactory());
        listView.setItems(rulesetItems);

        // Set up the button handlers
        setupButtonHandlers();

        // Load the initial ruleset data
        loadRulesets();

        // Set up the ruleset selection menu
        setupRulesetSelectMenu();

        // Load the selected ruleset
        loadRuleset(currentRuleset);

        // Update the window title
        patientNameLabel.setText("Ruleset Configuration");

        // Add a listener to check for the refresh flag when the window regains focus
        Stage stage = (Stage) listView.getScene().getWindow();
        stage.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { // When the window regains focus
                Boolean refreshFlag = (Boolean) SceneSwitcher.getData("refreshRulesetWindow");
                if (refreshFlag != null && refreshFlag) {
                    // Refresh the ruleset window
                    loadRulesets();
                    setupRulesetSelectMenu();
                    loadRuleset(currentRuleset);

                    // Clear the flag
                    SceneSwitcher.removeData("refreshRulesetWindow");
                }
            }
        });
    }

    /**
     * Sets up the ruleset selection menu.
     * Adds menu items for each ruleset and sets up the event handlers.
     */
    private void setupRulesetSelectMenu() {
        rulesetSelectMenuButton.getItems().clear();

        // Add menu items for all available rulesets
        for (String rulesetName : rulesets.keySet()) {
            MenuItem item = new MenuItem("Ruleset " + rulesetName);

            // Set up event handler for the menu item
            item.setOnAction(event -> {
                currentRuleset = rulesetName;
                rulesetSelectMenuButton.setText("Ruleset " + rulesetName);
                loadRuleset(currentRuleset);
            });

            // Add the menu item to the menu button
            rulesetSelectMenuButton.getItems().add(item);
        }

        // Set default text if there are rulesets
        if (!rulesets.isEmpty()) {
            // Use the first ruleset as the default
            String firstRuleset = rulesets.keySet().iterator().next();
            currentRuleset = firstRuleset;
            rulesetSelectMenuButton.setText("Ruleset " + firstRuleset);
            rulesetSelectMenuButton.setDisable(false);
        } else {
            rulesetSelectMenuButton.setText("No Rulesets Available");
            rulesetSelectMenuButton.setDisable(true);
        }
    }

    /**
     * Sets up the button handlers.
     * Adds event handlers for the new, edit, delete, ok, and cancel buttons.
     */
    private void setupButtonHandlers() {
        // New button handler
        newButton.setOnAction(event -> handleNewButtonAction());

        // Edit button handler
        editButton.setOnAction(event -> handleEditButtonAction());

        // Delete button handler
        deleteButton.setOnAction(event -> handleDeleteButtonAction());

        // OK button handler
        okButton.setOnAction(event -> handleOkButtonAction());
    }

    /**
     * Handles the new button action.
     * Shows a dialog to create a new ruleset item.
     */
    @FXML
    private void handleNewButtonAction() {
        // Create a dialog to get the new ruleset item details
        Dialog<RulesetItem> dialog = new Dialog<>();
        dialog.setTitle("New Ruleset Item");
        dialog.setHeaderText("Enter the details for the new ruleset item");

        // Set the button types
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/stkych/rivergreenap/ruleset_dialog.fxml"));
            Parent root = loader.load();

            // Get the controller
            RulesetDialogController controller = loader.getController();

            // Set up the combo boxes
            controller.getPriorityComboBox().setItems(getAllPriorities());

            // The procedure codes are already loaded from the database in the controller's initialize method
            controller.getProcedureCodeComboBox().setEditable(true);

            // Add a listener to the procedure code combo box to update the description
            controller.getProcedureCodeComboBox().valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null && !newValue.isEmpty()) {
                    try {
                        String description = RiverGreenDB.getProcedureCodeDescription(newValue);
                        controller.setDescription(description);
                    } catch (SQLException e) {
                        controller.setDescription("Description not available");
                        e.printStackTrace();
                    }
                } else {
                    controller.setDescription("");
                }
            });

            // Set the content of the dialog
            dialog.getDialogPane().setContent(root);

            // Convert the result to a RulesetItem when the OK button is clicked
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == okButtonType) {
                    String priority = controller.getPriorityComboBox().getValue();
                    if (priority == null || priority.isEmpty()) {
                        priority = controller.getPriorityComboBox().getEditor().getText();
                    }
                    String procedureCode = controller.getProcedureCodeComboBox().getValue();
                    if (procedureCode == null) {
                        procedureCode = controller.getProcedureCodeComboBox().getEditor().getText();
                    }
                    String description = controller.getDescriptionLabel().getText();
                    String teethNumbers = controller.getSelectedTeethAsString();
                    return new RulesetItem(priority, procedureCode, description, teethNumbers);
                }
                return null;
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Show the dialog and process the result
        Optional<RulesetItem> result = dialog.showAndWait();
        result.ifPresent(item -> {
            rulesetItems.add(item);
            sortRulesetItems();
            saveRuleset(currentRuleset);
        });
    }

    /**
     * Handles the edit button action.
     * Opens the ruleset configuration window as a popup.
     */
    @FXML
    private void handleEditButtonAction() {
        System.out.println("Edit Ruleset button clicked");

        try {
            // Clear any existing refresh flag
            SceneSwitcher.removeData("refreshRulesetWindow");

            // Show the popup
            SceneSwitcher.showPopup("ruleset_config", "Ruleset Configuration", 600, 400);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the delete button action.
     * Shows a confirmation dialog and deletes the selected ruleset item if confirmed.
     */
    @FXML
    private void handleDeleteButtonAction() {
        // Get the selected item
        RulesetItem selectedItem = listView.getSelectionModel().getSelectedItem();
        if (selectedItem == null || listView.getSelectionModel().getSelectedIndex() == 0) {
            // No item selected or header selected
            return;
        }

        // Show a confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Ruleset Item");
        alert.setHeaderText("Delete Ruleset Item");
        alert.setContentText("Are you sure you want to delete this ruleset item?");

        // Process the result
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            rulesetItems.remove(selectedItem);
            saveRuleset(currentRuleset);
        }
    }

    /**
     * Handles the OK button action.
     * Saves the current ruleset and closes the window.
     */
    @FXML
    private void handleOkButtonAction() {
        saveRuleset(currentRuleset);
        try {
            SceneSwitcher.switchScene("main", "RiverGreen AP");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Handles the Select Patient menu item action.
     * Opens a dialog to enter a patient number and reloads the application with that patient.
     */
    @FXML
    private void handleSelectPatientAction() {
        System.out.println("Select Patient menu item clicked");

        // Create a dialog to enter patient number
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Select Patient");
        dialog.setHeaderText("Enter Patient Number");
        dialog.setContentText("Patient ID:");

        // Show the dialog and wait for a response
        dialog.showAndWait().ifPresent(patientIdStr -> {
            try {
                // Parse the patient number
                int patientNumber = Integer.parseInt(patientIdStr);

                // Store the new patient number in the data cache
                SceneSwitcher.putData("patientNumber", patientNumber);

                // Reload the main scene with the new patient number
                SceneSwitcher.switchScene("main", "RiverGreen Dental Application");

            } catch (NumberFormatException e) {
                // Show an error if the input is not a valid number
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid Patient Number");
                alert.setContentText("Please enter a valid patient number.");
                alert.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Handles the Reset menu item action.
     * Resets all priorities and diagnosis.
     */
    @FXML
    private void handleResetAction() {
        System.out.println("Reset menu item clicked");

        // Confirm before resetting
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Reset");
        confirmAlert.setHeaderText("Reset All Ruleset Items");
        confirmAlert.setContentText("Are you sure you want to reset all ruleset items? This action cannot be undone.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Reload the current ruleset from file
                loadRuleset(currentRuleset);

                // Show success message
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Reset Complete");
                successAlert.setHeaderText(null);
                successAlert.setContentText("All ruleset items have been reset.");
                successAlert.showAndWait();
            }
        });
    }

    /**
     * Handles the Close menu item action.
     * Closes the current window.
     */
    @FXML
    private void handleCloseAction() {
        System.out.println("Close menu item clicked");

        // Get the current stage and close it
        Stage stage = (Stage) listView.getScene().getWindow();
        stage.close();
    }

    /**
     * Handles the Patient View menu item action.
     * Navigates to the main patient view.
     */
    @FXML
    private void handlePatientViewAction() {
        System.out.println("Patient View menu item clicked");

        try {
            SceneSwitcher.switchScene("main", "RiverGreen Dental Application");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the Manage Rulesets menu item action.
     * Opens the ruleset configuration window as a popup.
     */
    @FXML
    private void handleManageRulesetsAction() {
        System.out.println("Manage Rulesets menu item clicked");

        try {
            // Clear any existing refresh flag
            SceneSwitcher.removeData("refreshRulesetWindow");

            // Show the popup
            SceneSwitcher.showPopup("ruleset_config", "Ruleset Configuration", 600, 400);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the About menu item action.
     * Shows information about the application.
     */
    @FXML
    private void handleAboutAction() {
        System.out.println("About menu item clicked");

        // Create an alert dialog
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About RiverGreen Dental Application");
        alert.setHeaderText("RiverGreen Dental Application");
        alert.setContentText("Version 1.0\n\nA dental treatment planning application for automating procedure priorities.");

        // Add more detailed information in an expandable area
        Label label = new Label("Additional Information:");
        TextArea textArea = new TextArea(
            "RiverGreen Dental Application\n" +
            "Version 1.0\n\n" +
            "This application helps dental professionals manage treatment plans by automatically prioritizing procedures based on configurable rulesets.\n\n" +
            "Features:\n" +
            "- Patient-specific treatment plans\n" +
            "- Configurable priority rulesets\n" +
            "- Drag-and-drop procedure reordering\n" +
            "- Database integration\n\n" +
            "© 2025 RiverGreen Dental"
        );
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefHeight(200);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);
        alert.getDialogPane().setExpanded(true);

        alert.showAndWait();
    }

    /**
     * Loads all rulesets from CSV files.
     * No longer creates default rulesets if none are found.
     */
    private void loadRulesets() {
        // Clear existing rulesets
        rulesets.clear();

        // Look for ruleset files
        File currentDir = new File(".");
        File[] files = currentDir.listFiles((dir, name) -> name.startsWith("ruleset") && name.endsWith(".csv"));

        if (files != null) {
            for (File file : files) {
                String filename = file.getName();
                // Extract the ruleset name (between "ruleset" and ".csv")
                String rulesetName = filename.substring(7, filename.length() - 4);

                // Load the ruleset
                List<RulesetItem> rulesetItems = loadRulesetFromFile(filename);
                if (!rulesetItems.isEmpty()) {
                    rulesets.put(rulesetName, rulesetItems);
                }
            }
        }

        // No longer creating default rulesets if none are found
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
            items.add(new RulesetItem("Header", "Header", "Description", "Teeth"));
            return items;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Add a header item
            items.add(new RulesetItem("Header", "Header", "Description", "Teeth"));

            String line;
            while ((line = reader.readLine()) != null) {
                // Split by comma
                String[] parts = line.split(",");

                if (parts.length >= 2) {
                    String priority = parts[0];
                    String procedureCode = parts[1];
                    String teethNumbers = "";
                    String diagnosis = "";

                    // If there are more than 2 parts, the rest are teeth numbers and diagnosis
                    if (parts.length > 2) {
                        // Determine how many parts are for teeth numbers
                        int diagnosisIndex = -1;

                        // Look for a part that doesn't look like a tooth number (not a number)
                        for (int i = 2; i < parts.length; i++) {
                            try {
                                Integer.parseInt(parts[i].trim());
                            } catch (NumberFormatException e) {
                                // This part is not a number, so it's the start of the diagnosis
                                diagnosisIndex = i;
                                break;
                            }
                        }

                        // If we found a diagnosis part
                        if (diagnosisIndex > 2) {
                            // Teeth numbers are all parts from index 2 to diagnosisIndex-1
                            StringBuilder teethBuilder = new StringBuilder();
                            for (int i = 2; i < diagnosisIndex; i++) {
                                if (i > 2) teethBuilder.append("-");
                                teethBuilder.append(parts[i].trim());
                            }
                            teethNumbers = teethBuilder.toString();

                            // Diagnosis is all parts from diagnosisIndex to the end
                            StringBuilder diagnosisBuilder = new StringBuilder();
                            for (int i = diagnosisIndex; i < parts.length; i++) {
                                if (i > diagnosisIndex) diagnosisBuilder.append(",");
                                diagnosisBuilder.append(parts[i].trim());
                            }
                            diagnosis = diagnosisBuilder.toString();
                        } else if (diagnosisIndex == 2) {
                            // No teeth numbers, just diagnosis
                            StringBuilder diagnosisBuilder = new StringBuilder();
                            for (int i = 2; i < parts.length; i++) {
                                if (i > 2) diagnosisBuilder.append(",");
                                diagnosisBuilder.append(parts[i].trim());
                            }
                            diagnosis = diagnosisBuilder.toString();
                        } else {
                            // All remaining parts are teeth numbers
                            StringBuilder teethBuilder = new StringBuilder();
                            for (int i = 2; i < parts.length; i++) {
                                if (i > 2) teethBuilder.append("-");
                                teethBuilder.append(parts[i].trim());
                            }
                            teethNumbers = teethBuilder.toString();
                        }
                    }

                    // Always fetch description from the database
                    String description = "";
                    try {
                        description = RiverGreenDB.getProcedureCodeDescription(procedureCode);
                    } catch (SQLException e) {
                        // If there's an error, use an empty description
                        e.printStackTrace();
                    }

                    RulesetItem item = new RulesetItem(priority, procedureCode, description, teethNumbers);
                    if (!diagnosis.isEmpty()) {
                        item.setDiagnosis(diagnosis);
                    }
                    items.add(item);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return items;
    }

    /**
     * Loads a ruleset into the ListView.
     *
     * @param rulesetName The name of the ruleset to load
     */
    private void loadRuleset(String rulesetName) {
        rulesetItems.clear();
        List<RulesetItem> ruleset = rulesets.get(rulesetName);
        if (ruleset != null) {
            rulesetItems.addAll(ruleset);
        }
    }

    /**
     * Saves a ruleset to a CSV file.
     *
     * @param rulesetName The name of the ruleset to save
     */
    private void saveRuleset(String rulesetName) {
        String filename = "ruleset" + rulesetName + ".csv";
        File file = new File(filename);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int i = 1; i < rulesetItems.size(); i++) {
                RulesetItem item = rulesetItems.get(i);
                // Write priority, procedure code, teeth numbers, and diagnosis (not description)
                // Use comma as delimiter
                StringBuilder line = new StringBuilder();
                line.append(item.getPriority()).append(",");
                line.append(item.getProcedureCode());

                // Add teeth numbers if present
                String teethNumbers = item.getTeethNumbers();
                if (teethNumbers != null && !teethNumbers.isEmpty()) {
                    // Replace commas with hyphens in teeth numbers
                    String formattedTeethNumbers = teethNumbers.replace(",", "-");
                    line.append(",").append(formattedTeethNumbers);
                }

                // Add diagnosis if present
                String diagnosis = item.getDiagnosis();
                if (diagnosis != null && !diagnosis.isEmpty()) {
                    line.append(",").append(diagnosis);
                }

                writer.write(line.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Update the rulesets map
        List<RulesetItem> items = new ArrayList<>(rulesetItems);
        rulesets.put(rulesetName, items);
    }

    /**
     * Sorts the ruleset items by priority.
     */
    private void sortRulesetItems() {
        // Create a copy of the items without the header
        List<RulesetItem> itemsWithoutHeader = new ArrayList<>(rulesetItems.subList(1, rulesetItems.size()));

        // Sort the items by priority
        itemsWithoutHeader.sort(Comparator.comparing(RulesetItem::getPriority));

        // Clear the items and add the header and sorted items
        rulesetItems.clear();
        rulesetItems.add(new RulesetItem("Header", "Header", "Description", "Teeth"));
        rulesetItems.addAll(itemsWithoutHeader);
    }

    /**
     * Gets all available priorities as an ObservableList.
     * This method loads priorities from the database and sorts them in a specific order.
     *
     * @return An ObservableList of priority names
     */
    private ObservableList<String> getAllPriorities() {
        ObservableList<String> priorities;
        try {
            // Get priorities from the database
            priorities = RiverGreenDB.getAllPrioritiesObservable();
            // Sort the priorities
            sortPriorities(priorities);
        } catch (SQLException e) {
            // If there's an error, create a default list
            priorities = FXCollections.observableArrayList();
            e.printStackTrace();
        }

        // If the list is empty, add some default priorities
        if (priorities.isEmpty()) {
            priorities.add("Next");
            for (int i = 1; i <= 10; i++) {
                priorities.add(String.valueOf(i));
                priorities.add(i + "A");
                priorities.add(i + "B");
                priorities.add(i + "C");
                priorities.add(i + " Wait");
                priorities.add(i + " Decline");
            }
        }

        return priorities;
    }

    /**
     * Sorts the priorities in a specific order.
     *
     * @param priorities The list of priorities to sort
     */
    private void sortPriorities(ObservableList<String> priorities) {
        // Create a new list to append to
        ObservableList<String> sortedPriorities = FXCollections.observableArrayList();

        // Start by appending "None" if it's not already in the list
        if (!priorities.contains("None")) {
            sortedPriorities.add("None");
        }

        // Then append "Next"
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
}
