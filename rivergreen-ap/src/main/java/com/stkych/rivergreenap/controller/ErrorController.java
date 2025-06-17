package com.stkych.rivergreenap.controller;

import javafx.fxml.FXML;
import java.awt.Desktop;
import java.net.URI;
import java.io.IOException;

/**
 * Controller for the error scene of the application.
 * Shows up whenever a patient number isn't provided as a command line argument.
 */
public class ErrorController {
    /**
     * Handles the close button click event.
     * Exits the application.
     */
    @FXML
    private void handleCloseButtonAction() {
        System.exit(0);
    }

    /**
     * Handles the action of a hyperlink click event.
     * Opens a web browser and navigates to the specified URI.
     *
     * This method attempts to open the default web browser to visit the URL
     * "https://github.com/isoseven/rgdc-ap". If the operation fails (e.g., due to an exception),
     * the error message is logged to the standard error stream.
     */
    @FXML
    private void handleHyperlink() {
        try {
            Desktop.getDesktop().browse(new URI("https://github.com/isoseven/rgdc-ap"));
        } catch (Exception e) {
            System.err.println("Error opening URL: " + e.getMessage());
        }
    }
}
