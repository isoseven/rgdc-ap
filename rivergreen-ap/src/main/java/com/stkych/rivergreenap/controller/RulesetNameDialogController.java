package com.stkych.rivergreenap.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the ruleset name dialog.
 * Handles the UI interactions for entering a ruleset name.
 */
public class RulesetNameDialogController implements Initializable {

    @FXML
    private Label titleLabel;

    @FXML
    private TextField nameTextField;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;

    private boolean okClicked = false;
    private String initialName = "";

    /**
     * Initializes the controller.
     * This method is called automatically after the FXML file has been loaded.
     *
     * @param location The location used to resolve relative paths for the root object
     * @param resources The resources used to localize the root object
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize with empty name
    }

    /**
     * Sets the title of the dialog.
     *
     * @param title The title to set
     */
    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    /**
     * Sets the initial name in the text field.
     *
     * @param name The name to set
     */
    public void setName(String name) {
        this.initialName = name;
        nameTextField.setText(name);
    }

    /**
     * Gets the name entered in the text field.
     *
     * @return The name entered
     */
    public String getName() {
        return nameTextField.getText().trim();
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
     * Returns whether the OK button was clicked.
     *
     * @return true if the OK button was clicked, false otherwise
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Handles the OK button action.
     * Sets okClicked to true and closes the dialog.
     */
    @FXML
    private void handleOkButtonAction() {
        okClicked = true;
        closeDialog();
    }

    /**
     * Handles the Cancel button action.
     * Closes the dialog without setting okClicked.
     */
    @FXML
    private void handleCancelButtonAction() {
        closeDialog();
    }

    /**
     * Closes the dialog.
     */
    private void closeDialog() {
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }
}
