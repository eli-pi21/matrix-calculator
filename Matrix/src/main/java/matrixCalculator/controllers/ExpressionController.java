package matrixCalculator.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
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
import matrixCalculator.operations.Expression;

import java.util.function.UnaryOperator;

/**
 * Manages the expression window.
 */
public class ExpressionController {

    @FXML
    private Button op;
    @FXML
    private Button bAddRowColumn;
    @FXML
    private Button bDeleteRowColumn;
    @FXML
    private TextField expressionInput;
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
    private ScrollPane scrollPane; // Wraps resultPane
    @FXML
    private StackPane resultPane; // Where result can be shown
    @FXML
    private ColumnConstraints columnConstraintsTex; // Column constraints of result grid

    private GridPane gridA; // GridPane representing the matrixA, contains TextFields
    private GridPane gridB; // GridPane representing the matrixB, contains TextFields
    private LatexCanvas lastCanvas; // Last canvas on which a Latex result have been written

    public static Fraction[][] matrixC;  // Result matrix

    private static final String INVALID_EXPRESSION_ALERT = "Invalid expression, try again.";
    private static final String EXPRESSION_EXAMPLE = "A*(2A*5B)-4B+A*{A*[4(A-3B)]-B+A}+2B";

    public void initialize() {

        HomeController.alert.setText(HomeController.RESIZE_ALERT);
        this.mainPane.setTop(HomeController.anchorPane);
        this.copyClipboard.setVisible(false);
        this.lastCanvas = null;

        this.setTextFieldLayout();

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
            this.addRowColumn();
        }

        this.setButtonsOnAction();
        this.setWindowListeners();
    }

    private void setTextFieldLayout() {
        this.expressionInput.setPromptText(EXPRESSION_EXAMPLE);
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();
            if (text.contains("*") || text.contains("+") || text.contains("-") || text.contains("A") || text.contains(
                    "B") || text.contains("{") || text.contains("}") || text.contains("[") || text.contains("]") || text.contains("(") || text.contains(")") || text.matches("\\d*")/*|| text.matches("^?")*/) {
                return change;
            }
            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        this.expressionInput.setTextFormatter(textFormatter);
        this.expressionInput.setStyle("-fx-focus-color: #9acd7e;");
        this.expressionInput.setMaxWidth(500);
        this.expressionInput.setMinWidth(500);
        this.expressionInput.setPrefWidth(500);
    }

    private void setButtonsOnAction() {
        this.op.setOnAction(e -> this.computeExpression());
        this.bAddRowColumn.setOnAction(e -> this.addRowColumn());
        this.bDeleteRowColumn.setOnAction(e -> this.deleteRowColumn());
        this.copyClipboard.setOnAction(e -> this.copyToClipboard());
    }

    private void computeExpression() {
        HomeController.alert.setVisible(false);
        String expression = this.expressionInput.getText();
        if (Expression.isExpressionValid(expression)) {
            Fraction[][] matrixA = MatrixLayout.getFractionMatrix(this.gridA);
            Fraction[][] matrixB = MatrixLayout.getFractionMatrix(this.gridB);
            try {
                matrixC = Expression.computeExpression(expression, matrixA, matrixB);
                this.showResult();
            } catch (ArithmeticException e) {
                HomeController.showOverflowAlert();
            } catch (Exception e) {
                showInvalidExpressionAlert();
                // e.printStackTrace();
            }
        } else {
            showInvalidExpressionAlert();
        }
    }

    private static void showInvalidExpressionAlert() {
        HomeController.alert.setText(INVALID_EXPRESSION_ALERT);
        HomeController.alert.setVisible(true);
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
        int width = MatrixLayout.computeLatexMatrixWidth(matrixC) + 50; // +75 width of result matrix and "C="
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

    private void addRowColumn() {
        if (GridManagement.isValidToAddRow(this.gridA) && this.canAddRowColumn()) {
            GridManagement.addColumn(this.gridA);
            GridManagement.addColumn(this.gridB);
            GridManagement.addRow(this.gridA);
            GridManagement.addRow(this.gridB);
        }
    }

    private void deleteRowColumn() {
        if (GridManagement.isValidToDeleteRow(this.gridA)) {
            GridManagement.deleteColumn(this.gridA);
            GridManagement.deleteColumn(this.gridB);
            GridManagement.deleteRow(this.gridA);
            GridManagement.deleteRow(this.gridB);
        }
    }

    private boolean canAddRowColumn() {
        HomeController.alert.setText(HomeController.RESIZE_ALERT);
        if ((this.matrixPane.getWidth() < (this.gridA.getColumnCount() + 1) * 125) && (!(this.gridA.getColumnCount() < 3)) && (!(this.gridA.getColumnCount() == GridManagement.MAX_COLUMNS))) {
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
                    while (((newSceneWidth.intValue() - 240) < (ExpressionController.this.gridA.getColumnCount()) * 130)) {
                        ExpressionController.this.deleteRowColumn();
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
                    while (((newSceneHeight.intValue() - 400) < (ExpressionController.this.gridA.getRowCount()) * 40)) {
                        ExpressionController.this.deleteRowColumn();
                    }
                }
            }
        });
    }
}