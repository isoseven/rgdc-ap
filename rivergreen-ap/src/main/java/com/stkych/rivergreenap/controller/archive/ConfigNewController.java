package com.stkych.rivergreenap.controller.archive;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the new configuration scene of the application.
 * Handles user interactions and navigation from the new configuration screen.
 */
public class ConfigNewController extends ControllerOld {

    @FXML
    private Button confirmButton;

    @FXML
    private Button backButton;

    @FXML
    private Label nameLabel;

    @FXML
    private TextField codeTextField;

    @FXML
    private TextField priorityTextField;

    @FXML
    private TextField diagTextField;

    @FXML
    private CheckBox teethCheckBox1;

    @FXML
    private CheckBox teethCheckBox2;

    // Tooth checkboxes (32 of them)
    @FXML
    private CheckBox tooth1, tooth2, tooth3, tooth4, tooth5, tooth6, tooth7, tooth8;

    @FXML
    private CheckBox tooth9, tooth10, tooth11, tooth12, tooth13, tooth14, tooth15, tooth16;

    @FXML
    private CheckBox tooth17, tooth18, tooth19, tooth20, tooth21, tooth22, tooth23, tooth24;

    @FXML
    private CheckBox tooth25, tooth26, tooth27, tooth28, tooth29, tooth30, tooth31, tooth32;

    /**
     * Initializes the controller.
     * This method is called automatically after the FXML file has been loaded.
     * 
     * @param location The location used to resolve relative paths for the root object
     * @param resources The resources used to localize the root object
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize the controller
        nameLabel.setText("New Configuration");

        // Clear all form fields
        clearForm();
    }

    /**
     * Clears all form fields.
     */
    private void clearForm() {
        codeTextField.clear();
        priorityTextField.clear();
        diagTextField.clear();
        teethCheckBox1.setSelected(false);
        teethCheckBox2.setSelected(false);

        // Clear all tooth checkboxes
        clearAllTeethCheckboxes();
    }

    /**
     * Clears all tooth checkboxes.
     */
    private void clearAllTeethCheckboxes() {
        tooth1.setSelected(false); tooth2.setSelected(false); tooth3.setSelected(false); tooth4.setSelected(false);
        tooth5.setSelected(false); tooth6.setSelected(false); tooth7.setSelected(false); tooth8.setSelected(false);
        tooth9.setSelected(false); tooth10.setSelected(false); tooth11.setSelected(false); tooth12.setSelected(false);
        tooth13.setSelected(false); tooth14.setSelected(false); tooth15.setSelected(false); tooth16.setSelected(false);
        tooth17.setSelected(false); tooth18.setSelected(false); tooth19.setSelected(false); tooth20.setSelected(false);
        tooth21.setSelected(false); tooth22.setSelected(false); tooth23.setSelected(false); tooth24.setSelected(false);
        tooth25.setSelected(false); tooth26.setSelected(false); tooth27.setSelected(false); tooth28.setSelected(false);
        tooth29.setSelected(false); tooth30.setSelected(false); tooth31.setSelected(false); tooth32.setSelected(false);
    }

    /**
     * Handles the confirm button click event.
     * Creates a new configuration and closes the popup window.
     */
    @FXML
    private void handleConfirmButtonAction() {
        // Validate the form
        if (validateForm()) {
            // Create a new configuration
            createNewConfiguration();

            // Close the popup window
            closePopup();
        }
    }

    /**
     * Validates the form.
     * 
     * @return true if the form is valid, false otherwise
     */
    private boolean validateForm() {
        // Check if code is provided
        if (codeTextField.getText().trim().isEmpty()) {
            System.out.println("Code is required");
            return false;
        }

        // Check if priority is provided and is a number
        try {
            if (priorityTextField.getText().trim().isEmpty()) {
                System.out.println("Priority is required");
                return false;
            }
            Integer.parseInt(priorityTextField.getText().trim());
        } catch (NumberFormatException e) {
            System.out.println("Priority must be a number");
            return false;
        }

        // Check if diagnosis is provided
        if (diagTextField.getText().trim().isEmpty()) {
            System.out.println("Diagnosis is required");
            return false;
        }

        return true;
    }

    /**
     * Creates a new configuration.
     */
    private void createNewConfiguration() {
        // In a real application, you would create a new Configuration object
        // and populate it with the form data
        String code = codeTextField.getText().trim();
        int priority = Integer.parseInt(priorityTextField.getText().trim());
        String diagnosis = diagTextField.getText().trim();

        // Create a new configuration
        // Configuration newConfig = new Configuration(code, priority, diagnosis);

        // Add the selected teeth
        // if (tooth1.isSelected()) newConfig.addSelectedTooth(1);
        // if (tooth2.isSelected()) newConfig.addSelectedTooth(2);
        // ... and so on for all teeth

        // Save the new configuration to the data store

        System.out.println("New configuration created: " + code + ", " + priority + ", " + diagnosis);
    }

    /**
     * Handles the back button click event.
     * Closes the popup window without creating a new configuration.
     */
    @FXML
    private void handleBackButtonAction() {
        closePopup();
    }
}
