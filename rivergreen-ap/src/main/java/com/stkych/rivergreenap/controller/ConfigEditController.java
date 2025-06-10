package com.stkych.rivergreenap.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import com.stkych.rivergreenap.SceneSwitcher;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the configuration edit scene of the application.
 * Handles user interactions and navigation from the configuration edit screen.
 */
public class ConfigEditController extends Controller {

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
     * Populates the form with the configuration data.
     */
    private void populateForm() {
        // In a real application, you would cast configToEdit to the appropriate type
        // and populate the form fields with its data
        nameLabel.setText("Edit Configuration");

        // Example: if configToEdit was a Configuration object
        // codeTextField.setText(configToEdit.getCode());
        // priorityTextField.setText(String.valueOf(configToEdit.getPriority()));
        // diagTextField.setText(configToEdit.getDiagnosis());

        // Set the selected teeth
        // for (int toothNumber : configToEdit.getSelectedTeeth()) {
        //     selectTooth(toothNumber);
        // }
    }

    /**
     * Disables all form fields.
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
        tooth1.setDisable(true); tooth2.setDisable(true); tooth3.setDisable(true); tooth4.setDisable(true);
        tooth5.setDisable(true); tooth6.setDisable(true); tooth7.setDisable(true); tooth8.setDisable(true);
        tooth9.setDisable(true); tooth10.setDisable(true); tooth11.setDisable(true); tooth12.setDisable(true);
        tooth13.setDisable(true); tooth14.setDisable(true); tooth15.setDisable(true); tooth16.setDisable(true);
        tooth17.setDisable(true); tooth18.setDisable(true); tooth19.setDisable(true); tooth20.setDisable(true);
        tooth21.setDisable(true); tooth22.setDisable(true); tooth23.setDisable(true); tooth24.setDisable(true);
        tooth25.setDisable(true); tooth26.setDisable(true); tooth27.setDisable(true); tooth28.setDisable(true);
        tooth29.setDisable(true); tooth30.setDisable(true); tooth31.setDisable(true); tooth32.setDisable(true);
    }

    /**
     * Selects a tooth by its number.
     * 
     * @param toothNumber The number of the tooth to select (1-32)
     */
    private void selectTooth(int toothNumber) {
        switch (toothNumber) {
            case 1: tooth1.setSelected(true); break;
            case 2: tooth2.setSelected(true); break;
            case 3: tooth3.setSelected(true); break;
            case 4: tooth4.setSelected(true); break;
            case 5: tooth5.setSelected(true); break;
            case 6: tooth6.setSelected(true); break;
            case 7: tooth7.setSelected(true); break;
            case 8: tooth8.setSelected(true); break;
            case 9: tooth9.setSelected(true); break;
            case 10: tooth10.setSelected(true); break;
            case 11: tooth11.setSelected(true); break;
            case 12: tooth12.setSelected(true); break;
            case 13: tooth13.setSelected(true); break;
            case 14: tooth14.setSelected(true); break;
            case 15: tooth15.setSelected(true); break;
            case 16: tooth16.setSelected(true); break;
            case 17: tooth17.setSelected(true); break;
            case 18: tooth18.setSelected(true); break;
            case 19: tooth19.setSelected(true); break;
            case 20: tooth20.setSelected(true); break;
            case 21: tooth21.setSelected(true); break;
            case 22: tooth22.setSelected(true); break;
            case 23: tooth23.setSelected(true); break;
            case 24: tooth24.setSelected(true); break;
            case 25: tooth25.setSelected(true); break;
            case 26: tooth26.setSelected(true); break;
            case 27: tooth27.setSelected(true); break;
            case 28: tooth28.setSelected(true); break;
            case 29: tooth29.setSelected(true); break;
            case 30: tooth30.setSelected(true); break;
            case 31: tooth31.setSelected(true); break;
            case 32: tooth32.setSelected(true); break;
        }
    }

    /**
     * Handles the confirm button click event.
     * Saves the changes to the configuration and closes the popup window.
     */
    @FXML
    private void handleConfirmButtonAction() {
        if (configToEdit != null) {
            // In a real application, you would update the configuration with the form data
            // configToEdit.setCode(codeTextField.getText());
            // configToEdit.setPriority(Integer.parseInt(priorityTextField.getText()));
            // configToEdit.setDiagnosis(diagTextField.getText());

            // Update the selected teeth
            // configToEdit.clearSelectedTeeth();
            // if (tooth1.isSelected()) configToEdit.addSelectedTooth(1);
            // if (tooth2.isSelected()) configToEdit.addSelectedTooth(2);
            // ... and so on for all teeth

            // Save the changes to the data store

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
