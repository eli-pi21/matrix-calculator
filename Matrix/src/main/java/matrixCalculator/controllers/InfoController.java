package matrixCalculator.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import matrixCalculator.Main;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the window with commands.
 */
public class InfoController {

    @FXML
    private VBox vBox;

    private static final String COMMAND_1 = "1) Insert your matrices (you can insert integers, decimal numbers " +
            "or fraction, as 2/3) in the visible text fields. To add rows and columns click + or - under the matrix " +
            "you want to be bigger or smaller.";
    private static final String COMMAND_2 = "2) If you add columns to matrix A or row to matrix B for the matrix " +
            "product, a row/column to the other matrix will be added automatically to preserve the validity of the multiplication.";
    private static final String COMMAND_3 = "3) To work better with bigger matrices, resize the window to leave more " +
            "space to the matrices.";
    private static final String COMMAND_4 = "4) If your input is absent, too big or invalid, it will be automatically " +
            "replaced " + "with a 0.";
    private static final String COMMAND_5 = "5) With big matrices, use scrollbars to view the entire result matrix.";
    private static final String COMMAND_6 = "6) If you want to compute an expression with multiplication, addition and" +
            " subtraction, you can work just with n×n matrices to avoid conflicts.";
    private static final String COMMAND_7 = "7) Decreasing the window size, keep in mind you might lose some matrix dimensions " +
            "as the matrices fit in the window.";

    private List<Label> labels; // List containing commands

    public void initialize() {

        this.labels = new ArrayList<>();

        Label l1 = new Label(COMMAND_1);
        Label l2 = new Label(COMMAND_2);
        Label l3 = new Label(COMMAND_3);
        Label l4 = new Label(COMMAND_4);
        Label l5 = new Label(COMMAND_5);
        Label l6 = new Label(COMMAND_6);
        Label l7 = new Label(COMMAND_7);

        // Adds commands to list
        this.labels.add(l1);
        this.labels.add(l2);
        this.labels.add(l3);
        this.labels.add(l4);
        this.labels.add(l5);
        this.labels.add(l6);
        this.labels.add(l7);

        this.setLabelStyle(this.labels);
        this.vBox.getChildren().addAll(this.labels);
        this.setWindowListeners();
    }

    private void setLabelStyle(List<Label> labels) {
        for (Label l : labels) {
            l.getStyleClass().add("labelCommands"); // sets css file
            l.setWrapText(true);
        }
    }

    // Changes labels width arranging window size
    private void setWindowListeners() {
        Main.homeStage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                int width = newSceneWidth.intValue() - 60; // 60 min padding computed
                for (Label l : InfoController.this.labels) {
                    InfoController.this.setLabelWidth(l, width);
                }
            }
        });
    }

    private void setLabelWidth(Label l, int width) {
        l.setPrefWidth(width);
        l.setMinWidth(width);
        l.setMaxWidth(width);
    }
}