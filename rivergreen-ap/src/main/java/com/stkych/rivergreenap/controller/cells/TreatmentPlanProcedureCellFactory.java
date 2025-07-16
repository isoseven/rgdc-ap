package com.stkych.rivergreenap.controller.cells;

import com.stkych.rivergreenap.model.TreatmentPlanProcedure;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * Factory class for creating TreatmentPlanProcedureListCell instances.
 * This class implements the Callback interface to be used as a cell factory for ListView.
 * It creates a header cell for the first row and regular cells for all other rows.
 */
public class TreatmentPlanProcedureCellFactory implements Callback<ListView<TreatmentPlanProcedure>, ListCell<TreatmentPlanProcedure>> {

    /**
     * Creates a new ListCell for the given ListView.
     * Returns a TreatmentPlanProcedureHeaderCell for the first cell (index 0)
     * and a TreatmentPlanProcedureListCell for all other cells.
     * 
     * @param listView The ListView that will use the cell
     * @return A new ListCell appropriate for the given index
     */
    @Override
    public ListCell<TreatmentPlanProcedure> call(ListView<TreatmentPlanProcedure> listView) {
        return new ListCell<TreatmentPlanProcedure>() {
            private final TreatmentPlanProcedureHeaderCell headerCell = new TreatmentPlanProcedureHeaderCell();
            private final TreatmentPlanProcedureListCell listCell = new TreatmentPlanProcedureListCell();

            @Override
            public void updateIndex(int index) {
                super.updateIndex(index);

                if (isEmpty()) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                if (index == 0) {
                    // Use the header cell for the first row
                    headerCell.updateIndex(index);
                    headerCell.updateItem(getItem(), isEmpty());
                    setGraphic(headerCell.getGraphic());
                } else {
                    // Use the regular cell for all other rows
                    listCell.updateIndex(index);
                    listCell.updateItem(getItem(), isEmpty());
                    setGraphic(listCell.getGraphic());
                }
            }

            @Override
            protected void updateItem(TreatmentPlanProcedure item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                if (getIndex() == 0) {
                    // Use the header cell for the first row
                    headerCell.updateItem(item, false);
                    setGraphic(headerCell.getGraphic());
                    // Make the header non-selectable
                    setDisable(true);
                    setStyle("-fx-opacity: 1.0; -fx-background-color: #f0f0f0;");
                } else {
                    // Use the regular cell for all other rows
                    listCell.updateItem(item, false);
                    setGraphic(listCell.getGraphic());
                    // Make sure regular cells are selectable
                    setDisable(false);
                    setStyle("");
                }
            }
        };
    }
}
