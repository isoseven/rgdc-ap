package com.stkych.rivergreenap.controller;

import com.stkych.rivergreenap.RiverGreenDB;
import com.stkych.rivergreenap.SceneSwitcher;
import com.stkych.rivergreenap.controller.cells.RulesetItemCellFactory;
import com.stkych.rivergreenap.model.RulesetItem;
import com.stkych.rivergreenap.util.FileUtils;
import com.stkych.rivergreenap.util.TeethNotationUtil;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the ruleset window.
 * Handles the UI interactions and implements the business logic for the ruleset window.
 */
public class ControllerRuleset implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(ControllerRuleset.class.getName());

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

        // Add a listener to the scene property to set up the focus listener once the scene is available
        listView.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                // Now that the scene is available, we can get the window and add the focus listener
                Stage stage = (Stage) newScene.getWindow();
                stage.focusedProperty().addListener((obs, oldValue, newValue) -> {
                    if (newValue) { // When the window regains focus
                        Boolean refreshFlag = (Boolean) SceneSwitcher.getInstance().getData("refreshRulesetWindow");
                        if (refreshFlag != null && refreshFlag) {
                            // Refresh the ruleset window
                            loadRulesets();
                            setupRulesetSelectMenu();
                            loadRuleset(currentRuleset);

                            // Clear the flag
                            SceneSwitcher.getInstance().removeData("refreshRulesetWindow");
                        }
                    }
                });
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
                        LOGGER.log(Level.SEVERE, "Unexpected error", e);
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
                    String description = controller.getDescription();
                    String teethNumbers = controller.getSelectedTeethAsString();
                    String diagnosis = controller.getDiagnosis();

                    // Validate inputs
                    if (!validateRulesetItem(procedureCode, teethNumbers, priority, diagnosis)) {
                        return null;
                    }

                    RulesetItem item = new RulesetItem(priority, procedureCode, description, teethNumbers);
                    if (diagnosis != null && !diagnosis.isEmpty()) {
                        item.setDiagnosis(diagnosis);
                    }
                    return item;
                }
                return null;
            });
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unexpected error", e);
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
     * Handles the manage rulesets button action.
     * Opens the ruleset configuration window as a popup.
     */
    @FXML
    private void handleEditRulesetButtonAction() {
        try {
            // Clear any existing refresh flag
            SceneSwitcher.getInstance().removeData("refreshRulesetWindow");

            // Show the popup
            SceneSwitcher.getInstance().showPopup("ruleset_config", "Ruleset Configuration", 600, 400);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unexpected error", e);
        }
    }

    /**
     * Handles the edit button action.
     * Shows a dialog to edit the selected ruleset item.
     */
    @FXML
    private void handleEditButtonAction() {
        // Get the selected item
        RulesetItem selectedItem = listView.getSelectionModel().getSelectedItem();
        if (selectedItem == null || listView.getSelectionModel().getSelectedIndex() == 0) {
            // No item selected or header selected
            return;
        }

        LOGGER.info(() -> "Edit button clicked for item: " + selectedItem);

        // Create a dialog to edit the ruleset item details
        Dialog<RulesetItem> dialog = new Dialog<>();
        dialog.setTitle("Edit Ruleset Item");
        dialog.setHeaderText("Edit the details for the ruleset item");

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

            // Pre-populate the fields with the selected item's data
            controller.getPriorityComboBox().setValue(selectedItem.getPriority());
            controller.getProcedureCodeComboBox().setValue(selectedItem.getProcedureCode());
            controller.setDescription(selectedItem.getDescription());
            controller.setSelectedTeethFromString(selectedItem.getTeethNumbers());

            // Set the diagnosis ComboBox value if the selected item has a diagnosis
            if (selectedItem.getDiagnosis() != null && !selectedItem.getDiagnosis().isEmpty()) {
                controller.getDiagnosisComboBox().setValue(selectedItem.getDiagnosis());
            }

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
                        LOGGER.log(Level.SEVERE, "Unexpected error", e);
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
                    String description = controller.getDescription();
                    String teethNumbers = controller.getSelectedTeethAsString();
                    String diagnosis = controller.getDiagnosis();

                    // Validate inputs
                    if (!validateRulesetItem(procedureCode, teethNumbers, priority, diagnosis)) {
                        return null;
                    }

                    // Create a new RulesetItem with the updated values
                    RulesetItem updatedItem = new RulesetItem(priority, procedureCode, description, teethNumbers);

                    // Set the diagnosis from the controller if it's not null or empty
                    // Otherwise, preserve the existing diagnosis if it exists
                    if (diagnosis != null && !diagnosis.isEmpty()) {
                        updatedItem.setDiagnosis(diagnosis);
                    } else if (selectedItem.getDiagnosis() != null && !selectedItem.getDiagnosis().isEmpty()) {
                        updatedItem.setDiagnosis(selectedItem.getDiagnosis());
                    }
                    return updatedItem;
                }
                return null;
            });
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unexpected error", e);
        }

        // Show the dialog and process the result
        Optional<RulesetItem> result = dialog.showAndWait();
        result.ifPresent(updatedItem -> {
            // Replace the selected item with the updated item
            int selectedIndex = listView.getSelectionModel().getSelectedIndex();
            rulesetItems.set(selectedIndex, updatedItem);
            sortRulesetItems();
            saveRuleset(currentRuleset);
        });
    }

    /**
     * Shows an error alert with the specified title and message.
     *
     * @param title The title of the alert
     * @param message The message to display
     */
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Validates a ruleset item to ensure it has at least one of dental code or tooth selection,
     * and at least one of diagnosis or priority.
     *
     * @param procedureCode The dental procedure code
     * @param teethNumbers The selected teeth number as a string
     * @param priority The priority
     * @param diagnosis The diagnosis
     * @return true if the ruleset item is valid, false otherwise
     */
    private boolean validateRulesetItem(String procedureCode, String teethNumbers, String priority, String diagnosis) {
        boolean hasDentalCodeOrTeeth = (procedureCode != null && !procedureCode.isEmpty()) || 
                                      (teethNumbers != null && !teethNumbers.isEmpty());
        boolean hasDiagnosisOrPriority = (diagnosis != null && !diagnosis.isEmpty()) || 
                                        (priority != null && !priority.isEmpty());

        if (!hasDentalCodeOrTeeth) {
            showErrorAlert("Validation Error", "Each rule must have at least one of either dental code or tooth selection.");
            return false;
        }

        if (!hasDiagnosisOrPriority) {
            showErrorAlert("Validation Error", "Each rule must have at least one of either diagnosis or priority.");
            return false;
        }

        return true;
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
            SceneSwitcher.getInstance().switchScene("main", "RiverGreen AP");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unexpected error", e);
        }
    }


    /**
     * Handles the Select Patient menu item action.
     * Opens a dialog to enter a patient number and reloads the application with that patient.
     */
    @FXML
    private void handleSelectPatientAction() {
        LOGGER.info("Select Patient menu item clicked");

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
                SceneSwitcher.getInstance().putData("patientNumber", patientNumber);

                // Reload the main scene with the new patient number
                SceneSwitcher.getInstance().switchScene("main", "RiverGreen Dental Application");

            } catch (NumberFormatException e) {
                // Show an error if the input is not a valid number
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid Patient Number");
                alert.setContentText("Please enter a valid patient number.");
                alert.showAndWait();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Unexpected error", e);
            }
        });
    }

    /**
     * Handles the Reset menu item action.
     * Resets all priorities and diagnosis.
     */
    @FXML
    private void handleResetAction() {
        LOGGER.info("Reset menu item clicked");

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
        LOGGER.info("Close menu item clicked");

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
        LOGGER.info("Patient View menu item clicked");

        try {
            SceneSwitcher.getInstance().switchScene("main", "RiverGreen Dental Application");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unexpected error", e);
        }
    }

    /**
     * Handles the Manage Rulesets menu item action.
     * Opens the ruleset configuration window as a popup.
     */
    @FXML
    private void handleManageRulesetsAction() {
        LOGGER.info("Manage Rulesets menu item clicked");

        try {
            // Clear any existing refresh flag
            SceneSwitcher.getInstance().removeData("refreshRulesetWindow");

            // Show the popup
            SceneSwitcher.getInstance().showPopup("ruleset_config", "Ruleset Configuration", 600, 400);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unexpected error", e);
        }
    }

    /**
     * Handles the About menu item action.
     * Shows information about the application.
     */
    @FXML
    private void handleAboutAction() {
        LOGGER.info("About menu item clicked");

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

        // Migrate ruleset files from the current directory to the ruleset directory
        FileUtils.migrateRulesetFiles();

        // Look for ruleset files in the ruleset directory
        File rulesetDir = FileUtils.getRulesetDirectory();
        File[] files = rulesetDir.listFiles((dir, name) -> name.startsWith("ruleset") && name.endsWith(".csv"));

        if (files != null) {
            for (File file : files) {
                String filename = file.getName();
                // Extract the ruleset name (between "ruleset" and ".csv")
                String rulesetName = filename.substring(7, filename.length() - 4);

                // Load the ruleset
                List<RulesetItem> rulesetItems = loadRulesetFromFile(file.getAbsolutePath());
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
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                // Split by comma
                String[] parts = line.split(",");

                if (parts.length >= 2) {
                    // New format: priority,diagnosis,teeth,codes
                    String priority = parts[0].trim();
                    String diagnosis = "";
                    String teethNumbers = "";
                    String procedureCode = "";

                    // Get diagnosis if present
                    if (parts.length > 1 && !parts[1].trim().isEmpty()) {
                        diagnosis = parts[1].trim();
                    }

                    // Get teeth numbers if present
                    if (parts.length > 2 && !parts[2].trim().isEmpty()) {
                        teethNumbers = parts[2].trim();
                    }

                    // Get procedure code if present
                    if (parts.length > 3 && !parts[3].trim().isEmpty()) {
                        // Add 'D' prefix if not present
                        procedureCode = parts[3].trim();
                        if (!procedureCode.startsWith("D")) {
                            procedureCode = "D" + procedureCode;
                        }
                    }

                    // Handle old format files (priority,procedureCode,teethNumbers,diagnosis)
                    // Check if we have a valid procedure code in the second position
                    if (diagnosis.startsWith("D") && (procedureCode.isEmpty() || !procedureCode.startsWith("D"))) {
                        // This is likely the old format
                        procedureCode = diagnosis;
                        diagnosis = parts.length > 3 ? parts[3].trim() : "";
                        teethNumbers = parts.length > 2 ? parts[2].trim() : "";
                    }

                    // Always fetch description from the database
                    String description = "";
                    try {
                        if (!procedureCode.isEmpty()) {
                            description = RiverGreenDB.getProcedureCodeDescription(procedureCode);
                        }
                    } catch (SQLException e) {
                        // If there's an error, use an empty description
                        LOGGER.log(Level.SEVERE, "Unexpected error", e);
                    }

                    RulesetItem item = new RulesetItem(priority, procedureCode, description, teethNumbers);
                    if (!diagnosis.isEmpty()) {
                        item.setDiagnosis(diagnosis);
                    }
                    items.add(item);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unexpected error", e);
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
        File file = FileUtils.getRulesetFile(rulesetName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int i = 1; i < rulesetItems.size(); i++) {
                RulesetItem item = rulesetItems.get(i);
                // Write priority, diagnosis, teeth, codes in the new format
                // Use comma as delimiter
                StringBuilder line = new StringBuilder();
                line.append(item.getPriority()).append(",");

                // Add diagnosis if present
                String diagnosis = item.getDiagnosis();
                if (diagnosis != null && !diagnosis.isEmpty()) {
                    line.append(diagnosis);
                }
                line.append(",");

                // Add teeth numbers if present
                String teethNumbers = item.getTeethNumbers();
                if (teethNumbers != null && !teethNumbers.isEmpty()) {
                    // Format teeth numbers with ranges using - and ; as delimiters
                    String formattedTeethNumbers = formatTeethNumbers(teethNumbers);
                    line.append(formattedTeethNumbers);
                }
                line.append(",");

                // Add procedure code (without 'D' prefix)
                String procedureCode = item.getProcedureCode();
                if (procedureCode != null && !procedureCode.isEmpty()) {
                    // Remove 'D' prefix if present
                    if (procedureCode.startsWith("D")) {
                        procedureCode = procedureCode.substring(1);
                    }
                    line.append(procedureCode);
                }

                writer.write(line.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unexpected error", e);
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
     * Formats teeth numbers to use ranges with '-' and ';' as delimiters.
     * Example: {@code "1,2,3,4,6,7,8"} becomes {@code "1-4;6-8"}.
     *
     * @param teethNumbers The teeth numbers entered by the user (may contain commas or ranges)
     * @return The formatted teeth numbers using ranges with '-' and ';' as delimiters
     */
    private String formatTeethNumbers(String teethNumbers) {
        if (teethNumbers == null || teethNumbers.isEmpty()) {
            return "";
        }

        List<Integer> numbers = TeethNotationUtil.expandTeeth(teethNumbers);
        if (numbers.isEmpty()) {
            return teethNumbers.replace(",", ";");
        }

        // Sort the numbers
        Collections.sort(numbers);

        // Build the formatted string with ranges
        StringBuilder result = new StringBuilder();
        if (!numbers.isEmpty()) {
            int start = numbers.get(0);
            int prev = start;

            for (int i = 1; i < numbers.size(); i++) {
                int current = numbers.get(i);

                // If not consecutive, end the current range and start a new one
                if (current != prev + 1) {
                    // Add the completed range
                    if (start == prev) {
                        result.append(start);
                    } else {
                        result.append(start).append("-").append(prev);
                    }

                    // Start a new range
                    result.append(";");
                    start = current;
                }

                prev = current;
            }

            // Add the last range
            if (start == prev) {
                result.append(start);
            } else {
                result.append(start).append("-").append(prev);
            }
        }

        return result.toString();
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
            LOGGER.log(Level.SEVERE, "Unexpected error", e);
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
