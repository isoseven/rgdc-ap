package com.stkych.rivergreenap.archive.test;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Controller for the TableView with multiple selection demonstration.
 * Implements selection via dragging, Ctrl key, and Shift key.
 */
public class MultiSelectTableController implements Initializable {

    @FXML
    private TableView<DataItem> tableView;

    @FXML
    private TableColumn<DataItem, Integer> idColumn;

    @FXML
    private TableColumn<DataItem, String> nameColumn;

    @FXML
    private TableColumn<DataItem, String> descriptionColumn;

    @FXML
    private TableColumn<DataItem, LocalDate> dateColumn;

    @FXML
    private Label selectedItemsLabel;

    private ObservableList<DataItem> data = FXCollections.observableArrayList();

    // Variables to track drag selection
    private int dragStartIndex = -1;
    private boolean isDragging = false;
    private double dragStartY = -1; // Store the initial Y position when drag starts

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configure the table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        // Enable multiple selection
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Add listener to update the selected items count
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            updateSelectedItemsCount();
        });

        // Populate the table with sample data
        populateTable();

        // Set the data to the table
        tableView.setItems(data);

        // Set up mouse event handlers for drag selection
        tableView.setOnMousePressed(this::handleMousePressed);
        tableView.setOnMouseDragged(this::handleMouseDragged);
        tableView.setOnMouseReleased(this::handleMouseReleased);
    }

    /**
     * Handles mouse pressed events to start drag selection.
     * 
     * @param event The mouse event
     */
    private void handleMousePressed(MouseEvent event) {
        // Get the row index at the mouse position
        int rowIndex = getRowIndexAt(event.getY());

        // Debug output
        System.out.println("Mouse Pressed - Y: " + event.getY() + 
                           ", Scene Y: " + event.getSceneY() + 
                           ", Screen Y: " + event.getScreenY() + 
                           ", Row Index: " + rowIndex);

        if (rowIndex >= 0 && rowIndex < tableView.getItems().size()) {
            // Start drag selection
            dragStartIndex = rowIndex;
            dragStartY = event.getY(); // Store the initial Y position
            isDragging = true;

            // Additional debug output
            System.out.println("Drag selection started - Start Index: " + dragStartIndex + 
                               ", Start Y: " + dragStartY);

            // If not holding Ctrl or Shift, clear previous selection
            if (!event.isControlDown() && !event.isShiftDown()) {
                tableView.getSelectionModel().clearSelection();
            }

            // Select the clicked row
            tableView.getSelectionModel().select(rowIndex);

            // Consume the event to prevent default handling
            event.consume();
        }
    }

    /**
     * Gets the row index at the specified y-coordinate.
     * 
     * @param y The y-coordinate
     * @return The row index at the specified y-coordinate, or -1 if none
     */
    private int getRowIndexAt(double y) {
        // Calculate the row index based on the y-coordinate
        double headerHeight = tableView.lookup(".column-header-background") != null ? 
            tableView.lookup(".column-header-background").getBoundsInLocal().getHeight() : 0;

        // Get the row height
        double rowHeight = estimateRowHeight();

        // Get the vertical scroll bar
        ScrollBar verticalScrollBar = (ScrollBar) tableView.lookup(".scroll-bar:vertical");
        double scrollOffset = 0.0;

        if (verticalScrollBar != null && verticalScrollBar.isVisible()) {
            // Get the first visible row index
            int firstVisibleRowIndex = -1;
            for (Node node : tableView.lookupAll(".table-row-cell")) {
                if (node instanceof TableRow && node.isVisible()) {
                    TableRow<?> row = (TableRow<?>) node;
                    if (firstVisibleRowIndex == -1 || row.getIndex() < firstVisibleRowIndex) {
                        firstVisibleRowIndex = row.getIndex();
                    }
                }
            }

            // Calculate the offset based on the first visible row
            if (firstVisibleRowIndex > 0) {
                scrollOffset = firstVisibleRowIndex * rowHeight;
            }

            // Debug output for scrolling
            System.out.println("First Visible Row Index: " + firstVisibleRowIndex + 
                               ", Row Height: " + rowHeight + 
                               ", Scroll Offset: " + scrollOffset);
        }

        // Adjust y to account for header
        double adjustedY = y - headerHeight;

        // Calculate the visible row index (relative to what's visible)
        int visibleRowIndex = (int) Math.floor(adjustedY / rowHeight);

        // Calculate the actual row index (accounting for scrolling)
        int actualRowIndex = -1;

        // Try to find the actual row at this position
        for (Node node : tableView.lookupAll(".table-row-cell")) {
            if (node instanceof TableRow && node.isVisible()) {
                TableRow<?> row = (TableRow<?>) node;
                double rowY = row.localToParent(0, 0).getY();
                if (rowY <= y && y < rowY + row.getHeight()) {
                    actualRowIndex = row.getIndex();
                    break;
                }
            }
        }

        // If we couldn't find the actual row, estimate it
        if (actualRowIndex == -1) {
            // Get the first visible row index
            int firstVisibleRowIndex = -1;
            for (Node node : tableView.lookupAll(".table-row-cell")) {
                if (node instanceof TableRow && node.isVisible()) {
                    TableRow<?> row = (TableRow<?>) node;
                    if (firstVisibleRowIndex == -1 || row.getIndex() < firstVisibleRowIndex) {
                        firstVisibleRowIndex = row.getIndex();
                    }
                }
            }

            if (firstVisibleRowIndex != -1) {
                actualRowIndex = firstVisibleRowIndex + visibleRowIndex;
            } else {
                actualRowIndex = visibleRowIndex;
            }
        }

        // Debug output
        System.out.println("Y: " + y + 
                           ", Adjusted Y: " + adjustedY + 
                           ", Visible Row Index: " + visibleRowIndex + 
                           ", Actual Row Index: " + actualRowIndex);

        // Ensure the index is within bounds
        if (actualRowIndex < 0) {
            return -1;
        } else if (actualRowIndex >= tableView.getItems().size()) {
            return tableView.getItems().size() - 1;
        }

        return actualRowIndex;
    }

    /**
     * Gets a TableRow from its index.
     * 
     * @param index The row index
     * @return The TableRow at the specified index, or null if not found
     */
    @SuppressWarnings("unchecked")
    private TableRow<DataItem> getRowFromIndex(int index) {
        for (Node node : tableView.lookupAll(".table-row-cell")) {
            if (node instanceof TableRow) {
                TableRow<DataItem> row = (TableRow<DataItem>) node;
                if (row.getIndex() == index) {
                    return row;
                }
            }
        }
        return null;
    }

    /**
     * Handles mouse dragged events to update selection during dragging.
     * 
     * @param event The mouse event
     */
    private void handleMouseDragged(MouseEvent event) {
        if (!isDragging || dragStartIndex < 0 || dragStartY < 0) return;

        // Handle auto-scrolling when dragging outside the visible area
        handleAutoScroll(event);

        // Get the current row index directly
        int currentIndex = getRowIndexAt(event.getY());

        // If we couldn't determine the current index, use the difference-based approach
        if (currentIndex < 0) {
            // Calculate how many rows to select based on the difference in Y position
            double currentY = event.getY();
            double yDifference = currentY - dragStartY;
            double rowHeight = estimateRowHeight();

            // Calculate the number of rows to select (positive or negative)
            int rowsToSelect = (int) Math.round(yDifference / rowHeight);

            // Calculate the current index based on the start index and rows to select
            currentIndex = dragStartIndex + rowsToSelect;

            // Debug output for difference-based calculation
            System.out.println("Using difference-based calculation - Y: " + currentY + 
                               ", Start Y: " + dragStartY +
                               ", Y Difference: " + yDifference +
                               ", Row Height: " + rowHeight +
                               ", Rows to Select: " + rowsToSelect);
        }

        // Ensure the current index is within bounds
        currentIndex = Math.max(0, Math.min(currentIndex, tableView.getItems().size() - 1));

        // Debug output
        System.out.println("Mouse Dragged - Y: " + event.getY() + 
                           ", Start Index: " + dragStartIndex +
                           ", Current Index: " + currentIndex);

        // Clear previous selection
        tableView.getSelectionModel().clearSelection();

        // Select all rows between start index and current index
        int startIdx = Math.min(dragStartIndex, currentIndex);
        int endIdx = Math.max(dragStartIndex, currentIndex);

        // Debug output for selection range
        System.out.println("Selecting rows from " + startIdx + " to " + endIdx);

        for (int i = startIdx; i <= endIdx; i++) {
            tableView.getSelectionModel().select(i);
        }

        // Consume the event to prevent default handling
        event.consume();
    }

    /**
     * Handles mouse released events to finalize selection.
     * 
     * @param event The mouse event
     */
    private void handleMouseReleased(MouseEvent event) {
        // Get the final row index
        int finalRowIndex = getRowIndexAt(event.getY());

        // Debug output
        System.out.println("Mouse Released - Y: " + event.getY() + 
                           ", Start Y: " + dragStartY +
                           ", Scene Y: " + event.getSceneY() + 
                           ", Screen Y: " + event.getScreenY() + 
                           ", Drag completed from index: " + dragStartIndex + 
                           " to index: " + finalRowIndex);

        // Additional debug output for selection
        int selectedCount = tableView.getSelectionModel().getSelectedItems().size();
        System.out.println("Final selection - Selected items: " + selectedCount);

        // End drag selection
        isDragging = false;
        dragStartY = -1; // Reset the drag start Y position

        // Consume the event to prevent default handling
        event.consume();
    }

    /**
     * Estimates the average row height in the table.
     * 
     * @return The estimated row height
     */
    private double estimateRowHeight() {
        // If fixed cell size is set, use that
        if (tableView.getFixedCellSize() > 0) {
            return tableView.getFixedCellSize();
        }

        // Try to get the height from a visible row
        for (Node node : tableView.lookupAll(".table-row-cell")) {
            if (node instanceof TableRow) {
                TableRow<?> row = (TableRow<?>) node;
                if (row.isVisible() && row.getHeight() > 0) {
                    return row.getHeight();
                }
            }
        }

        // Default fallback
        return 25.0;
    }

    /**
     * Handles auto-scrolling when dragging outside the visible area.
     * 
     * @param event The mouse event
     */
    private void handleAutoScroll(MouseEvent event) {
        double mouseY = event.getY();
        double tableHeight = tableView.getHeight();

        // Get the vertical scroll bar
        ScrollBar verticalScrollBar = (ScrollBar) tableView.lookup(".scroll-bar:vertical");
        if (verticalScrollBar == null || !verticalScrollBar.isVisible()) {
            return; // No scrolling needed if scroll bar is not visible
        }

        // Define the auto-scroll zones (top and bottom 10% of the table)
        double scrollZoneSize = tableHeight * 0.1;
        double scrollSpeed = 0.05; // Adjust this value to control scroll speed

        // Calculate header height
        double headerHeight = tableView.lookup(".column-header-background") != null ? 
            tableView.lookup(".column-header-background").getBoundsInLocal().getHeight() : 0;

        // Adjust mouseY to account for header
        double adjustedMouseY = mouseY;

        // Debug output for auto-scrolling
        System.out.println("Auto-scroll - Mouse Y: " + mouseY + 
                           ", Table Height: " + tableHeight + 
                           ", Header Height: " + headerHeight + 
                           ", Scroll Value: " + verticalScrollBar.getValue());

        // Check if mouse is in the top scroll zone
        if (adjustedMouseY < headerHeight + scrollZoneSize) {
            // Scroll up
            double newValue = Math.max(0, verticalScrollBar.getValue() - scrollSpeed);
            verticalScrollBar.setValue(newValue);
            System.out.println("Auto-scrolling UP to value: " + newValue);
        } 
        // Check if mouse is in the bottom scroll zone
        else if (adjustedMouseY > tableHeight - scrollZoneSize) {
            // Scroll down
            double newValue = Math.min(verticalScrollBar.getMax(), verticalScrollBar.getValue() + scrollSpeed);
            verticalScrollBar.setValue(newValue);
            System.out.println("Auto-scrolling DOWN to value: " + newValue);
        }
    }


    /**
     * Populates the table with sample data.
     */
    private void populateTable() {
        // Add 20 sample items
        for (int i = 1; i <= 20; i++) {
            data.add(new DataItem(
                i,
                "Item " + i,
                "Description for item " + i,
                LocalDate.now().minusDays(i)
            ));
        }
    }

    /**
     * Updates the selected items count label.
     */
    private void updateSelectedItemsCount() {
        int selectedCount = tableView.getSelectionModel().getSelectedItems().size();
        selectedItemsLabel.setText(String.valueOf(selectedCount));
    }

    /**
     * Clears the selection in the table.
     */
    @FXML
    private void clearSelection() {
        tableView.getSelectionModel().clearSelection();
    }

    /**
     * Selects all items in the table.
     */
    @FXML
    private void selectAll() {
        tableView.getSelectionModel().selectAll();
    }

    /**
     * Data class for the table items.
     */
    public static class DataItem {
        private final SimpleIntegerProperty id;
        private final SimpleStringProperty name;
        private final SimpleStringProperty description;
        private final SimpleObjectProperty<LocalDate> date;

        public DataItem(int id, String name, String description, LocalDate date) {
            this.id = new SimpleIntegerProperty(id);
            this.name = new SimpleStringProperty(name);
            this.description = new SimpleStringProperty(description);
            this.date = new SimpleObjectProperty<>(date);
        }

        public int getId() {
            return id.get();
        }

        public SimpleIntegerProperty idProperty() {
            return id;
        }

        public String getName() {
            return name.get();
        }

        public SimpleStringProperty nameProperty() {
            return name;
        }

        public String getDescription() {
            return description.get();
        }

        public SimpleStringProperty descriptionProperty() {
            return description;
        }

        public LocalDate getDate() {
            return date.get();
        }

        public SimpleObjectProperty<LocalDate> dateProperty() {
            return date;
        }
    }
}
