package matrixCalculator.actions;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;

import java.util.HashSet;
import java.util.Set;
import java.util.function.UnaryOperator;

/**
 * Manages the grids with TextFields representing matrices.
 */
public class GridManagement {

    /**
     * Max number of rows that can be added to the matrix.
     */
    public static final int MAX_ROWS = 10;
    /**
     * Max number of columns that can be added to the matrix.
     */
    public static final int MAX_COLUMNS = 10;

    /**
     * Adds a row to the matrix grid.
     *
     * @param grid Grid representing the matrix.
     * @return true if row is successfully added, false otherwise.
     */
    public static boolean addRow(GridPane grid) {

        int nRow = grid.getRowCount();
        int nCol = grid.getColumnCount();

        if (nRow == MAX_ROWS)
            return false;

        TextField[] newRow = new TextField[nCol];
        for (int i = 0; i < newRow.length; i++) { // Creates a new TextField array to add to the grid as new row
            TextField tf = new TextField();
            setTextFieldLayout(tf); // Formats the TextField
            newRow[i] = tf;
        }

        grid.addRow(grid.getRowCount(), newRow);
        return true;
    }

    /**
     * Deletes a row from the matrix grid.
     *
     * @param grid Grid representing the matrix.
     * @return true if row is successfully deleted, false otherwise.
     */
    public static boolean deleteRow(GridPane grid) {

        int nRow = grid.getRowCount();

        if (nRow == 1)
            return false;

        Set<Node> deleteNodes = new HashSet<>();
        int i = 0;
        for (Node node : grid.getChildren()) { // Collects the children nodes (TextFields) of the last row
            Integer r = GridPane.getRowIndex(node);
            if (r != null && r == nRow - 1)
                deleteNodes.add(node);
        }
        grid.getChildren().removeAll(deleteNodes); // Deletes from the grid the collected children
        return true;
    }

    /**
     * Adds a column to the matrix grid.
     *
     * @param grid Grid representing the matrix.
     * @return true if column is successfully added, false otherwise.
     */
    public static boolean addColumn(GridPane grid) {

        int nRow = grid.getRowCount();
        int nCol = grid.getColumnCount();

        if (nCol == MAX_COLUMNS)
            return false;

        TextField[] newCol = new TextField[nRow];
        for (int i = 0; i < newCol.length; i++) { // Creates a new TextField array to add to the grid as new column
            TextField tf = new TextField();
            setTextFieldLayout(tf); // Formats the TextField
            newCol[i] = tf;
        }

        grid.addColumn(grid.getColumnCount(), newCol);
        return true;
    }

    /**
     * Deletes a column to the matrix grid.
     *
     * @param grid Grid representing the matrix.
     * @return true if column is successfully deleted, false otherwise.
     */
    public static boolean deleteColumn(GridPane grid) {

        int nCol = grid.getColumnCount();

        if (nCol == 1)
            return false;

        Set<Node> deleteNodes = new HashSet<>();
        int i = 0;
        for (Node node : grid.getChildren()) { // Collects the children nodes (TextFields) of the last column
            Integer c = GridPane.getColumnIndex(node);
            if (c != null && c == nCol - 1)
                deleteNodes.add(node);
        }
        grid.getChildren().removeAll(deleteNodes); // Deletes from the grid the collected children
        return true;
    }

    /**
     * @param grid Grid representing the matrix.
     * @return true if row can be added, false otherwise.
     */
    public static boolean isValidToAddRow(GridPane grid) {
        int nRow = grid.getRowCount();
        return nRow != MAX_ROWS;
    }

    /**
     * @param grid Grid representing the matrix.
     * @return true if row can be deleted, false otherwise.
     */
    public static boolean isValidToDeleteRow(GridPane grid) {
        int nRow = grid.getRowCount();
        return nRow != 1;
    }

    /**
     * @param grid Grid representing the matrix.
     * @return true if column can be added, false otherwise.
     */
    public static boolean isValidToAddColumn(GridPane grid) {
        int nCol = grid.getColumnCount();
        return nCol != MAX_COLUMNS;
    }

    /**
     * @param grid Grid representing the matrix.
     * @return true if column can be deleted, false otherwise.
     */
    public static boolean isValidToDeleteColumn(GridPane grid) {
        int nCol = grid.getColumnCount();
        return nCol != 1;
    }

    /**
     * Formats the input TextField to a matrix cell.
     *
     * @param textField Any TextField.
     */
    public static void setTextFieldLayout(TextField textField) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();
            if (text.contains("/") || text.contains(".") || text.contains("-") || text.matches("\\d*")) {
                return change;
            }
            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter); // Sets the TextFormatter
        textField.setTextFormatter(textFormatter);
        textField.setAlignment(Pos.CENTER);
        textField.setStyle("-fx-focus-color: #9acd7e;");
        textField.setPromptText("0");
        textField.setMaxHeight(30);
        textField.setMinHeight(30);
        textField.setMaxWidth(50);
        textField.setMinWidth(50);
        textField.setPrefHeight(30);
        textField.setPrefWidth(50);
    }
}