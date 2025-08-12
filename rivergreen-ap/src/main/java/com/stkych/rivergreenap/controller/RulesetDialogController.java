package com.stkych.rivergreenap.controller;

import com.stkych.rivergreenap.RiverGreenDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
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
    private Button okButton;

    @FXML
    private TextField teethTextField;

    @FXML
    private TextField codesTextField;

    private String selectedProcedureCode;
    private String selectedPriority;
    private String selectedDiagnosis;
    private String procedureDescription;

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

        // Add listener to codes text field
        codesTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                // Add 'D' prefix if not present
                String code = newValue;
                if (!code.startsWith("D")) {
                    code = "D" + code;
                }
                // Update the procedure code combo box
                procedureCodeComboBox.setValue(code);
            }
        });
    }

    /**
     * Updates the description with the description of the selected procedure code.
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
     * If the codes text field is not empty, returns its value with 'D' prefix.
     * Otherwise, returns the selected procedure code from the combo box.
     *
     * @return The procedure code as a string
     */
    public String getProcedureCode() {
        // If the codes text field is not empty, use its value
        if (codesTextField != null && !codesTextField.getText().isEmpty()) {
            String code = codesTextField.getText();
            // Add 'D' prefix if not present
            if (!code.startsWith("D")) {
                code = "D" + code;
            }
            return code;
        }
        // Otherwise, use the selected procedure code from the combo box
        return selectedProcedureCode;
    }

    /**
     * Gets the priority as a string.
     * If the priority combo box has a value that's not in the list, returns that value.
     * Otherwise, returns the selected priority.
     *
     * @return The priority as a string
     */
    public String getPriority() {
        // Check if the combo box has a value that's not in the list
        if (priorityComboBox != null && priorityComboBox.getEditor() != null) {
            String editorText = priorityComboBox.getEditor().getText();
            if (editorText != null && !editorText.isEmpty() && 
                (selectedPriority == null || !editorText.equals(selectedPriority))) {
                return editorText;
            }
        }
        return selectedPriority;
    }

    /**
     * Gets the diagnosis as a string.
     * If the diagnosis combo box has a value that's not in the list, returns that value.
     * Otherwise, returns the selected diagnosis.
     *
     * @return The diagnosis as a string
     */
    public String getDiagnosis() {
        // Check if the combo box has a value that's not in the list
        if (diagnosisComboBox != null && diagnosisComboBox.getEditor() != null) {
            String editorText = diagnosisComboBox.getEditor().getText();
            if (editorText != null && !editorText.isEmpty() && 
                (selectedDiagnosis == null || !editorText.equals(selectedDiagnosis))) {
                return editorText;
            }
        }
        return selectedDiagnosis;
    }

    /**
     * Gets the selected teeth as a string from the teeth text field.
     *
     * @return The selected teeth as a string
     */
    public String getSelectedTeethAsString() {
        if (teethTextField != null && !teethTextField.getText().isEmpty()) {
            return teethTextField.getText();
        }
        return "";
    }

    /**
     * Gets the procedure description.
     *
     * @return The procedure description
     */
    public String getDescription() {
        return procedureDescription;
    }

    /**
     * Sets the description text.
     *
     * @param description The description text
     */
    public void setDescription(String description) {
        this.procedureDescription = description;
    }

    /**
     * Sets the selected teeth from a string representation.
     * The string format can be tooth numbers separated by hyphens, commas, or semicolons,
     * e.g., "1-2-3-4" or "1, 2, 3, 4" or "1-3;4;5;6-10".
     *
     * @param teethString The string representation of selected teeth
     */
    public void setSelectedTeethFromString(String teethString) {
        // If the string is empty or null, return
        if (teethString == null || teethString.isEmpty()) {
            return;
        }

        // Update the teeth text field
        if (teethTextField != null) {
            teethTextField.setText(teethString);
        }
    }
}