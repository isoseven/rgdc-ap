package com.stkych.rivergreenap.controller;

import com.stkych.rivergreenap.RiverGreenDB;
import com.stkych.rivergreenap.util.DentalCodeUtil;
import com.stkych.rivergreenap.util.TeethNotationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controller for the ruleset dialog.
 * Handles the UI interactions for the ruleset dialog.
 */
public class RulesetDialogController implements Initializable {

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

    @FXML
    private TextField descriptionTextField;

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

        // Add listener to priority combo box
        priorityComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.equals("none")) {
                    selectedPriority = "";
                } else {
                    selectedPriority = newValue;
                }
            }
        });

        // Add listener to diagnosis combo box
        diagnosisComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.equals("none")) {
                    selectedDiagnosis = "";
                } else {
                    selectedDiagnosis = newValue;
                }
            }
        });

        // Removed auto-filling listener for codes text field to stop description from auto-filling
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
     * Gets the procedure codes as a comma-separated string from the codes text field.
     * Handles ranges like "3000-3999" by expanding them to individual codes.
     *
     * @return The procedure codes as a comma-separated string
     */
    public String getProcedureCodes() {
        // Get the codes from the codes text field
        if (codesTextField != null && !codesTextField.getText().isEmpty()) {
            String codesText = codesTextField.getText();
            System.out.println("[DEBUG_LOG] Getting procedure codes from text field: '" + codesText + "'");

            // Expand any ranges in the codes
            List<String> expandedCodes = DentalCodeUtil.expandDentalCodes(codesText);
            System.out.println("[DEBUG_LOG] Expanded to " + expandedCodes.size() + " individual codes");

            // Join the expanded codes with semicolons
            String result = String.join(",", expandedCodes); // Keep using commas internally for compatibility
            System.out.println("[DEBUG_LOG] Formatted procedure codes: '" + result + "'");
            return result;
        }
        System.out.println("[DEBUG_LOG] No procedure codes to format (empty text field)");
        return "";
    }

    /**
     * Gets the procedure codes in a display-friendly format.
     * This method expands any ranges in the codes and then compresses them back into a compact representation.
     * Example: "3000-3999" becomes "D3000-D3999" instead of a long list of individual codes.
     *
     * @return The procedure codes in a display-friendly format
     */
    public String getProcedureCodesForDisplay() {
        // Get the codes from the codes text field
        if (codesTextField != null && !codesTextField.getText().isEmpty()) {
            String codesText = codesTextField.getText();

            // Expand any ranges in the codes
            List<String> expandedCodes = DentalCodeUtil.expandDentalCodes(codesText);

            // Compress the expanded codes back into a compact representation
            return DentalCodeUtil.compressDentalCodes(expandedCodes);
        }
        return "";
    }

    /**
     * Gets the procedure code as a string from the codes text field.
     * This method is provided for backward compatibility.
     *
     * @return The first procedure code as a string
     */
    public String getProcedureCode() {
        String procedureCodes = getProcedureCodes();
        if (!procedureCodes.isEmpty()) {
            // Return the first code if there are multiple
            int commaIndex = procedureCodes.indexOf(',');
            if (commaIndex > 0) {
                return procedureCodes.substring(0, commaIndex);
            }
            return procedureCodes;
        }
        return "";
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
     * Gets the procedure description from the description text field.
     *
     * @return The procedure description
     */
    public String getDescription() {
        if (descriptionTextField != null && !descriptionTextField.getText().isEmpty()) {
            return descriptionTextField.getText();
        }
        return procedureDescription != null ? procedureDescription : "";
    }

    /**
     * Sets the description text in the description text field.
     *
     * @param description The description text
     */
    public void setDescription(String description) {
        this.procedureDescription = description;
        if (descriptionTextField != null) {
            descriptionTextField.setText(description);
        }
    }

    /**
     * Sets the selected teeth from a string representation.
     * The string may contain ranges using hyphens and semicolons as delimiters, e.g.,
     * {@code "1-3;5;6-8"}. Ranges are displayed as they are.
     *
     * @param teethString The string representation of selected teeth
     */
    public void setSelectedTeethFromString(String teethString) {
        if (teethString == null || teethString.isEmpty()) {
            return;
        }

        if (teethTextField != null) {
            // Display the teeth ranges as they are
            teethTextField.setText(teethString);
        }
    }

    /**
     * Sets the procedure codes in the codes text field.
     * If the codes start with 'D', they will be removed before setting the text.
     * Compresses the codes into ranges for display.
     *
     * @param procedureCodes The procedure codes to set as a comma-separated list
     */
    public void setProcedureCodes(String procedureCodes) {
        System.out.println("[DEBUG_LOG] Setting procedure codes: '" + procedureCodes + "'");

        if (procedureCodes == null || procedureCodes.isEmpty()) {
            System.out.println("[DEBUG_LOG] Procedure codes are null or empty, not setting");
            return;
        }

        // First, expand the codes to ensure we have a complete list
        List<String> expandedCodes = DentalCodeUtil.expandDentalCodes(procedureCodes);
        System.out.println("[DEBUG_LOG] Expanded to " + expandedCodes.size() + " individual codes");

        // Then compress them into ranges (e.g., D1000-1999; N2300-2500)
        String compressedCodes = DentalCodeUtil.compressDentalCodes(expandedCodes);
        System.out.println("[DEBUG_LOG] Compressed into ranges: '" + compressedCodes + "'");

        // Update the codes text field directly with letters preserved
        if (codesTextField != null) {
            codesTextField.setText(compressedCodes);
            System.out.println("[DEBUG_LOG] Updated codes text field");
        } else {
            System.out.println("[DEBUG_LOG] Codes text field is null, cannot update");
        }
    }

    /**
     * Sets the procedure code in the codes text field.
     * This method is provided for backward compatibility.
     *
     * @param procedureCode The procedure code to set
     */
    public void setProcedureCode(String procedureCode) {
        setProcedureCodes(procedureCode);
    }

    /**
     * Gets the procedure code combo box.
     * This method is provided for compatibility with code that expects the old interface.
     * It returns a dummy ComboBox that delegates to the new implementation.
     *
     * @return A dummy ComboBox that delegates to the new implementation
     */
    public ComboBox<String> getProcedureCodeComboBox() {
        // Create a dummy ComboBox that delegates to the new implementation
        ComboBox<String> dummyComboBox = new ComboBox<>();

        // Make it editable
        dummyComboBox.setEditable(true);

        // Set up a dummy value property that delegates to the new implementation
        dummyComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                setProcedureCodes(newValue);
            }
        });

        return dummyComboBox;
    }
}
