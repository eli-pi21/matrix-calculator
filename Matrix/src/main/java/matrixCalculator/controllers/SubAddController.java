package matrixCalculator.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import matrixCalculator.HomeController;
import matrixCalculator.Main;
import matrixCalculator.actions.GridManagement;
import matrixCalculator.actions.LatexCanvas;
import matrixCalculator.actions.MatrixLayout;
import matrixCalculator.numberDataTypes.Fraction;
import matrixCalculator.operations.BinaryOperations;

/**
 * Manages the addition and subtraction window.
 */
public class SubAddController {

    @FXML
    private Button opAdd;
    @FXML
    private Button opSub;
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
    private GridPane matrixPane; // Wraps gridA and gridB
    @FXML
    private GridPane matrixContainerA; // Wraps gridA
    @FXML
    private GridPane matrixContainerB; // Wraps gridB
    @FXML
    private ScrollPane scrollPane; // Wraps resultPane
    @FXML
    private StackPane resultPane; // Where result can be shown
    @FXML
    private ColumnConstraints columnConstraintsTex; // Column constraints of result grid

    private GridPane gridA; // GridPane representing the matrixA, contains TextFields
    private GridPane gridB; // GridPane representing the matrixB, contains TextFields
    private LatexCanvas lastCanvas; // Last canvas on which a Latex result have been written

    private static Fraction[][] matrixC; // Result matrix

    public void initialize() {

        HomeController.alert.setText(HomeController.RESIZE_ALERT);
        this.mainPane.setTop(HomeController.anchorPane);
        this.copyClipboard.setVisible(false);
        this.lastCanvas = null;

        this.gridA = new GridPane();
        this.gridB = new GridPane();

        this.matrixContainerA.add(this.gridA, 0, 1);
        this.matrixContainerB.add(this.gridB, 0, 1);

        TextField startTextA = new TextField();
        TextField startTextB = new TextField();
        this.gridA.setAlignment(Pos.CENTER);
        this.gridB.setAlignment(Pos.CENTER);
        this.gridA.add(startTextA, 0, 0);
        this.gridB.add(startTextB, 0, 0);
        GridManagement.setTextFieldLayout(startTextA);
        GridManagement.setTextFieldLayout(startTextB);

        // Start matrix 3x3
        for (int i = 0; i < 2; i++) {
            this.addColumn();
            this.addRow();
        }

        this.setButtonsOnAction();
        this.setWindowListeners();
    }

    private void setButtonsOnAction() {
        this.opAdd.setOnAction(e -> this.add());
        this.opSub.setOnAction(e -> this.sub());
        this.bAddRow.setOnAction(e -> this.addRow());
        this.bDeleteRow.setOnAction(e -> this.deleteRow());
        this.bAddColumn.setOnAction(e -> this.addColumn());
        this.bDeleteColumn.setOnAction(e -> this.deleteColumn());
        this.copyClipboard.setOnAction(e -> this.copyToClipboard());
    }

    private void sub() {
        HomeController.alert.setVisible(false);
        Fraction[][] matrixA = MatrixLayout.getFractionMatrix(this.gridA);
        Fraction[][] matrixB = MatrixLayout.getFractionMatrix(this.gridB);
        try {
            matrixC = BinaryOperations.sub(matrixA, matrixB);
            this.showResult();
        } catch (ArithmeticException e) {
            HomeController.showOverflowAlert();
        }
    }

    private void add() {
        HomeController.alert.setVisible(false);
        Fraction[][] matrixA = MatrixLayout.getFractionMatrix(this.gridA);
        Fraction[][] matrixB = MatrixLayout.getFractionMatrix(this.gridB);
        try {
            matrixC = BinaryOperations.add(matrixA, matrixB);
            this.showResult();
        } catch (ArithmeticException e) {
            HomeController.showOverflowAlert();
        }
    }

    private void copyToClipboard() {
        MatrixLayout.copyToClipboard(matrixC);
    }

    private void showResult() {
        this.copyClipboard.setVisible(true);
        // Show the result pane with the result formatted in latex
        this.resultPane.getChildren().removeAll(this.lastCanvas);
        String latex = MatrixLayout.convertMatrixToLatex(matrixC);
        LatexCanvas lc = new LatexCanvas("$C=$" + latex);

        this.setResultPaneSize();

        this.resultPane.getChildren().add(lc);
        StackPane.setAlignment(lc, Pos.CENTER);
        this.lastCanvas = lc;
        lc.widthProperty().bind(this.resultPane.widthProperty());
        lc.heightProperty().bind(this.resultPane.heightProperty());
    }

    private void setResultPaneSize() {
        int width = MatrixLayout.computeLatexMatrixWidth(matrixC) + 75; // +75 width of result matrix and "C="
        this.columnConstraintsTex.setPrefWidth(width + 10);
        this.columnConstraintsTex.setMinWidth(width + 10);
        this.columnConstraintsTex.setMaxWidth(width + 10);
        this.scrollPane.setPrefWidth(width + 10);
        this.scrollPane.setPrefWidth(width + 10);
        this.scrollPane.setPrefWidth(width + 10);
        this.resultPane.setPrefWidth(width);
        this.resultPane.setMinWidth(width);
        this.resultPane.setMaxWidth(width);
        int height = MatrixLayout.computeLatexMatrixHeight(matrixC);
        this.resultPane.setPrefHeight(height);
        this.resultPane.setMinHeight(height);
        this.resultPane.setMaxHeight(height);
    }

    private void addColumn() {
        if (GridManagement.isValidToAddColumn(this.gridA) && this.canAddColumn()) {
            GridManagement.addColumn(this.gridA);
            GridManagement.addColumn(this.gridB);
        }
    }

    private void addRow() {
        if (GridManagement.isValidToAddRow(this.gridA) && this.canAddRow()) {
            GridManagement.addRow(this.gridA);
            GridManagement.addRow(this.gridB);
        }
    }

    private void deleteColumn() {
        if (GridManagement.isValidToDeleteColumn(this.gridA)) {
            GridManagement.deleteColumn(this.gridA);
            GridManagement.deleteColumn(this.gridB);
        }
    }

    private void deleteRow() {
        if (GridManagement.isValidToDeleteRow(this.gridA)) {
            GridManagement.deleteRow(this.gridA);
            GridManagement.deleteRow(this.gridB);
        }
    }

    private boolean canAddRow() {
        if ((this.matrixPane.getHeight() < (this.gridA.getRowCount() + 1) * 40) && (!(this.gridA.getRowCount() < 3)) && (!(this.gridA.getRowCount() == GridManagement.MAX_ROWS))) {
            HomeController.alert.setVisible(true);
            return false;
        } else {
            HomeController.alert.setVisible(false);
            return true;
        }
    }

    private boolean canAddColumn() {
        if ((this.matrixPane.getWidth() < (this.gridA.getColumnCount() + 1) * 120) && (!(this.gridA.getColumnCount() < 3)) && (!(this.gridA.getColumnCount() == GridManagement.MAX_COLUMNS))) {
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
                    while (((newSceneWidth.intValue() - 240) < (SubAddController.this.gridA.getColumnCount()) * 120)) {
                        SubAddController.this.deleteColumn();
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
                    while (((newSceneHeight.intValue() - 400) < (SubAddController.this.gridA.getRowCount()) * 40)) {
                        SubAddController.this.deleteRow();
                    }
                }
            }
        });
    }
}