package matrixCalculator.operations;

import matrixCalculator.numberDataTypes.Fraction;

import java.util.Arrays;

/**
 * <p>Manages operations e transformations on one matrix.</p>
 * <p>Identity matrix, transposed matrix, inverse matrix, row echelon form.</p>
 */

public class UnaryOperations {

    /**
     * Provides the identity matrix of desired order.
     *
     * @param order Order of the desired matrix.
     * @return An identity matrix of the specified order.
     */
    public static Fraction[][] getIdentityMatrix(int order) {
        Fraction[][] identity = new Fraction[order][order];
        for (int i = 0; i < order; i++) {
            for (int j = 0; j < order; j++) {
                if (i == j)
                    identity[i][j] = new Fraction(1, 1);
                else
                    identity[i][j] = new Fraction(0, 1);
            }
        }
        return identity;
    }

    /**
     * Provides the transposed of the input matrix.
     *
     * @param matrix Any Fraction matrix.
     * @return The transposed matrix of the matrix as parameter.
     */
    public static Fraction[][] getTransposedMatrix(Fraction[][] matrix) {
        Fraction[][] transposed = new Fraction[matrix[0].length][matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                transposed[i][j] = matrix[j][i];
            }
        }
        return transposed;
    }

    /**
     * Provides the inverse of the input matrix.
     *
     * @param matrix A square matrix.
     * @return The inverse of the matrix as parameter if the determinant is different from 0, otherwise null.
     */
    public static Fraction[][] getInverseMatrix(Fraction[][] matrix) {
        Fraction determinant = Determinant.computeDeterminant(matrix);
        if (determinant.equals(new Fraction(0, 1)))
            return null;
        Fraction[][] cof = new Fraction[matrix.length][matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                int k;
                if ((i + j) % 2 == 0)
                    k = 1;
                else
                    k = -1;
                Fraction[][] subMatrix = computeComplementaryMinor(matrix, i, j);
                cof[i][j] = Fraction.multiply(Fraction.toFraction(k),
                        Determinant.computeDeterminant(subMatrix));
            }
        }
        Fraction mul = determinant.getInverse();
        Fraction[][] inverse = BinaryOperations.multiplyByScalar(UnaryOperations.getTransposedMatrix(cof), mul);
        return inverse;
    }

    /**
     * Computes the complementary minor as requested.
     *
     * @param matrix Any square matrix.
     * @param i      Number of the row not to consider.
     * @param j      Number of the column not to consider.
     * @return The complementary minor of the matrix without the i-th row and the j-th column, null if the matrix is
     * not square.
     */
    public static Fraction[][] computeComplementaryMinor(Fraction[][] matrix, int i, int j) {

        if (!isSquare(matrix))
            return null;
        Fraction[][] complementaryMinor = new Fraction[matrix.length - 1][matrix.length - 1];
        for (int i1 = 0, i2 = 0; i1 < matrix.length - 1; i1++, i2++) { //i1 indice sottomatrice, i2 indice matrice originale
            if (i2 == i)
                i2++;
            for (int j1 = 0, j2 = 0; j1 < matrix.length - 1; j1++, j2++) {
                if (j2 == j)
                    j2++;
                complementaryMinor[i1][j1] = matrix[i2][j2];
            }
        }
        return complementaryMinor;
    }

    /**
     * Checks if the matrix is in the Row Echelon Form.
     *
     * @param matrix Any Fraction matrix.
     * @return true if the matrix is in the Row Echelon Form, false otherwise.
     */
    public static boolean isRowEchelonForm(Fraction[][] matrix) {
        if (matrix[0].length == 1)
            return true;
        int i = 0;
        int zeros = -1;
        while (i < matrix.length) {
            int j = 0;
            int rowZeros = 0;
            while (j < matrix[0].length) {
                if (matrix[i][j].equals(new Fraction(0, 1))) {
                    rowZeros++;
                } else {
                    break;
                }
                j++;
            }
            if (rowZeros == zeros) {
                if (rowZeros != matrix[0].length)
                    return false;
            } else if (rowZeros < zeros) {
                return false;
            }
            zeros = rowZeros;
            i++;
        }
        return true;
    }

    /**
     * Computes the Row Echelon Form of the given matrix according to the Gaussian algorithm.
     * The logic of the steps can be found at the following URL:
     * https://www.youmath.it/lezioni/algebra-lineare/matrici-e-vettori/831-eliminazione-di-gauss.html
     *
     * @param startMatrix Any Fraction matrix.
     * @return The Row Echelon Form of the given matrix.
     */
    public static Fraction[][] reduceToRowEchelonForm(Fraction[][] startMatrix) {

        Fraction[][] matrix = copyMatrix(startMatrix);
        int actCol = 0;
        int actRow = 0;
        while (!isRowEchelonForm(matrix) && actRow < matrix.length && actCol < matrix[0].length) {
            int i = actRow;
            int j = actCol;
            // Finds first column with a not null element "a"
            while (j < matrix[0].length && i < matrix.length && matrix[i][j].equals(Fraction.toFraction(0))) {
                i = actRow;
                while (i < matrix.length && matrix[i][j].equals(Fraction.toFraction(0))) {
                    i++;
                }
                if (i < matrix.length /*&& !matrix[i][j].equals(Fraction.toFraction(0))*/)
                    break;
                j++;
            }
            // If "a" is not an element of the actual row
            if (i != actRow) {
                switchRows(matrix, actRow, i);
            }
            int k = actRow + 1;
            while (k < matrix.length) {
                // Nullifying all element under "a"
                if (!matrix[k][j].equals(Fraction.toFraction(0))) {
                    Fraction x = Fraction.divide(matrix[k][j], matrix[actRow][j]);
                    for (int z = j; z < matrix[0].length; z++) {
                        // Multiplies the whole row by the found scalar
                        Fraction res = Fraction.multiply(matrix[actRow][z], x);
                        matrix[k][z] = Fraction.sub(matrix[k][z], res);
                    }
                }
                k++;
            }
            actCol = j + 1;
            actRow++;
        }
        return matrix;
    }

    /**
     * Switches two rows of any matrix creating entirely new objects and not referring the new rows to the old ones.
     *
     * @param matrix Any matrix.
     * @param i      Index of the first row.
     * @param j      Index of the second row.
     */
    private static void switchRows(Fraction[][] matrix, int i, int j) {
        Fraction[] row1 = Arrays.copyOf(matrix[i], matrix[i].length);
        Fraction[] row2 = Arrays.copyOf(matrix[j], matrix[j].length);
        matrix[i] = Arrays.copyOf(row2, row2.length);
        matrix[j] = Arrays.copyOf(row1, row1.length);
    }

    /**
     * Creates a entirely new copy if the original matrix without any reference.
     *
     * @param matrix Any matrix.
     * @return A copy of the given matrix.
     */
    private static Fraction[][] copyMatrix(Fraction[][] matrix) {
        Fraction[][] copy = new Fraction[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                copy[i][j] = Fraction.copyOf(matrix[i][j]);
            }
        }
        return copy;
    }

    /**
     * Computes rank of the given matrix.
     *
     * @param matrix Any Fraction matrix, even if it isn't reduced to the Row Echelon Form.
     * @return The rank of the given matrix.
     */
    public static int computeRank(Fraction[][] matrix) {
        Fraction[][] rowEchelon = reduceToRowEchelonForm(matrix);
        int nullRows = 0;
        for (int i = 0; i < rowEchelon.length; i++) {
            int zeros = countRowStartZeros(rowEchelon[i]);
            if (zeros == rowEchelon[0].length)
                nullRows++;
        }
        int rank = rowEchelon.length - nullRows;
        return rank;
    }

    /**
     * @param row Array which represents the row of a matrix.
     * @return The number of zeros before a not zero number from the start of the row.
     */
    private static int countRowStartZeros(Fraction[] row) {
        int i = 0;
        while (i < row.length && row[i].equals(Fraction.toFraction(0))) {
            i++;
        }
        return i;
    }

    /**
     * Checks if a matrix is square.
     *
     * @param matrix Any matrix.
     * @return true if it's a square matrix, false otherwise.
     */
    public static boolean isSquare(Fraction[][] matrix) {
        return matrix.length == matrix[0].length;
    }
}
