package matrixCalculator.actions;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import matrixCalculator.numberDataTypes.Fraction;

import java.util.Arrays;

/**
 * Various methods to transform, format and work with Fraction matrices.
 */
public class MatrixLayout {

    /**
     * @param matrix Any Fraction matrix.
     * @return A String with the input matrix Latex formatted.
     */
    public static String convertMatrixToLatex(Fraction[][] matrix) {

        String latex = "\\begin{pmatrix}\n";

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                latex += matrix[i][j].toLatex() + " ";
                if (j != matrix[0].length - 1)
                    latex += "& ";
            }
            if (i != matrix.length - 1)
                latex += "\\\\";
            latex += "\n";
        }
        latex += "\\end{pmatrix}\n";

        return latex;
    }

    /**
     * Copies to clipboard the input matrix, according to curly brackets style (i.e. {{0,0,0},{0,0,0},{0,0,0}}).
     *
     * @param matrix Any Fraction matrix.
     */
    public static void copyToClipboard(Fraction[][] matrix) {
        ClipboardContent content = new ClipboardContent();
        String matrixString = Arrays.deepToString(matrix);
        matrixString = matrixString.replace("[", "{");
        matrixString = matrixString.replace("]", "}");
        content.putString(matrixString);
        Clipboard.getSystemClipboard().setContent(content);
    }

    /**
     * @param g The GridPane representing the matrix.
     * @return The Fraction matrix with the values in TextFields.
     */
    public static Fraction[][] getFractionMatrix(GridPane g) {
        Fraction[][] matrix = new Fraction[g.getRowCount()][g.getColumnCount()];
        ObservableList<Node> children = g.getChildren(); // Collects all children of the GridPane
        /*
         * Since rows and columns are added at runtime, the List containing grid's node doesn't have children in matrix order.
         * The following loop matches correctly children with its proper spot in matrix.
         * Converts any value in a Fraction Object, even if Integer or Double.
         */
        for (Node n : children) {
            if (n instanceof TextField) {
                int i = GridPane.getRowIndex(n);
                int j = GridPane.getColumnIndex(n);
                String s = ((TextField) n).getText(); // Gets value
                if (isValueInt(s)) { // Checks if value is Integer
                    matrix[i][j] = Fraction.toFraction(Integer.parseInt(s));
                } else if (isValueDouble(s)) { // Checks if value is Double
                    matrix[i][j] = Fraction.toFraction(Double.parseDouble(s));
                } else if (isValueFraction(s)) { // Checks if value is Fraction
                    String[] v = s.split("/");
                    matrix[i][j] = new Fraction(Integer.parseInt(v[0]), Integer.parseInt(v[1]));
                } else { // If value is invalid, it is replaced with a 0
                    matrix[i][j] = new Fraction(0, 1);
                    ((TextField) n).setText("0");
                }
            }
        }
        return matrix;
    }

    /**
     * Checks if a value is Integer.
     */
    private static boolean isValueInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks if a value is Double.
     */
    private static boolean isValueDouble(String value) {
        /*
         * To convert a double in a Fraction Object, using integers for numerator and denominator the raw value
         * without "." can't be greater than an Integer.
         */
        try {
            if (!value.contains("."))
                return false;
            Integer.parseInt(value.replace(".", ""));
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks if a value is Fraction.
     */
    private static boolean isValueFraction(String value) {
        String[] s = value.split("/");
        if (s.length != 2)
            return false;
        try {
            Integer.parseInt(s[0]);
            Integer.parseInt(s[1]);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Computes the height occupied by a latex matrix.
     */
    public static int computeLatexMatrixHeight(Fraction[][] matrix) {
        // Height changes if rows has fractions or simple values.
        int rowsWithFractions = MatrixLayout.countRowsWithFractions(matrix);
        int height = (52 * rowsWithFractions) + ((matrix.length - rowsWithFractions) * 23) + 10;
        return height;
    }

    /**
     * Counts number of rows of the matrix occupied by at least one fraction.
     */
    private static int countRowsWithFractions(Fraction[][] matrix) {
        int rows = 0;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j].isFraction()) {
                    rows++;
                    break;
                }
            }
        }
        return rows;
    }

    /**
     * Computes the width occupied by a latex matrix.
     */
    public static int computeLatexMatrixWidth(Fraction[][] matrix) {
        int maxDigits = countMaxRowDigit(matrix);
        int width = maxDigits * 10 + (matrix[0].length) * 25;
        return width;
    }

    /**
     * Counts the maximum number of digits along the rows in a latex matrix.
     * Sums the max number of digits in each column, minus sign is counted as 2 digits.
     */
    private static int countMaxRowDigit(Fraction[][] matrix) {
        int maxDigits = 0;
        for (int i = 0; i < matrix[0].length; i++) {
            int columnMaxDigits = 0; // Max number of digits of a value in the actual column
            for (int j = 0; j < matrix.length; j++) {
                int columnDigits;
                int extraDigits = 0;
                if (matrix[j][i].isNegative()) // Minus sign is counted as 2 digits
                    extraDigits = 2;
                int numeratorDigits = (Math.abs(matrix[j][i].getNumerator()) + "").length() + extraDigits;
                int denominatorDigits = (Math.abs(matrix[j][i].getDenominator()) + "").length() + extraDigits;
                columnDigits = Math.max(numeratorDigits, denominatorDigits);
                columnMaxDigits = Math.max(columnDigits, columnMaxDigits);
            }
            maxDigits += columnMaxDigits; // Sums the max number of digits in the column to the final value
        }
        return maxDigits;
    }

    @Deprecated
    public static int[][] getMatrix(GridPane g) {
        int[][] matrix = new int[g.getRowCount()][g.getColumnCount()];
        ObservableList<Node> children = g.getChildren();
        for (Node n : children) {
            if (n instanceof TextField) {
                int i = g.getRowIndex(n);
                int j = g.getColumnIndex(n);
                if (isValueInt(((TextField) n).getText())) {
                    matrix[i][j] = Integer.parseInt(((TextField) n).getText());
                } else {
                    matrix[i][j] = 0;
                    ((TextField) n).setText("0");
                }
            }
        }
        return matrix;
    }

    @Deprecated
    public static String convertMatrixToLatex(int[][] matrix) {
        String latex = "\\begin{pmatrix}\n";
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                latex += matrix[i][j] + " ";
                if (j != matrix[0].length - 1)
                    latex += "& ";
            }
            if (i != matrix.length - 1)
                latex += "\\\\";
            latex += "\n";
        }
        latex += "\\end{pmatrix}\n";
        return latex;
    }

}