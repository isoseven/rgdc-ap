package com.stkych.rivergreenap.controller;

import com.stkych.rivergreenap.model.TreatmentPlanProcedure;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.text.NumberFormat;

/**
 * Custom ListCell implementation for displaying TreatmentPlanProcedure objects
 * using the sinset.fxml layout.
 */
public class TreatmentPlanProcedureListCell extends ListCell<TreatmentPlanProcedure> {

    private GridPane gridPane;
    private Label priorityLabel;
    private Label toothLabel;
    private Label surfaceLabel;
    private Label codeLabel;
    private Label diagnosisLabel;
    private Label descriptionLabel;
    private Label feeLabel;

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    /**
     * Constructs a new TreatmentPlanProcedureListCell.
     * Loads the sinset.fxml layout and initializes the labels.
     */
    public TreatmentPlanProcedureListCell() {
        try {
            // Load the sinset.fxml layout
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/stkych/rivergreenap/2/sinset.fxml"));
            gridPane = loader.load();

            // Get references to the labels in the layout based on their order in the GridPane
            // The order is determined by the GridPane.columnIndex property in sinset.fxml
            priorityLabel = (Label) gridPane.getChildren().get(0);
            toothLabel = (Label) gridPane.getChildren().get(1);
            surfaceLabel = (Label) gridPane.getChildren().get(2);
            codeLabel = (Label) gridPane.getChildren().get(3);
            diagnosisLabel = (Label) gridPane.getChildren().get(4);
            descriptionLabel = (Label) gridPane.getChildren().get(5);
            feeLabel = (Label) gridPane.getChildren().get(6);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the item display when the item changes.
     * 
     * @param item The TreatmentPlanProcedure to display
     * @param empty Whether the cell is empty
     */
    @Override
    protected void updateItem(TreatmentPlanProcedure item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            // Set the text of each label to the corresponding property of the item
            // The order matches the visual order in sinset.fxml
            priorityLabel.setText(item.getPriority());
            toothLabel.setText(item.getToothNumber());
            surfaceLabel.setText(item.getSurface());
            codeLabel.setText(item.getProcedureCode());
            diagnosisLabel.setText(item.getDiagnosis());
            descriptionLabel.setText(item.getDescription());
            feeLabel.setText(currencyFormat.format(item.getFee()));

            // Set the graphic to the gridPane
            setGraphic(gridPane);
        }
    }
}
