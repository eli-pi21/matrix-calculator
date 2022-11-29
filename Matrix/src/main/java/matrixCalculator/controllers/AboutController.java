package matrixCalculator.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Manages the window "About" with information about the programs.
 */
public class AboutController {

    @FXML
    private Button close;

    public void initialize() {
        this.close.setOnAction(e -> this.closeWindow());
    }

    private void closeWindow() {
        this.close.getScene().getWindow().hide(); // Close the "About" window
    }
}
