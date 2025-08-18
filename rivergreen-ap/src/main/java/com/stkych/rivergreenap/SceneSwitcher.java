package com.stkych.rivergreenap;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for handling scene transitions in the application.
 * Provides methods for loading and switching between scenes
 * and supports passing data between scenes.
 */
public final class SceneSwitcher {
    private static final String FXML_PATH = "/com/stkych/rivergreenap/";
    private static final SceneSwitcher INSTANCE = new SceneSwitcher();
    private Stage primaryStage;
    private final Map<String, Object> dataCache = new HashMap<>();

    private SceneSwitcher() {
    }

    public static SceneSwitcher getInstance() {
        return INSTANCE;
    }

    public void initialize(Stage stage) {
        this.primaryStage = stage;
    }

    public Parent loadFXML(String fxmlFile) throws IOException {
         String resourcePath = FXML_PATH + fxmlFile + ".fxml";
        FXMLLoader fxmlLoader = new FXMLLoader(SceneSwitcher.class.getResource(resourcePath));
        if (fxmlLoader.getLocation() == null) {
            throw new IOException("Cannot find FXML file: " + resourcePath);
        }
        return fxmlLoader.load();
    }

    public void switchScene(String fxmlFile, String title, double width, double height) throws IOException {
        Parent root = loadFXML(fxmlFile);
        Scene scene = new Scene(root, width, height);
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void switchScene(String fxmlFile, String title) throws IOException {
        switchScene(fxmlFile, title, 1100, 800);
    }

    public Object showPopup(String fxmlFile, String title, double width, double height) throws IOException {
        String resourcePath = FXML_PATH + fxmlFile + ".fxml";
        FXMLLoader fxmlLoader = new FXMLLoader(SceneSwitcher.class.getResource(resourcePath));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, width, height);
        Stage popupStage = new Stage();
        popupStage.setTitle(title);
        popupStage.setScene(scene);
        popupStage.show();
        return fxmlLoader.getController();
    }

    public Object showPopup(String fxmlFile, String title) throws IOException {
        return showPopup(fxmlFile, title, 400, 300);
    }

    public void putData(String key, Object value) {
        dataCache.put(key, value);
    }

    public Object getData(String key) {
        return dataCache.get(key);
    }

    public Object removeData(String key) {
        return dataCache.remove(key);
    }

    public void clearData() {
        dataCache.clear();
    }
}
