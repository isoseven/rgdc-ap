package com.stkych.rivergreenap.controller;

import com.stkych.rivergreenap.model.RulesetItem;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;

import java.io.IOException;

/**
 * Custom ListCell implementation for displaying RulesetItem objects
 * using the ruleset_item.fxml layout.
 */
public class RulesetItemListCell extends ListCell<RulesetItem> {

    private GridPane gridPane;
    private Label priorityLabel;
    private Label procedureCodeLabel;

    /**
     * Constructs a new RulesetItemListCell.
     * Loads the ruleset_item.fxml layout and initializes the labels.
     */
    public RulesetItemListCell() {
        try {
            // Load the ruleset_item.fxml layout
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/stkych/rivergreenap/2/ruleset_item.fxml"));
            gridPane = loader.load();

            // Get references to the labels in the layout
            priorityLabel = (Label) gridPane.lookup("#priorityLabel");
            procedureCodeLabel = (Label) gridPane.lookup("#procedureCodeLabel");

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

            // Set the graphic to the gridPane
            setGraphic(gridPane);
        }
    }
}