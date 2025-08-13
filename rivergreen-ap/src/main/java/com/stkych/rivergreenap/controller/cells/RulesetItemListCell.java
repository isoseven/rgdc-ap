package com.stkych.rivergreenap.controller.cells;

import com.stkych.rivergreenap.model.RulesetItem;
import com.stkych.rivergreenap.util.DentalCodeUtil;
import com.stkych.rivergreenap.util.TeethNotationUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Custom ListCell implementation for displaying RulesetItem objects
 * using the ruleset_column.fxml layout.
 */
public class RulesetItemListCell extends ListCell<RulesetItem> {

    private static final Logger LOGGER = Logger.getLogger(RulesetItemListCell.class.getName());

    private GridPane gridPane;
    private Label priorityLabel;
    private Label procedureCodeLabel;
    private Label teethLabel;
    private Label diagnosisLabel;
    private Label descriptionLabel;

    /**
     * Constructs a new RulesetItemListCell.
     * Loads the ruleset_column.fxml layout and initializes the labels.
     */
    public RulesetItemListCell() {
        try {
            // Load the ruleset_column.fxml layout
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/stkych/rivergreenap/ruleset_column.fxml"));
            gridPane = loader.load();

            // Get references to the labels in the layout
            priorityLabel = (Label) gridPane.lookup("#priorityLabel");
            procedureCodeLabel = (Label) gridPane.lookup("#procedureCodeLabel");
            teethLabel = (Label) gridPane.lookup("#teethLabel");
            diagnosisLabel = (Label) gridPane.lookup("#diagnosisLabel");
            descriptionLabel = (Label) gridPane.lookup("#descriptionLabel");

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load ruleset column FXML", e);
        }
    }

    /**
     * Updates the item display when the item changes.
     * 
     * @param item The RulesetItem to display
     * @param empty Whether the cell is empty
     */
    @Override
    protected void updateItem(RulesetItem item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            // Set the text of each label to the corresponding property of the item
            priorityLabel.setText(item.getPriority());

            // Use the procedure codes property and display in a compact form with ranges
            String procedureCodes = item.getProcedureCodes();
            // Compress the codes for display
            String displayCodes = DentalCodeUtil.compressDentalCodes(procedureCodes);
            procedureCodeLabel.setText(displayCodes);

            // Use the teeth numbers property without shorthand
            String teethNumbers = item.getTeethNumbers();
            teethLabel.setText(teethNumbers);

            // Set the diagnosis and description
            String description = item.getDescription();
            diagnosisLabel.setText(item.getDiagnosis()); // Use the actual diagnosis
            descriptionLabel.setText(description);

            // Set the graphic to the gridPane
            setGraphic(gridPane);
        }
    }
}
