package matrixCalculator.operations;

import matrixCalculator.numberDataTypes.Fraction;

/**
 * Methods to compute determinant.
 */
public class Determinant {

    /**
     * Compute the determinant of the matrix expanding along the first column.
     *
     * @param matrix Any matrix.
     * @return The determinant of the input matrix, null if matrix isn't square or is null.
     */
    public static Fraction computeDeterminant(Fraction[][] matrix) {
        if (!isDetValid(matrix))
            return null;
        // Expanding along first column
        int r = matrix.length;
        if (r == 1) { // Base case 1x1
            return matrix[0][0];
        } else if (r == 2) { // Base case 2x2
            return Fraction.sub(Fraction.multiply(matrix[0][0], matrix[1][1]), Fraction.multiply(matrix[0][1], matrix[1][0]));
        } else {
            Fraction det = new Fraction(0, 1);
            for (int i = 0; i < r; i++) {
                int k;
                if (i % 2 == 0)
                    k = 1;
                else
                    k = -1;
                Fraction[][] subMatrix = UnaryOperations.computeComplementaryMinor(matrix, i, 0);
                Fraction detAct = Fraction.multiply(new Fraction(k, 1), matrix[i][0], computeDeterminant(subMatrix));
                det = Fraction.add(det, detAct);
            }
            return det;
        }
    }

    /**
     * Checks if it's valid to compute the determinant on the given matrix.
     */
    public static boolean isDetValid(Fraction[][] matrix) {
        if (matrix == null)
            return false;
        return matrix.length == matrix[0].length;
    }

    @Deprecated
    public static boolean isDetValid(int[][] matrix) {
        if (matrix == null)
            return false;
        return matrix.length == matrix[0].length;
    }

    @Deprecated
    public static int computeDeterminant(int[][] matrix) {
        if (!isDetValid(matrix))
            return 0;
        // Expanding along first column
        int r = matrix.length;
        if (r == 1) { // Base case 1x1
            return matrix[0][0];
        } else if (r == 2) { // Base case 2x2
            return (matrix[0][0] * matrix[1][1]) - (matrix[0][1] * matrix[1][0]);
        } else {
            int det = 0;
            for (int i = 0; i < r; i++) {
                int k;
                if (i % 2 == 0)
                    k = 1;
                else
                    k = -1;
                int[][] subMatrix = new int[r - 1][r - 1];
                for (int i1 = 0, i2 = 0; i1 < r - 1; i1++, i2++) { //i1 sub matrix index, i2 original matrix index
                    if (i2 == i)
                        i2++;
                    for (int j1 = 0, j2 = 0; j1 < r - 1; j1++, j2++) {
                        if (j2 == 0)
                            j2++;
                        subMatrix[i1][j1] = matrix[i2][j2];
                    }
                }
                det += k * matrix[i][0] * computeDeterminant(subMatrix);
            }
            return det;
        }
    }
}
