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
 * Manages the multiplication window.
 */
public class MultiplicationController {

    @FXML
    private Button opMul;
    @FXML
    private Button bAddRowA;
    @FXML
    private Button bDeleteRowA;
    @FXML
    private Button bAddColumnA;
    @FXML
    private Button bDeleteColumnA;
    @FXML
    private Button bAddRowB;
    @FXML
    private Button bDeleteRowB;
    @FXML
    private Button bAddColumnB;
    @FXML
    private Button bDeleteColumnB;
    @FXML
    private Button copyClipboard;

    @FXML
    private BorderPane mainPane; // Wraps all panes
    @FXML
    private GridPane matrixPane; // Wraps matrixContainerA and matrixContainer
    @FXML
    private GridPane matrixContainerA; // Wraps gridA
    @FXML
    private GridPane matrixContainerB; // Wraps gridB
    @FXML
    private ScrollPane scrollPane;  // Wraps resultPane
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
            this.addColumnA();
            this.addRowA();
            this.addColumnB();
        }

        this.setButtonsOnAction();
        this.setWindowListeners();
    }

    private void setButtonsOnAction() {
        this.bAddRowA.setOnAction(e -> this.addRowA());
        this.bDeleteRowA.setOnAction(e -> this.deleteRowA());
        this.bAddColumnA.setOnAction(e -> this.addColumnA());
        this.bDeleteColumnA.setOnAction(e -> this.deleteColumnA());
        this.bAddRowB.setOnAction(e -> this.addRowB());
        this.bDeleteRowB.setOnAction(e -> this.deleteRowB());
        this.bAddColumnB.setOnAction(e -> this.addColumnB());
        this.bDeleteColumnB.setOnAction(e -> this.deleteColumnB());
        this.opMul.setOnAction(e -> this.multiply());
        this.copyClipboard.setOnAction(e -> this.copyToClipboard());
    }

    private void multiply() {
        HomeController.alert.setVisible(false);
        Fraction[][] matrixA = MatrixLayout.getFractionMatrix(this.gridA);
        Fraction[][] matrixB = MatrixLayout.getFractionMatrix(this.gridB);
        try {
            matrixC = BinaryOperations.multiply(matrixA, matrixB);
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

    private void addColumnA() {
        if (GridManagement.isValidToAddColumn(this.gridA) && GridManagement.isValidToAddRow(this.gridB) && this.canAddColumnARowB()) {
            GridManagement.addColumn(this.gridA);
            GridManagement.addRow(this.gridB);
        }
    }

    private void addRowA() {
        if (GridManagement.isValidToAddRow(this.gridA) && this.canAddRowA())
            GridManagement.addRow(this.gridA);
    }

    private void deleteColumnA() {
        if (GridManagement.isValidToDeleteColumn(this.gridA) && GridManagement.isValidToDeleteRow(this.gridB)) {
            GridManagement.deleteColumn(this.gridA);
            GridManagement.deleteRow(this.gridB);
        }
    }

    private void deleteRowA() {
        if (GridManagement.isValidToDeleteRow(this.gridA))
            GridManagement.deleteRow(this.gridA);
    }

    private void addColumnB() {
        if (GridManagement.isValidToAddColumn(this.gridB) && this.canAddColumnB())
            GridManagement.addColumn(this.gridB);
    }

    private void addRowB() {
        this.addColumnA();
    }

    private void deleteColumnB() {
        if (GridManagement.isValidToDeleteColumn(this.gridB))
            GridManagement.deleteColumn(this.gridB);
    }

    private void deleteRowB() {
        this.deleteColumnA();
    }

    private boolean canAddRowA() {
        if (((this.matrixPane.getHeight() < (this.gridA.getRowCount() + 1) * 40) && (!(this.gridA.getRowCount() < 3)) && (!(this.gridA.getRowCount() == GridManagement.MAX_ROWS)))) {
            this.mainPane.setTop(HomeController.anchorPane);
            HomeController.alert.setVisible(true);
            return false;
        } else {
            HomeController.alert.setVisible(false);
            return true;
        }
    }

    private boolean canAddColumnB() {
        if (((this.matrixPane.getWidth() < (this.gridB.getColumnCount() + 1) * 120)) && (!(this.gridB.getColumnCount() < 3)) && (!(this.gridB.getColumnCount() == GridManagement.MAX_COLUMNS))) {
            this.mainPane.setTop(HomeController.anchorPane);
            HomeController.alert.setVisible(true);
            return false;
        } else {
            HomeController.alert.setVisible(false);
            return true;
        }
    }

    private boolean canAddColumnARowB() {
        if (((this.matrixPane.getWidth() < (this.gridA.getColumnCount() + 1) * 120)) && (!(this.gridA.getColumnCount() < 3)) && (!(this.gridA.getColumnCount() == GridManagement.MAX_COLUMNS))) {
            this.mainPane.setTop(HomeController.anchorPane);
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
                    /*
                    while ((matrixPane.getWidth() < (gridA.getColumnCount()) * 120)) {
                        deleteColumnA();
                    }
                    while ((matrixPane.getWidth() < (gridB.getColumnCount()) * 120)) {
                        deleteColumnB();
                    }
                    if (((int) matrixPane.getWidth()) == 1297) {
                        while (gridA.getColumnCount() > 3) {
                            deleteColumnA();
                        }
                        while (gridB.getColumnCount() > 3) {
                            deleteColumnB();
                        }
                    }
                     */
                    while (((newSceneWidth.intValue() - 240) < (MultiplicationController.this.gridA.getColumnCount()) * 120)) {
                        MultiplicationController.this.deleteColumnA();
                    }
                    while (((newSceneWidth.intValue() - 400) < (MultiplicationController.this.gridB.getColumnCount()) * 120)) {
                        MultiplicationController.this.deleteColumnB();
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
                    /*
                    while ((matrixPane.getHeight() < (gridA.getRowCount()) * 40)) {

                        deleteRowA();
                    }
                    while ((matrixPane.getHeight() < (gridB.getRowCount()) * 40)) {
                        deleteRowB();
                    }
                    if (((int) matrixPane.getHeight()) == 452) {
                        while (gridA.getRowCount() > 3) {
                            deleteRowA();
                        }
                        while (gridB.getRowCount() > 3) {
                            deleteRowB();
                        }
                    }
                     */
                    while (((newSceneHeight.intValue() - 400) < (MultiplicationController.this.gridA.getRowCount()) * 40)) {
                        MultiplicationController.this.deleteRowA();
                    }
                    while (((newSceneHeight.intValue() - 400) < (MultiplicationController.this.gridB.getRowCount()) * 40)) {
                        MultiplicationController.this.deleteRowB();
                    }
                }
            }
        });
    }
}

/*
matrixPane.prefHeightProperty().bind(contA.widthProperty());
matrixPane.prefWidthProperty().bind(contB.heightProperty());
contA.prefHeightProperty().bind(gridA.widthProperty());
contB.prefWidthProperty().bind(gridB.heightProperty());
mainPane.prefHeightProperty().bind(matrixPane.widthProperty());
mainPane.prefHeightProperty().bind(matrixPane.heightProperty());
*/
