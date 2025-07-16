package com.stkych.rivergreenap.controller.cells;

import com.stkych.rivergreenap.model.RulesetItem;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;

import java.io.IOException;

/**
 * Custom ListCell implementation for displaying RulesetItem objects
 * using the ruleset_column.fxml layout.
 */
public class RulesetItemListCell extends ListCell<RulesetItem> {

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
            e.printStackTrace();
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
            procedureCodeLabel.setText(item.getProcedureCode());

            // Extract teeth and diagnosis information from the description
            String description = item.getDescription();
            String teeth = "";
            String diagnosis = description;

            // If the description contains teeth information, extract it
            if (description != null && description.contains("(Teeth: ")) {
                int start = description.indexOf("(Teeth: ") + 8;
                int end = description.indexOf(")", start);
                if (end > start) {
                    teeth = description.substring(start, end);
                    // The diagnosis is the text before the teeth information
                    diagnosis = description.substring(0, description.indexOf("(Teeth: ")).trim();
                }
            }

            teethLabel.setText(teeth);
            diagnosisLabel.setText(diagnosis);
            descriptionLabel.setText(description);

            // Set the graphic to the gridPane
            setGraphic(gridPane);
        }
    }
}
