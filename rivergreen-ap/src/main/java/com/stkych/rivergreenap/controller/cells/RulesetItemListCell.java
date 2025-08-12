package com.stkych.rivergreenap.controller.cells;

import com.stkych.rivergreenap.model.RulesetItem;
import com.stkych.rivergreenap.util.TeethNotationUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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

            // Display teeth numbers without shorthand.
            // Convert ranges (e.g. "1-3;5") to their comma form and append
            // the original range representation in parentheses when applicable.
            String teethNumbers = item.getTeethNumbers();
            List<Integer> expanded = TeethNotationUtil.expandTeeth(teethNumbers);
            String commaForm = expanded.isEmpty()
                    ? teethNumbers.replace(";", ",")
                    : expanded.stream().map(String::valueOf).collect(Collectors.joining(","));
            String teethDisplay = commaForm;
            if (!expanded.isEmpty() && !teethNumbers.replace(";", ",").equals(commaForm)) {
                teethDisplay = commaForm + " (" + teethNumbers.replace(";", ",") + ")";
            }
            teethLabel.setText(teethDisplay);

            // Display procedure code and description
            procedureCodeLabel.setText(item.getProcedureCode().replace(";", ","));
            String description = item.getDescription();
            diagnosisLabel.setText(item.getDiagnosis());
            descriptionLabel.setText(description);

            // Set the graphic to the gridPane
            setGraphic(gridPane);
        }
    }
}
