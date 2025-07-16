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
public class SceneSwitcher {
    private static final String FXML_PATH = "/com/stkych/rivergreenap/";
    private static Stage primaryStage;
    private static final Map<String, Object> dataCache = new HashMap<>();

    /**
     * Initializes the SceneSwitcher with the primary stage.
     *
     * @param stage The primary stage of the application
     */
    public static void initialize(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Loads an FXML file and returns the root element.
     * Throws an exception if FXML file can't be found.
     *
     * @param fxmlFile The name of the FXML file to load (without path or extension)
     * @return The root element of the loaded FXML file
     * @throws IOException If the FXML file cannot be loaded
     */
    public static Parent loadFXML(String fxmlFile) throws IOException {
        String resourcePath = FXML_PATH + fxmlFile + ".fxml";
        FXMLLoader fxmlLoader = new FXMLLoader(SceneSwitcher.class.getResource(resourcePath));
        if (fxmlLoader.getLocation() == null) {
            throw new IOException("Cannot find FXML file: " + resourcePath);
        }
        return fxmlLoader.load();
    }


    /**
     * Switches to a new scene.
     *
     * @param fxmlFile The name of the FXML file to load (without path or extension)
     * @param title The title of the new scene
     * @param width The width of the new scene
     * @param height The height of the new scene
     * @throws IOException If the FXML file cannot be loaded
     */
    public static void switchScene(String fxmlFile, String title, double width, double height) throws IOException {
        Parent root = loadFXML(fxmlFile);
        Scene scene = new Scene(root, width, height);
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Switches to a new scene.
     * Override with default dimensions (1600x880) if none given.
     *
     * @param fxmlFile The name of the FXML file to load (without path or extension)
     * @param title The title of the new scene
     * @throws IOException If the FXML file cannot be loaded
     */
    public static void switchScene(String fxmlFile, String title) throws IOException {
        switchScene(fxmlFile, title, 1100, 800);
    }

    /**
     * Creates and shows a popup window.
     *
     * @param fxmlFile The name of the FXML file to load (without path or extension)
     * @param title    The title of the popup window
     * @param width    The width of the popup window
     * @param height   The height of the popup window
     * @return The controller instance of the loaded FXML
     * @throws IOException If the FXML file cannot be loaded
     */
    public static Object showPopup(String fxmlFile, String title, double width, double height) throws IOException {
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

    /**
     * Creates and shows a popup window.
     * Overload with default dimensions (400x300) if none given.
     *
     * @param fxmlFile The name of the FXML file to load (without path or extension)
     * @param title    The title of the popup window
     * @return The controller instance of the loaded FXML
     * @throws IOException If the FXML file cannot be loaded
     */
    public static Object showPopup(String fxmlFile, String title) throws IOException {
        return showPopup(fxmlFile, title, 400, 300);
    }

    /**
     * Stores data in the cache to use in other scenes.
     *
     * @param key The key to store the data under
     * @param value The data to store
     */
    public static void putData(String key, Object value) {
        dataCache.put(key, value);
    }

    /**
     * Retrieves data from the cache.
     *
     * @param key The key to retrieve the data for
     * @return The data stored under the given key, or null if no data is found
     */
    public static Object getData(String key) {
        return dataCache.get(key);
    }

    /**
     * Removes data from the cache.
     *
     * @param key The key to remove the data for
     * @return The data that was removed, or null if no data was found
     */
    public static Object removeData(String key) {
        return dataCache.remove(key);
    }

    /**
     * Clears the data cache.
     */
    public static void clearData() {
        dataCache.clear();
    }
}
