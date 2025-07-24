package com.stkych.rivergreenap.controller;

import com.stkych.rivergreenap.RiverGreenDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the ruleset dialog.
 * Handles the UI interactions for the ruleset dialog.
 */
public class RulesetDialogController implements Initializable {

    @FXML
    private ComboBox<String> procedureCodeComboBox;

    @FXML
    private ComboBox<String> priorityComboBox;

    @FXML
    private ComboBox<String> diagnosisComboBox;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Button okButton;

    @FXML
    private GridPane teethGridPane;

    private final List<CheckBox> teethCheckboxes = new ArrayList<>();
    private String selectedProcedureCode;
    private String selectedPriority;
    private String selectedDiagnosis;
    private final List<String> selectedTeeth = new ArrayList<>();

    /**
     * Initializes the controller.
     * This method is called automatically after the FXML file has been loaded.
     *
     * @param location The location used to resolve relative paths for the root object
     * @param resources The resources used to localize the root object
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize procedure code combo box
        try {
            ObservableList<String> procedureCodes = RiverGreenDB.getAllProcedureCodesObservable();
            procedureCodeComboBox.setItems(procedureCodes);
        } catch (SQLException e) {
            showErrorAlert("Error loading procedure codes", e.getMessage());
        }

        // Initialize priority combo box
        try {
            ObservableList<String> priorities = RiverGreenDB.getAllPrioritiesObservable();
            priorityComboBox.setItems(priorities);
        } catch (SQLException e) {
            showErrorAlert("Error loading priorities", e.getMessage());
        }

        // Initialize diagnosis combo box
        try {
            ObservableList<String> diagnoses = RiverGreenDB.getAllDiagnosesObservable();
            diagnosisComboBox.setItems(diagnoses);
        } catch (SQLException e) {
            showErrorAlert("Error loading diagnoses", e.getMessage());
        }

        // Create checkboxes for teeth 1-32
        createTeethCheckboxes();

        // Add listener to procedure code combo box to update description
        procedureCodeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedProcedureCode = newValue;
                updateDescription(newValue);
            }
        });

        // Add listener to priority combo box
        priorityComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.equals("n/a")) {
                    selectedPriority = "";
                } else {
                    selectedPriority = newValue;
                }
            }
        });

        // Add listener to diagnosis combo box
        diagnosisComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.equals("n/a")) {
                    selectedDiagnosis = "";
                } else {
                    selectedDiagnosis = newValue;
                }
            }
        });
    }

    /**
     * Creates checkboxes for teeth 1-32 and adds them to the teethGridPane.
     * Arranges teeth in a 2x16 grid with 1-16 on top and 32-17 on bottom.
     * Adds labels above the checkboxes showing the tooth numbers.
     */
    private void createTeethCheckboxes() {
        teethGridPane.getChildren().clear();
        teethCheckboxes.clear();

        // Add labels for teeth 1-16 (top row)
        for (int i = 1; i <= 16; i++) {
            Label label = new Label(String.valueOf(i));
            label.setAlignment(javafx.geometry.Pos.CENTER);
            teethGridPane.add(label, i - 1, 0); // Add to top row (row 0)
        }

        // Add labels for teeth 32-17 (middle row, in reverse order)
        for (int i = 32; i >= 17; i--) {
            Label label = new Label(String.valueOf(i));
            label.setAlignment(javafx.geometry.Pos.CENTER);
            teethGridPane.add(label, 32 - i, 1); // Add to middle row (row 1)
        }

        // Create checkboxes for teeth 1-16 (bottom row)
        for (int i = 1; i <= 16; i++) {
            CheckBox checkBox = new CheckBox();
            // Remove the text from the checkbox but keep the userData for identification
            checkBox.setText("");
            checkBox.setUserData(i);
            addCheckBoxListener(checkBox);
            teethCheckboxes.add(checkBox);
            teethGridPane.add(checkBox, i - 1, 2); // Add to bottom row (row 2)
        }

        // Create checkboxes for teeth 32-17 (bottom row, in reverse order)
        for (int i = 32; i >= 17; i--) {
            CheckBox checkBox = new CheckBox();
            // Remove the text from the checkbox but keep the userData for identification
            checkBox.setText("");
            checkBox.setUserData(i);
            addCheckBoxListener(checkBox);
            teethCheckboxes.add(checkBox);
            teethGridPane.add(checkBox, 32 - i, 3); // Add to bottom row (row 3)
        }
    }

