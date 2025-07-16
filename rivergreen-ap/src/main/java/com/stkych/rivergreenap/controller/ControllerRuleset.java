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

    @FXML
    private Button cancelButton;

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

        // Set up the ruleset selection menu
        setupRulesetSelectMenu();

        // Set up the button handlers
        setupButtonHandlers();

        // Load the initial ruleset data
        loadRulesets();
        loadRuleset(currentRuleset);

        // Update the window title
        patientNameLabel.setText("Ruleset Configuration");
    }

    /**
     * Sets up the ruleset selection menu.
     * Adds menu items for each ruleset and sets up the event handlers.
     */
    private void setupRulesetSelectMenu() {
        rulesetSelectMenuButton.getItems().clear();

        // Add menu items for the default rulesets
        MenuItem itemA = new MenuItem("Ruleset A");
        MenuItem itemB = new MenuItem("Ruleset B");

        // Set up event handlers for the menu items
        itemA.setOnAction(event -> {
            currentRuleset = "A";
            rulesetSelectMenuButton.setText("Ruleset A");
            loadRuleset(currentRuleset);
        });

        itemB.setOnAction(event -> {
            currentRuleset = "B";
            rulesetSelectMenuButton.setText("Ruleset B");
            loadRuleset(currentRuleset);
        });

        // Add the menu items to the menu button
        rulesetSelectMenuButton.getItems().addAll(itemA, itemB);
        rulesetSelectMenuButton.setText("Ruleset A");
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

        // Cancel button handler
        cancelButton.setOnAction(event -> handleCancelButtonAction());
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
                    return new RulesetItem(priority, procedureCode, description);
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

            // The controller now initializes its own UI components in the initialize method
            // We just need to set the current values

            // Set the current values
            controller.getPriorityComboBox().setValue(selectedItem.getPriority());
            controller.getProcedureCodeComboBox().setValue(selectedItem.getProcedureCode());

            // Parse the teeth from the description if they exist
            String description = selectedItem.getDescription();
            if (description != null && description.contains("(Teeth: ")) {
                int start = description.indexOf("(Teeth: ") + 8;
                int end = description.indexOf(")", start);
                if (end > start) {
                    String teethString = description.substring(start, end);
                    String[] teeth = teethString.split(",");

                    // Select the teeth in the UI
                    for (String tooth : teeth) {
                        for (javafx.scene.Node node : controller.getTeethGridPane().getChildren()) {
                            if (node instanceof CheckBox) {
                                CheckBox checkBox = (CheckBox) node;
                                if (String.valueOf(checkBox.getUserData()).equals(tooth.trim())) {
                                    checkBox.setSelected(true);
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            // Set the content of the dialog
            dialog.getDialogPane().setContent(root);

            // Convert the result to a RulesetItem when the OK button is clicked
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == okButtonType) {
                    String priority = controller.getPriority();
                    String procedureCode = controller.getProcedureCode();
                    String descriptionText = controller.getDescriptionLabel().getText().replace("Description: ", "");

                    // Get the selected teeth as a comma-separated string
                    String teethString = controller.getSelectedTeethAsString();

                    // Create a new RulesetItem with the procedure code and priority
                    // Store the teeth in the description field for now
                    // In a real application, you would want to store this in a separate field
                    String fullDescription = descriptionText;
                    if (!teethString.isEmpty()) {
                        fullDescription += " (Teeth: " + teethString + ")";
                    }

                    return new RulesetItem(priority, procedureCode, fullDescription);
                }
                return null;
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Show the dialog and process the result
        Optional<RulesetItem> result = dialog.showAndWait();
        result.ifPresent(item -> {
            int index = listView.getSelectionModel().getSelectedIndex();
            rulesetItems.set(index, item);
            sortRulesetItems();
            saveRuleset(currentRuleset);
        });
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
     * Handles the cancel button action.
     * Closes the window without saving changes.
     */
    @FXML
    private void handleCancelButtonAction() {
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
     * Creates default rulesets if the files don't exist.
     */
    private void loadRulesets() {
        // Load ruleset A
        List<RulesetItem> rulesetA = loadRulesetFromFile("rulesetA.csv");
        if (rulesetA.isEmpty()) {
            // Create a default ruleset A
            rulesetA.add(new RulesetItem("Header", "Header", "Description"));

            // Try to get descriptions from the database
            String description1 = "";
            String description2 = "";
            String description3 = "";
            try {
                description1 = RiverGreenDB.getProcedureCodeDescription("D2140");
                description2 = RiverGreenDB.getProcedureCodeDescription("D2150");
                description3 = RiverGreenDB.getProcedureCodeDescription("D2160");
            } catch (SQLException e) {
                e.printStackTrace();
            }

            rulesetA.add(new RulesetItem("1", "D2140", description1));
            rulesetA.add(new RulesetItem("2", "D2150", description2));
            rulesetA.add(new RulesetItem("3", "D2160", description3));
        }
        rulesets.put("A", rulesetA);

        // Load ruleset B
        List<RulesetItem> rulesetB = loadRulesetFromFile("rulesetB.csv");
        if (rulesetB.isEmpty()) {
            // Create a default ruleset B
            rulesetB.add(new RulesetItem("Header", "Header", "Description"));

            // Try to get descriptions from the database
            String description1 = "";
            String description2 = "";
            String description3 = "";
            try {
                description1 = RiverGreenDB.getProcedureCodeDescription("D2330");
                description2 = RiverGreenDB.getProcedureCodeDescription("D2331");
                description3 = RiverGreenDB.getProcedureCodeDescription("D2332");
            } catch (SQLException e) {
                e.printStackTrace();
            }

            rulesetB.add(new RulesetItem("1", "D2330", description1));
            rulesetB.add(new RulesetItem("2", "D2331", description2));
            rulesetB.add(new RulesetItem("3", "D2332", description3));
        }
        rulesets.put("B", rulesetB);
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
            items.add(new RulesetItem("Header", "Header", "Description"));
            return items;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Add a header item
            items.add(new RulesetItem("Header", "Header", "Description"));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String priority = parts[0];
                    String procedureCode = parts[1];
                    String description = "";

                    // If the description is in the file, use it
                    if (parts.length >= 3) {
                        description = parts[2];
                    } else {
                        // Otherwise, try to fetch it from the database
                        try {
                            description = RiverGreenDB.getProcedureCodeDescription(procedureCode);
                        } catch (SQLException e) {
                            // If there's an error, use an empty description
                            e.printStackTrace();
                        }
                    }

                    items.add(new RulesetItem(priority, procedureCode, description));
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
        rulesetItems.addAll(rulesets.get(rulesetName));
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
                writer.write(item.getPriority() + "," + item.getProcedureCode() + "," + item.getDescription());
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
        rulesetItems.add(new RulesetItem("Header", "Header", "Description"));
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
