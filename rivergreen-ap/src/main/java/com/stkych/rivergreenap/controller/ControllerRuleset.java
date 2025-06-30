package com.stkych.rivergreenap.controller;

import com.stkych.rivergreenap.RiverGreenDB;
import com.stkych.rivergreenap.SceneSwitcher;
import com.stkych.rivergreenap.model.RulesetItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
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

        // Create the priority dropdown and procedure code text field
        ComboBox<String> priorityComboBox = new ComboBox<>(getAllPriorities());
        priorityComboBox.setPromptText("Select Priority");
        priorityComboBox.setEditable(true); // Allow custom values
        TextField procedureCodeField = new TextField();
        procedureCodeField.setPromptText("Procedure Code");

        // Create the grid for the dialog
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.add(new Label("Priority:"), 0, 0);
        grid.add(priorityComboBox, 1, 0);
        grid.add(new Label("Procedure Code:"), 0, 1);
        grid.add(procedureCodeField, 1, 1);
        dialog.getDialogPane().setContent(grid);

        // Convert the result to a RulesetItem when the OK button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                String priority = priorityComboBox.getValue();
                if (priority == null || priority.isEmpty()) {
                    priority = priorityComboBox.getEditor().getText();
                }
                return new RulesetItem(priority, procedureCodeField.getText());
            }
            return null;
        });

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

        // Create the priority dropdown and procedure code text field
        ComboBox<String> priorityComboBox = new ComboBox<>(getAllPriorities());
        priorityComboBox.setPromptText("Select Priority");
        priorityComboBox.setEditable(true); // Allow custom values
        priorityComboBox.setValue(selectedItem.getPriority()); // Set the current value
        TextField procedureCodeField = new TextField(selectedItem.getProcedureCode());

        // Create the grid for the dialog
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.add(new Label("Priority:"), 0, 0);
        grid.add(priorityComboBox, 1, 0);
        grid.add(new Label("Procedure Code:"), 0, 1);
        grid.add(procedureCodeField, 1, 1);
        dialog.getDialogPane().setContent(grid);

        // Convert the result to a RulesetItem when the OK button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                String priority = priorityComboBox.getValue();
                if (priority == null || priority.isEmpty()) {
                    priority = priorityComboBox.getEditor().getText();
                }
                return new RulesetItem(priority, procedureCodeField.getText());
            }
            return null;
        });

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
            SceneSwitcher.switchScene("news", "RiverGreen AP");
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
            SceneSwitcher.switchScene("news", "RiverGreen AP");
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                writer.write(item.getPriority() + "," + item.getProcedureCode());
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
        rulesetItems.add(new RulesetItem("Header", "Header"));
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
}
