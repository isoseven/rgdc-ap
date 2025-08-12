package com.stkych.rivergreenap.controller.cells;

import com.stkych.rivergreenap.model.TreatmentPlanProcedure;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Custom ListCell implementation for displaying a header row in the TreatmentPlanProcedure ListView.
 * This cell is non-selectable and immutable, displaying the default column names from main_column.fxml.
 */
public class TreatmentPlanProcedureHeaderCell extends ListCell<TreatmentPlanProcedure> {

    private GridPane gridPane;
    private static final Logger LOGGER = Logger.getLogger(TreatmentPlanProcedureHeaderCell.class.getName());

    /**
     * Constructs a new TreatmentPlanProcedureHeaderCell.
     * Loads the main_column.fxml layout.
     */
    public TreatmentPlanProcedureHeaderCell() {
        try {
            // Load the main_column.fxml layout
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/stkych/rivergreenap/main_column.fxml"));
            gridPane = loader.load();

            // Make the cell non-selectable and visually distinct as a header
            setDisable(true);
            setStyle("-fx-opacity: 1.0; -fx-background-color: #e0e0e0; -fx-font-weight: bold; -fx-border-color: #c0c0c0; -fx-border-width: 0 0 1 0;");

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load main column FXML", e);
        }
    }

    /**
     * Updates the item display.
     * This cell always displays the default column names from main_column.fxml.
     * 
     * @param item The TreatmentPlanProcedure to display (ignored)
     * @param empty Whether the cell is empty (ignored)
     */
    @Override
    protected void updateItem(TreatmentPlanProcedure item, boolean empty) {
        super.updateItem(item, empty);

        // Always display the header, regardless of the item
        setText(null);
        setGraphic(gridPane);
    }
}
