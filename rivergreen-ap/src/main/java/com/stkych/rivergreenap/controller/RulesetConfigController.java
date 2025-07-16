package com.stkych.rivergreenap.controller;

import com.stkych.rivergreenap.SceneSwitcher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * Controller for the ruleset configuration window.
 * Handles the UI interactions and implements the business logic for managing rulesets.
 */
public class RulesetConfigController implements Initializable {

    @FXML
    private ListView<String> rulesetListView;


    @FXML
    private Button addButton;

    @FXML
    private Button renameButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button closeButton;

    private ObservableList<String> rulesets = FXCollections.observableArrayList();

    /**
     * Initializes the controller.
     * Sets up the UI components and loads the initial data.
     *
     * @param location The location used to resolve relative paths for the root object
     * @param resources The resources used to localize the root object
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up the ListView with the rulesets
        rulesetListView.setItems(rulesets);

        // Load existing rulesets
        loadRulesets();

        // No selection listener needed
    }

    /**
     * Loads existing rulesets from the filesystem.
     * Looks for files with the pattern "ruleset*.csv" and extracts the ruleset names.
     * No longer creates default rulesets if none are found.
     */
    private void loadRulesets() {
        rulesets.clear();

        // Look for ruleset files
        File currentDir = new File(".");
        File[] files = currentDir.listFiles((dir, name) -> name.startsWith("ruleset") && name.endsWith(".csv"));

        if (files != null) {
            for (File file : files) {
                String filename = file.getName();
                // Extract the ruleset name (between "ruleset" and ".csv")
                String rulesetName = filename.substring(7, filename.length() - 4);

                // Add to the list
                rulesets.add(rulesetName);
            }
        }

        // No longer creating default rulesets if none are found
    }

    /**
     * Handles the Add button action.
     * Opens a dialog to enter a new ruleset name and creates a new ruleset with the specified name.
     */
    @FXML
    private void handleAddButtonAction() {
        try {
            // Show the ruleset name dialog
            RulesetNameDialogController controller = (RulesetNameDialogController) SceneSwitcher.showPopup("ruleset_name_dialog", "Add Ruleset");
            controller.setTitle("Add New Ruleset");

            // Wait for the dialog to close
            Stage popupStage = (Stage) controller.getOkButton().getScene().getWindow();
            popupStage.setOnHidden(event -> {
                if (controller.isOkClicked()) {
                    String rulesetName = controller.getName();

                    if (rulesetName.isEmpty()) {
                        showAlert("Error", "Ruleset name cannot be empty.");
                        return;
                    }

                    if (rulesets.contains(rulesetName)) {
                        showAlert("Error", "A ruleset with this name already exists.");
                        return;
                    }

                    // Create a new empty ruleset file
                    createEmptyRulesetFile(rulesetName);

                    // Add to the list
                    rulesets.add(rulesetName);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open ruleset name dialog: " + e.getMessage());
        }
    }

    /**
     * Creates an empty ruleset file with the specified name.
     * 
     * @param rulesetName The name of the ruleset
     */
    private void createEmptyRulesetFile(String rulesetName) {
        String filename = "ruleset" + rulesetName + ".csv";
        File file = new File(filename);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // The file is created empty
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to create ruleset file: " + e.getMessage());
        }
    }

    /**
     * Handles the Rename button action.
     * Opens a dialog to enter a new ruleset name and renames the selected ruleset.
     */
    @FXML
    private void handleRenameButtonAction() {
        String selectedRuleset = rulesetListView.getSelectionModel().getSelectedItem();

        if (selectedRuleset == null) {
            showAlert("Error", "No ruleset selected.");
            return;
        }

        try {
            // Show the ruleset name dialog
            RulesetNameDialogController controller = (RulesetNameDialogController) SceneSwitcher.showPopup("ruleset_name_dialog", "Rename Ruleset");
            controller.setTitle("Rename Ruleset");
            controller.setName(selectedRuleset);

            // Wait for the dialog to close
            Stage popupStage = (Stage) controller.getOkButton().getScene().getWindow();
            popupStage.setOnHidden(event -> {
                if (controller.isOkClicked()) {
                    String newName = controller.getName();

                    if (newName.isEmpty()) {
                        showAlert("Error", "Ruleset name cannot be empty.");
                        return;
                    }

                    if (rulesets.contains(newName) && !selectedRuleset.equals(newName)) {
                        showAlert("Error", "A ruleset with this name already exists.");
                        return;
                    }

                    // Rename the ruleset file
                    renameRulesetFile(selectedRuleset, newName);

                    // Update the list
                    int index = rulesets.indexOf(selectedRuleset);
                    rulesets.set(index, newName);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open ruleset name dialog: " + e.getMessage());
        }
    }

    /**
     * Renames a ruleset file.
     * 
     * @param oldName The old name of the ruleset
     * @param newName The new name of the ruleset
     */
    private void renameRulesetFile(String oldName, String newName) {
        File oldFile = new File("ruleset" + oldName + ".csv");
        File newFile = new File("ruleset" + newName + ".csv");

        if (oldFile.exists()) {
            if (oldFile.renameTo(newFile)) {
                System.out.println("Ruleset file renamed successfully.");
            } else {
                showAlert("Error", "Failed to rename ruleset file.");
            }
        } else {
            showAlert("Error", "Ruleset file not found.");
        }
    }

    /**
     * Handles the Delete button action.
     * Deletes the selected ruleset.
     */
    @FXML
    private void handleDeleteButtonAction() {
        String selectedRuleset = rulesetListView.getSelectionModel().getSelectedItem();

        if (selectedRuleset == null) {
            showAlert("Error", "No ruleset selected.");
            return;
        }

        // Confirm deletion
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Ruleset");
        alert.setContentText("Are you sure you want to delete the ruleset '" + selectedRuleset + "'?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Delete the ruleset file
            deleteRulesetFile(selectedRuleset);

            // Remove from the list
            rulesets.remove(selectedRuleset);
        }
    }

    /**
     * Deletes a ruleset file.
     * 
     * @param rulesetName The name of the ruleset to delete
     */
    private void deleteRulesetFile(String rulesetName) {
        File file = new File("ruleset" + rulesetName + ".csv");

        if (file.exists()) {
            if (file.delete()) {
                System.out.println("Ruleset file deleted successfully.");
            } else {
                showAlert("Error", "Failed to delete ruleset file.");
            }
        } else {
            showAlert("Error", "Ruleset file not found.");
        }
    }

    /**
     * Handles the Close button action.
     * Closes the window and sets a flag to refresh the ruleset window.
     */
    @FXML
    private void handleCloseButtonAction() {
        // Set a flag in the data cache to indicate that the ruleset window should be refreshed
        SceneSwitcher.putData("refreshRulesetWindow", true);

        // Close the window
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Shows an alert dialog with the specified title and message.
     * 
     * @param title The title of the alert
     * @param message The message to display
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
