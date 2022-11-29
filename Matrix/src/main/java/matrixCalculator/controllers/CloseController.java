package matrixCalculator.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Manages the closing window.
 */
public class CloseController {

    @FXML
    private Button yes;
    @FXML
    private Button no;

    public void initialize() {
        this.yes.setOnAction(e -> this.close());
        this.no.setOnAction(e -> this.dontClose());
    }

    private void close() {
        System.exit(0); // Exit the program
    }

    private void dontClose() {
        this.no.getScene().getWindow().hide(); // Close modal window and return home
    }

}
