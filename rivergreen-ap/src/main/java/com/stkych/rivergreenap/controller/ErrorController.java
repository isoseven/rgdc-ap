package com.stkych.rivergreenap.controller;

import javafx.fxml.FXML;
import java.awt.Desktop;
import java.net.URI;
import java.io.IOException;


public class ErrorController {
    @FXML
    private void handleCloseButtonAction() {
        System.exit(0);
    }

    @FXML
    private void handleHyperlink() {
        try {
            Desktop.getDesktop().browse(new URI("https://github.com/isoseven/rgdc-ap"));
        } catch (Exception e) {
            System.err.println("Error opening URL: " + e.getMessage());
        }
    }
}
