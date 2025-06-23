package com.stkych.rivergreenap.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main application class for the TableView with multiple selection demonstration.
 * This application demonstrates a TableView with multiple selection capabilities:
 * - Dragging/holding over items
 * - Ctrl key for non-contiguous selection
 * - Shift key for range selection
 */
public class MultiSelectTableApplication extends Application {

    private static final String FXML_PATH = "/com/stkych/rivergreenap/test/draggableList.fxml";

    /**
     * Starts the application.
     * Loads the FXML file and displays the main scene.
     * 
     * @param stage The primary stage for this application
     * @throws IOException If the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXML_PATH));
        Parent root = fxmlLoader.load();
        
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("TableView with Multiple Selection");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Main method to launch the application.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}