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
import matrixCalculator.operations.UnaryOperations;

/**
 * Manages the rank and row echelon window.
 */
public class RowEchelonController {

    @FXML
    private Button computeRank;
    @FXML
    private Button bAddRow;
    @FXML
    private Button bDeleteRow;
    @FXML
    private Button bAddColumn;
    @FXML
    private Button bDeleteColumn;
    @FXML
    private Button copyClipboard;

    @FXML
    private BorderPane mainPane; // Wraps all panes
    @FXML
    private GridPane matrixPane; // Wraps matrixContainer
    @FXML
    private GridPane matrixContainer;  // Wraps grid
    @FXML
    private StackPane resultPane; // Where result can be shown
    @FXML
    private ScrollPane scrollPane; // Wraps resultPane

    private GridPane grid; // GridPane representing the matrix, contains TextFields
    private LatexCanvas lastCanvas; // Last canvas on which a Latex result have been written

    private static Fraction[][] rowEchelonMatrix; // Matrix reduced to row echelon form
    private static int rank; // Matrix rank

    public void initialize() {

        HomeController.alert.setText(HomeController.RESIZE_ALERT);
        this.mainPane.setTop(HomeController.anchorPane);
        this.copyClipboard.setVisible(false); // copyClipboard button will appear just after result is shown
        this.lastCanvas = null;

        this.grid = new GridPane();

        this.matrixContainer.add(this.grid, 0, 1);

        TextField startTextA = new TextField();
        this.grid.setAlignment(Pos.CENTER);
        this.grid.add(startTextA, 0, 0);
        GridManagement.setTextFieldLayout(startTextA);

        // Start matrix 3x3
        for (int i = 0; i < 2; i++) {
            this.addColumn();
            this.addRow();
        }

        this.setButtonsOnAction();
        this.setWindowListeners();
    }

    private void setButtonsOnAction() {
        this.computeRank.setOnAction(e -> this.reduceRowEchelonForm());
        this.bAddRow.setOnAction(e -> this.addRow());
        this.bDeleteRow.setOnAction(e -> this.deleteRow());
        this.bAddColumn.setOnAction(e -> this.addColumn());
        this.bDeleteColumn.setOnAction(e -> this.deleteColumn());
        this.copyClipboard.setOnAction(e -> this.copyToClipboard());
    }

    private void reduceRowEchelonForm() {
        HomeController.alert.setVisible(false);
        // Matrix on which compute rank
        Fraction[][] matrix = MatrixLayout.getFractionMatrix(this.grid);
        try {
            rowEchelonMatrix = UnaryOperations.reduceToRowEchelonForm(matrix);
            rank = UnaryOperations.computeRank(rowEchelonMatrix);
            this.showResult();
        } catch (ArithmeticException e) {
            HomeController.showOverflowAlert();
        }
    }

    private void copyToClipboard() {
        MatrixLayout.copyToClipboard(rowEchelonMatrix);
    }

    private void showResult() {
        this.copyClipboard.setVisible(true);
        // Show the result pane with the result formatted in latex
        this.resultPane.getChildren().removeAll(this.lastCanvas);
        String latex = MatrixLayout.convertMatrixToLatex(rowEchelonMatrix);
        LatexCanvas lc = new LatexCanvas("$rank$" + latex + "$=$" + rank);

        this.setResultPaneSize();

        this.resultPane.getChildren().add(lc);
        StackPane.setAlignment(lc, Pos.CENTER);
        this.lastCanvas = lc;
        lc.widthProperty().bind(this.resultPane.widthProperty());
        lc.heightProperty().bind(this.resultPane.heightProperty());
    }

    private void setResultPaneSize() {
        int width = MatrixLayout.computeLatexMatrixWidth(rowEchelonMatrix) + 100; // +100 width of result matrix and rank
        this.resultPane.setPrefWidth(width);
        this.resultPane.setMinWidth(width);
        this.resultPane.setMaxWidth(width);
        this.scrollPane.setPrefWidth(width + 10);
        this.scrollPane.setPrefWidth(width + 10);
        this.scrollPane.setPrefWidth(width + 10);
        int height = MatrixLayout.computeLatexMatrixHeight(rowEchelonMatrix);
        this.resultPane.setPrefHeight(height);
        this.resultPane.setMinHeight(height);
        this.resultPane.setMaxHeight(height);
        this.scrollPane.setPrefHeight(height + 10);
        this.scrollPane.setMinHeight(height + 10);
        this.scrollPane.setMaxHeight(height + 10);
    }

    private void addColumn() {

        if (GridManagement.isValidToAddColumn(this.grid) && this.canAddColumn()) {
            GridManagement.addColumn(this.grid);
        }
    }

    private void addRow() {
        if (GridManagement.isValidToAddRow(this.grid) && this.canAddRow()) {
            GridManagement.addRow(this.grid);
        }
    }

    private void deleteColumn() {
        if (GridManagement.isValidToDeleteColumn(this.grid)) {
            GridManagement.deleteColumn(this.grid);
        }
    }

    private void deleteRow() {
        if (GridManagement.isValidToDeleteRow(this.grid)) {
            GridManagement.deleteRow(this.grid);
        }
    }

    private boolean canAddRow() {
        if ((this.matrixPane.getHeight() < (this.grid.getRowCount() + 1) * 70) && (!(this.grid.getRowCount() < 3)) && (!(this.grid.getRowCount() == GridManagement.MAX_ROWS))) {
            HomeController.alert.setVisible(true);
            return false;
        } else {
            HomeController.alert.setVisible(false);
            return true;
        }
    }

    private boolean canAddColumn() {
        if (((this.matrixPane.getWidth() < (this.grid.getColumnCount() + 1) * 60)) && (!(this.grid.getColumnCount() < 3)) && (!(this.grid.getColumnCount() == GridManagement.MAX_COLUMNS))) {
            HomeController.alert.setVisible(true);
            return false;
        } else {
            HomeController.alert.setVisible(false);
            return true;
        }
    }

    private void setWindowListeners() {
        Main.homeStage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                if (oldSceneWidth.intValue() < newSceneWidth.intValue()) {
                    HomeController.alert.setVisible(false);
                } else {
                    while (((newSceneWidth.intValue() - 240) < (RowEchelonController.this.grid.getColumnCount()) * 120)) {
                        RowEchelonController.this.deleteColumn();
                    }
                }
            }
        });
        Main.homeStage.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                if (oldSceneHeight.intValue() < newSceneHeight.intValue()) {
                    HomeController.alert.setVisible(false);
                } else {
                    while (((newSceneHeight.intValue() - 400) < (RowEchelonController.this.grid.getRowCount()) * 35)) {
                        RowEchelonController.this.deleteRow();
                    }
                }
            }
        });
    }
}
