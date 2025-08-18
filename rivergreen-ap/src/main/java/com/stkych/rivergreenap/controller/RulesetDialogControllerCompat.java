package com.stkych.rivergreenap.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ComboBox;

/**
 * Compatibility class for RulesetDialogController.
 * Provides the same interface as the old RulesetDialogController but uses the new implementation.
 * This class is used to maintain compatibility with code that expects the old interface.
 */
public class RulesetDialogControllerCompat {
    private final RulesetDialogController controller;

    /**
     * Constructs a new RulesetDialogControllerCompat with the specified controller.
     *
     * @param controller The RulesetDialogController to wrap
     */
    public RulesetDialogControllerCompat(RulesetDialogController controller) {
        this.controller = controller;
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
        StringProperty dummyValue = new SimpleStringProperty();
        dummyValue.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                controller.setProcedureCode(newValue);
            }
        });
        
        // Override the getValue method to delegate to the new implementation
        dummyComboBox.valueProperty().bindBidirectional(dummyValue);
        
        return dummyComboBox;
    }

    /**
     * Gets the priority combo box.
     *
     * @return The priority combo box
     */
    public ComboBox<String> getPriorityComboBox() {
        return controller.getPriorityComboBox();
    }

    /**
     * Gets the diagnosis combo box.
     *
     * @return The diagnosis combo box
     */
    public ComboBox<String> getDiagnosisComboBox() {
        return controller.getDiagnosisComboBox();
    }

    /**
     * Gets the procedure code as a string.
     *
     * @return The procedure code as a string
     */
    public String getProcedureCode() {
        return controller.getProcedureCode();
    }

    /**
     * Gets the priority as a string.
     *
     * @return The priority as a string
     */
    public String getPriority() {
        return controller.getPriority();
    }

    /**
     * Gets the diagnosis as a string.
     *
     * @return The diagnosis as a string
     */
    public String getDiagnosis() {
        return controller.getDiagnosis();
    }

    /**
     * Gets the selected teeth as a string.
     *
     * @return The selected teeth as a string
     */
    public String getSelectedTeethAsString() {
        return controller.getSelectedTeethAsString();
    }

    /**
     * Gets the procedure description.
     *
     * @return The procedure description
     */
    public String getDescription() {
        return controller.getDescription();
    }

    /**
     * Sets the description text.
     *
     * @param description The description text
     */
    public void setDescription(String description) {
        controller.setDescription(description);
    }

    /**
     * Sets the selected teeth from a string representation.
     *
     * @param teethString The string representation of selected teeth
     */
    public void setSelectedTeethFromString(String teethString) {
        controller.setSelectedTeethFromString(teethString);
    }
}