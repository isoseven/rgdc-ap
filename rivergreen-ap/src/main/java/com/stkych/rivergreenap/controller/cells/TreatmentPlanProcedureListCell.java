package com.stkych.rivergreenap.controller.cells;

import com.stkych.rivergreenap.model.TreatmentPlanProcedure;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Custom ListCell implementation for displaying TreatmentPlanProcedure objects
 * using the main_column.fxml layout.
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

    private static final Logger LOGGER = Logger.getLogger(TreatmentPlanProcedureListCell.class.getName());

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    /**
     * Constructs a new TreatmentPlanProcedureListCell.
     * Loads the main_column.fxml layout and initializes the labels.
     */
    public TreatmentPlanProcedureListCell() {
        try {
            // Load the main_column.fxml layout
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/stkych/rivergreenap/main_column.fxml"));
            gridPane = loader.load();

            // Get references to the labels in the layout based on their order in the GridPane
            // The order is determined by the GridPane.columnIndex property in main_column.fxml
            priorityLabel = (Label) gridPane.getChildren().get(0);
            toothLabel = (Label) gridPane.getChildren().get(1);
            surfaceLabel = (Label) gridPane.getChildren().get(2);
            codeLabel = (Label) gridPane.getChildren().get(3);
            diagnosisLabel = (Label) gridPane.getChildren().get(4);
            descriptionLabel = (Label) gridPane.getChildren().get(5);
            feeLabel = (Label) gridPane.getChildren().get(6);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load main column FXML", e);
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
            // The order matches the visual order in main_column.fxml
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
