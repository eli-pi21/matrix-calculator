package matrixCalculator.operations;

import matrixCalculator.numberDataTypes.Fraction;

/**
 * Manages operations with two Fraction matrices.
 */
public class BinaryOperations {

    /**
     * Multiply two matrices.
     *
     * @param matrix1 Fraction matrix. Number of columns must be the same of number of rows of the second one.
     * @param matrix2 Fraction matrix. Number of rows must be the same of number of columns of the first one.
     * @return Matrix results of the product of matrix1 by matrix2, null if conditions are not observed.
     */
    public static Fraction[][] multiply(Fraction[][] matrix1, Fraction[][] matrix2) {

        if (!isMultiplicationValid(matrix1, matrix2))
            return null;
        int r1 = matrix1.length; // Number rows first matrix
        int rc = matrix2.length; // Number columns first matrix and row second one
        int c2 = matrix2[0].length; // Number columns second matrix
        Fraction[][] matrix3 = new Fraction[r1][c2];
        for (int r3 = 0; r3 < r1; r3++) {
            for (int c3 = 0; c3 < c2; c3++) {
                matrix3[r3][c3] = new Fraction(0, 1);
                for (int i = 0; i < rc; i++) {
                    Fraction f = Fraction.multiply(matrix1[r3][i], matrix2[i][c3]);
                    matrix3[r3][c3] = Fraction.add(matrix3[r3][c3], f);
                }
            }
        }
        return matrix3;
    }

    /**
     * Subtract two matrices.
     *
     * @param matrix1 Fraction matrix. Number of rows must be the same of number of rows of the second one.
     * @param matrix2 Fraction matrix. Number of columns must be the same of number of columns of the first one.
     * @return Matrix result of matrix1 minus matrix2, null if conditions are not observed.
     */
    public static Fraction[][] sub(Fraction[][] matrix1, Fraction[][] matrix2) {

        if (!isSubAddValid(matrix1, matrix2))
            return null;
        int r = matrix1.length;
        int c = matrix1[0].length;
        Fraction[][] matrix3 = new Fraction[r][c];

        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                matrix3[i][j] = Fraction.sub(matrix1[i][j], matrix2[i][j]);
            }
        }
        return matrix3;
    }

    /**
     * Add two matrices.
     *
     * @param matrix1 Fraction matrix. Number of rows must be the same of number of rows of the second one.
     * @param matrix2 Fraction matrix. Number of columns must be the same of number of columns of the first one.
     * @return Matrix result of matrix1 plus matrix2, null if conditions are not observed.
     */
    public static Fraction[][] add(Fraction[][] matrix1, Fraction[][] matrix2) {

        if (!isSubAddValid(matrix1, matrix2))
            return null;
        int r = matrix1.length;
        int c = matrix1[0].length;
        Fraction[][] matrix3 = new Fraction[r][c];

        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                matrix3[i][j] = Fraction.add(matrix1[i][j], matrix2[i][j]);
            }
        }
        return matrix3;
    }

    /**
     * Multiplies a matrix by a int scalar.
     *
     * @param matrix Can be any Fraction matrix.
     * @param scalar Int umber by which multiplying the matrix.
     * @return Matrix result of the product of matrix by the scalar.
     */
    public static Fraction[][] multiplyByScalar(Fraction[][] matrix, int scalar) {
        return multiplyByScalar(matrix, new Fraction(scalar, 1));
    }

    /**
     * Multiplies a matrix by a Fraction scalar.
     *
     * @param matrix Can be any Fraction matrix.
     * @param scalar Fraction number by which multiplying the matrix.
     * @return Matrix result of the product of matrix by the scalar.
     */
    public static Fraction[][] multiplyByScalar(Fraction[][] matrix, Fraction scalar) {

        int r = matrix.length;
        int c = matrix[0].length;
        Fraction[][] matrixRes = new Fraction[r][c];

        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                matrixRes[i][j] = Fraction.multiply(matrix[i][j], scalar);
            }
        }
        return matrixRes;
    }

    /**
     * Checks if multiplication by the two matrices is valid.
     */
    public static boolean isMultiplicationValid(Fraction[][] matrix1, Fraction[][] matrix2) {
        if (matrix1 == null)
            return false;
        if (matrix2 == null)
            return false;
        return matrix1[0].length == matrix2.length;
    }

    /**
     * Checks if addition or subtraction of the two matrices is valid.
     */
    public static boolean isSubAddValid(Fraction[][] matrix1, Fraction[][] matrix2) {
        if (matrix1 == null)
            return false;
        if (matrix2 == null)
            return false;
        return (matrix1.length == matrix2.length) || (matrix1[0].length == matrix2[0].length);
    }

    /**
     * Raises a matrix to a power.
     *
     * @param matrix Any square matrix.
     * @param pow    Power to which raise the matrix.
     * @return The matrix raised to the input power, if it isn't square return null.
     */
    public static Fraction[][] pow(Fraction[][] matrix, int pow) {
        if (!UnaryOperations.isSquare(matrix))
            return null;
        if (pow == 0) {
            return UnaryOperations.getIdentityMatrix(matrix.length);
        }
        Fraction[][] result = matrix;
        for (int i = 2; i <= pow; i++) {
            result = multiply(result, matrix);
        }
        return result;
    }

    @Deprecated
    public static int[][] multiply(int[][] matrix1, int[][] matrix2) {

        int r1 = matrix1.length;
        int rc = matrix2.length;
        int c2 = matrix2[0].length;
        int[][] matrix3 = new int[r1][c2];

        for (int r3 = 0; r3 < r1; r3++) {
            for (int c3 = 0; c3 < c2; c3++) {
                for (int i = 0; i < rc; i++) {
                    matrix3[r3][c3] += matrix1[r3][i] * matrix2[i][c3];
                }
            }
        }
        return matrix3;
    }

    @Deprecated
    public static int[][] sub(int[][] matrix1, int[][] matrix2) {

        int r = matrix1.length;
        int c = matrix1[0].length;
        int[][] matrix3 = new int[r][c];

        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                matrix3[i][j] = matrix1[i][j] - matrix2[i][j];
            }
        }
        return matrix3;
    }

    @Deprecated
    public static int[][] add(int[][] matrix1, int[][] matrix2) {

        int r = matrix1.length;
        int c = matrix1[0].length;
        int[][] matrix3 = new int[r][c];

        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                matrix3[i][j] = matrix1[i][j] + matrix2[i][j];
            }
        }
        return matrix3;
    }
}