    /**
     * Adds a listener to a checkbox to update the selectedTeeth list.
     * 
     * @param checkBox The checkbox to add the listener to
     */
    private void addCheckBoxListener(CheckBox checkBox) {
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            // Use userData instead of text for tooth identification
            String toothNumber = String.valueOf(checkBox.getUserData());
            if (newValue) {
                if (!selectedTeeth.contains(toothNumber)) {
                    selectedTeeth.add(toothNumber);
                }
            } else {
                selectedTeeth.remove(toothNumber);
            }
        });
    }

    /**
     * Updates the description label with the description of the selected procedure code.
     *
     * @param procedureCode The procedure code to get the description for
     */
    private void updateDescription(String procedureCode) {
        try {
            String description = RiverGreenDB.getProcedureCodeDescription(procedureCode);
            setDescription("Description: " + description);
        } catch (SQLException e) {
            showErrorAlert("Error loading procedure description", e.getMessage());
        }
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
     * Handles the OK button action.
     * Closes the dialog and returns the selected values.
     */
    @FXML
    private void handleOkButtonAction() {
        // Close the dialog
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Gets the procedure code combo box.
     *
     * @return The procedure code combo box
     */
    public ComboBox<String> getProcedureCodeComboBox() {
        return procedureCodeComboBox;
    }

    /**
     * Gets the priority combo box.
     *
     * @return The priority combo box
     */
    public ComboBox<String> getPriorityComboBox() {
        return priorityComboBox;
    }

    /**
     * Gets the diagnosis combo box.
     *
     * @return The diagnosis combo box
     */
    public ComboBox<String> getDiagnosisComboBox() {
        return diagnosisComboBox;
    }

    /**
     * Gets the OK button.
     *
     * @return The OK button
     */
    public Button getOkButton() {
        return okButton;
    }

    /**
     * Gets the procedure code as a string.
     *
     * @return The procedure code as a string
     */
    public String getProcedureCode() {
        return selectedProcedureCode;
    }

    /**
     * Gets the priority as a string.
     *
     * @return The priority as a string
     */
    public String getPriority() {
        return selectedPriority;
    }

    /**
     * Gets the diagnosis as a string.
     *
     * @return The diagnosis as a string
     */
    public String getDiagnosis() {
        return selectedDiagnosis;
    }

    /**
     * Gets the selected teeth as a list of strings.
     *
     * @return The selected teeth as a list of strings
     */
    public List<String> getSelectedTeeth() {
        return new ArrayList<>(selectedTeeth);
    }

    /**
     * Gets the selected teeth as a hyphen-separated string.
     *
     * @return The selected teeth as a hyphen-separated string
     */
    public String getSelectedTeethAsString() {
        return String.join("-", selectedTeeth);
    }

    /**
     * Gets the description label.
     *
     * @return The description label
     */
    public Label getDescriptionLabel() {
        return descriptionLabel;
    }

    /**
     * Gets the teeth grid pane.
     *
     * @return The teeth grid pane
     */
    public GridPane getTeethGridPane() {
        return teethGridPane;
    }

    /**
     * Sets the description text.
     *
     * @param description The description text
     */
    public void setDescription(String description) {
        descriptionLabel.setText(description);
    }

    /**
     * Sets the selected teeth from a string representation.
     * The string format can be tooth numbers separated by hyphens or commas, e.g., "1-2-3-4" or "1, 2, 3, 4".
     *
     * @param teethString The string representation of selected teeth
     */
    public void setSelectedTeethFromString(String teethString) {
        // Clear current selections
        for (CheckBox checkBox : teethCheckboxes) {
            checkBox.setSelected(false);
        }
        selectedTeeth.clear();

        // If the string is empty or null, return
        if (teethString == null || teethString.isEmpty()) {
            return;
        }

        // Split the string by hyphens or commas
        String[] teethArray;
        if (teethString.contains(",")) {
            teethArray = teethString.split(",");
        } else {
            teethArray = teethString.split("-");
        }

        // Select the checkboxes for the specified teeth
        for (String toothStr : teethArray) {
            try {
                int toothNumber = Integer.parseInt(toothStr.trim());
                // Find the checkbox with this tooth number as userData
                for (CheckBox checkBox : teethCheckboxes) {
                    if (checkBox.getUserData().equals(toothNumber)) {
                        checkBox.setSelected(true);
                        // The listener will add the tooth to selectedTeeth
                        break;
                    }
                }
            } catch (NumberFormatException e) {
                // Skip non-numeric values
                System.out.println("Skipping non-numeric tooth value: " + toothStr);
            }
        }
    }
}
