package matrixCalculator.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import matrixCalculator.HomeController;
import matrixCalculator.Main;
import matrixCalculator.actions.GridManagement;
import matrixCalculator.actions.LatexCanvas;
import matrixCalculator.actions.MatrixLayout;
import matrixCalculator.numberDataTypes.Fraction;
import matrixCalculator.operations.Determinant;

/**
 * Manages the determinant window.
 */
public class DeterminantController {

    @FXML
    private Button opDet;
    @FXML
    private Button bAddRowColumn;
    @FXML
    private Button bDeleteRowColumn;
    @FXML
    private Button copyClipboard;

    @FXML
    private BorderPane mainPane; // Wraps all panes
    @FXML
    private GridPane matrixPane; // Wraps matrixContainer
    @FXML
    private GridPane matrixContainer; // Wraps grid
    @FXML
    private StackPane resultPane; // Where result can be shown
    @FXML
    private ScrollPane scrollPane; // Wraps resultPane

    private GridPane grid; // GridPane representing the matrix, contains TextFields
    private LatexCanvas lastCanvas; // Last canvas on which a Latex result have been written

    private static Fraction[][] matrix; // Matrix on which compute determinant
    private static Fraction resDeterminant; // Result of computing

    public void initialize() {

        HomeController.alert.setText(HomeController.RESIZE_ALERT);
        this.mainPane.setTop(HomeController.anchorPane);
        this.copyClipboard.setVisible(false); // copyClipboard button will appear just after result is shown
        this.lastCanvas = null;

        this.grid = new GridPane();

        this.matrixContainer.add(this.grid, 0, 1);

        TextField startText = new TextField();
        this.grid.setAlignment(Pos.CENTER);
        this.grid.add(startText, 0, 0);
        GridManagement.setTextFieldLayout(startText);

        // Start matrix 3x3
        for (int i = 0; i < 2; i++) {
            this.addRowColumn();
        }

        this.setButtonsOnAction();
        this.setWindowListeners();
    }

    private void setButtonsOnAction() {
        this.bAddRowColumn.setOnAction(e -> this.addRowColumn());
        this.bDeleteRowColumn.setOnAction(e -> this.deleteRowColumn());
        this.opDet.setOnAction(e -> this.computeDet());
        this.copyClipboard.setOnAction(e -> this.copyToClipboard());
    }

    private void computeDet() {
        HomeController.alert.setVisible(false);
        matrix = MatrixLayout.getFractionMatrix(this.grid);
        try {
            resDeterminant = Determinant.computeDeterminant(matrix);
            this.showResult();
        } catch (ArithmeticException e) {
            HomeController.showOverflowAlert();
        }
    }

    private void copyToClipboard() {
        MatrixLayout.copyToClipboard(matrix);
    }

    private void showResult() {
        this.copyClipboard.setVisible(true);
        // Show the result pane with the result formatted in latex
        this.resultPane.getChildren().removeAll(this.lastCanvas);
        String latex = MatrixLayout.convertMatrixToLatex(matrix);
        LatexCanvas lc = new LatexCanvas("$det$" + latex + "$= " + resDeterminant.toLatex() + "$");

        this.setResultPaneSize();

        this.resultPane.getChildren().add(lc);
        StackPane.setAlignment(lc, Pos.CENTER);
        this.lastCanvas = lc;
        lc.widthProperty().bind(this.resultPane.widthProperty());
        lc.heightProperty().bind(this.resultPane.heightProperty());
    }

    private void setResultPaneSize() {
        int width = MatrixLayout.computeLatexMatrixWidth(matrix) + 100; // +100 width of result determinant and "det"
        this.resultPane.setPrefWidth(width);
        this.resultPane.setMinWidth(width);
        this.resultPane.setMaxWidth(width);
        this.scrollPane.setPrefWidth(width + 10);
        this.scrollPane.setPrefWidth(width + 10);
        this.scrollPane.setPrefWidth(width + 10);
        int height = MatrixLayout.computeLatexMatrixHeight(matrix);
        this.resultPane.setPrefHeight(height);
        this.resultPane.setMinHeight(height);
        this.resultPane.setMaxHeight(height);
        this.scrollPane.setPrefHeight(height + 10);
        this.scrollPane.setMinHeight(height + 10);
        this.scrollPane.setMaxHeight(height + 10);
    }

    private void addRowColumn() {
        if (GridManagement.isValidToAddRow(this.grid) && this.canAddColumnRow()) {
            GridManagement.addRow(this.grid);
            GridManagement.addColumn(this.grid);
        }
    }

    private void deleteRowColumn() {
        if (GridManagement.isValidToDeleteRow(this.grid)) {
            GridManagement.deleteRow(this.grid);
            GridManagement.deleteColumn(this.grid);
        }
    }

    private boolean canAddColumnRow() {
        if (((this.matrixPane.getWidth() < (this.grid.getColumnCount() + 1) * 60)) && (!(this.grid.getColumnCount() < 3)) && (!(this.grid.getColumnCount() == GridManagement.MAX_COLUMNS))) {
            HomeController.alert.setVisible(true);
            return false;
        } else {
            HomeController.alert.setVisible(false);
            return true;
        }
    }

    // Delete rows and columns resizing the window if matrix don't fit anymore
    private void setWindowListeners() {
        Main.homeStage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                if (oldSceneWidth.intValue() < newSceneWidth.intValue()) {
                    HomeController.alert.setVisible(false);
                } else {
                    while (((newSceneWidth.intValue() - 240) < (DeterminantController.this.grid.getColumnCount()) * 120)) {
                        DeterminantController.this.deleteRowColumn();
                    }
                }
            }
        });
    }
}
