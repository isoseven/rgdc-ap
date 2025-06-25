package com.stkych.rivergreenap.controller.archive;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import com.stkych.rivergreenap.SceneSwitcher;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the configuration edit scene of the application.
 * Handles user interactions and navigation from the configuration edit screen.
 */
public class ConfigEditController extends ControllerOld {

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

    @FXML
    private CheckBox tooth1, tooth2, tooth3, tooth4, tooth5, tooth6, tooth7, tooth8;

    @FXML
    private CheckBox tooth9, tooth10, tooth11, tooth12, tooth13, tooth14, tooth15, tooth16;

    @FXML
    private CheckBox tooth17, tooth18, tooth19, tooth20, tooth21, tooth22, tooth23, tooth24;

    @FXML
    private CheckBox tooth25, tooth26, tooth27, tooth28, tooth29, tooth30, tooth31, tooth32;

    private Object configToEdit;

    /**
     * Initializes the controller.
     * This method is called automatically after the FXML file has been loaded.
     * 
     * @param location The location used to resolve relative paths for the root object
     * @param resources The resources used to localize the root object
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Get the configuration to edit from the SceneSwitcher data cache
        configToEdit = SceneSwitcher.getData("selectedConfig");

        if (configToEdit != null) {
            // Populate the form with the configuration data
            populateForm();
        } else {
            // Handle the case where no configuration was passed
            nameLabel.setText("Error: No configuration selected");
            disableForm();
        }
    }

    /**
     * Populates the form with data from the current configuration object being edited.
     * This method sets the initial state of the form fields based on the data stored
     * in the `configToEdit` object. It can include values such as textual information
     * for text fields, selected options for dropdowns, or checked states for checkboxes.
     * <p>
     * This method assumes that the `configToEdit` object has been properly initialized
     * and contains valid data.
     * <p>
     * Note: Placeholder
     */
    private void populateForm() {
        nameLabel.setText("Edit Configuration");
    }

    /**
     * Disables all form fields in SceneConfigEdit.fxml.
     */
    private void disableForm() {
        codeTextField.setDisable(true);
        priorityTextField.setDisable(true);
        diagTextField.setDisable(true);
        teethCheckBox1.setDisable(true);
        teethCheckBox2.setDisable(true);

        // Disable all tooth checkboxes
        disableAllTeethCheckboxes();

        confirmButton.setDisable(true);
    }

    /**
     * Disables all tooth checkboxes.
     */
    private void disableAllTeethCheckboxes() {
        for (int i = 1; i <= 32; i++) {
            try {
                CheckBox tooth = (CheckBox) getClass().getDeclaredField("tooth" + i).get(this);
                tooth.setDisable(true);
            } catch (Exception e) {
                System.out.println("Error disabling tooth checkbox: " + e.getMessage());
            }
        }
    }

    /**
     * Selects a tooth by its number.
     * 
     * @param toothNumber The number of the tooth to select (1-32)
     */
    private void selectTooth(int toothNumber) {
        try {
            CheckBox tooth = (CheckBox) getClass().getDeclaredField("tooth" + toothNumber).get(this);
            tooth.setSelected(true);
        } catch (Exception e) {
            System.out.println("Error selecting tooth: " + e.getMessage());
        }
    }

    /**
     * Handles the confirm button click event.
     * Saves the changes to the configuration and closes the popup window.
     */
    @FXML
    private void handleConfirmButtonAction() {
        if (configToEdit != null) {
            // Placeholder
            System.out.println("Configuration updated");
        }

        // Close the popup window
        closePopup();
    }

    /**
     * Handles the back button click event.
     * Closes the popup window without saving changes.
     */
    @FXML
    private void handleBackButtonAction() {
        closePopup();
    }
}
