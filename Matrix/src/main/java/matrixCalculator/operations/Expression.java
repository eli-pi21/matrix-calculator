package matrixCalculator.operations;

import matrixCalculator.numberDataTypes.Fraction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Manages an expression containing various operations between scalars and two matrices, A and B.
 * <p>
 * An example: A*3B+{[3(2A+4B)(A+2B)]A}+B
 */

public class Expression {

    /**
     * Given the initial expression, adds the missing multiplication signs according to mathematical rules
     *
     * @param sequence A string containing a raw expression.
     * @return A list containing the characters of the original string memorized as Integers and the missing
     * multiplication signs added.
     */
    public static List<Integer> addMulSigns(String sequence) {
        List<Integer> mainExpression = new ArrayList<>();
        // Adds multiplication signs
        for (int i = 0; i < sequence.length(); i++) {
            if (sequence.charAt(i) >= '0' && sequence.charAt(i) <= '9') { // Manages scalars
                if (i > 0 && sequence.charAt(i - 1) != '*' && sequence.charAt(i - 1) != '(' && sequence.charAt(i - 1) != '[' && sequence.charAt(i - 1) != '{'
                        && sequence.charAt(i - 1) != '+' && sequence.charAt(i - 1) != '-') { // Before the scalar there is a letter or a closed parenthesis
                    mainExpression.add((int) '*');
                }
                String number = sequence.charAt(i) + "";
                i++;
                while ((i < sequence.length()) && (sequence.charAt(i) >= '0' && sequence.charAt(i) <= '9')) { // Builds the entire number
                    number += sequence.charAt(i) + "";
                    i++;
                }
                int scalar = -Integer.parseInt(number); // A scalar is memorized as negative integer to avoid conflicts
                mainExpression.add(scalar);
                i--;
                // If there is a letter, a scalar or an open parenthesis after the scalar
                if ((i < sequence.length() - 1 && sequence.charAt(i + 1) != ')' && sequence.charAt(i + 1) != ']' && sequence.charAt(i + 1) != '}'
                        && sequence.charAt(i + 1) != '*' && sequence.charAt(i + 1) != '+' && sequence.charAt(i + 1) != '-')) {
                    mainExpression.add((int) '*');
                }
            } else {
                boolean parNext = i < sequence.length() - 1 && (sequence.charAt(i + 1) ==
                        '(' || sequence.charAt(i + 1) == '[' || sequence.charAt(i + 1) == '{'); // If it follows a parenthesis
                mainExpression.add((int) sequence.charAt(i));
                if ((sequence.charAt(i) == 'A' || sequence.charAt(i) == 'B') && (parNext)) {
                    // Letter followed by parenthesis
                    mainExpression.add((int) '*');
                } else if (i < sequence.length() - 1 && ((sequence.charAt(i) == ')' && (parNext))
                        || (sequence.charAt(i) == ']' && (parNext))) || (sequence.charAt(i) == '}' && (parNext))) {
                    // Two consecutive parentheses
                    mainExpression.add((int) '*');
                }
            }
        }
        return mainExpression;
    }

    /**
     * @param simpleExpression An expression without parentheses.
     * @param results          List where result matrices can be memorized.
     * @param a                Any square matrix.
     * @param b                Any square matrix.
     * @return The original simpleExpression with the first unary operation solved (nothing if +, memorized in results if -)
     */
    public static List<Integer> resolveUnaryOperands(List<Integer> simpleExpression, List<Fraction[][]> results, Fraction[][] a, Fraction[][] b) {
        // Manages unary + and - in first position in a simple sequence (size >= 2, at least a sign and a scalar/letter)

        boolean prevUnaryMinus = simpleExpression.get(0) == '-';
        boolean prevUnaryPlus = simpleExpression.get(0) == '+';
        boolean isScalar = simpleExpression.get(1) < 0;
        boolean isLetterA = simpleExpression.get(1) == 'A';
        boolean isLetterB = simpleExpression.get(1) == 'B';

        if ((isScalar || isLetterA || isLetterB) && prevUnaryPlus) {
            simpleExpression.remove(0);
        } else if ((isScalar || isLetterA || isLetterB) && prevUnaryMinus) {
            Fraction[][] x;
            if (isLetterA)
                x = Arrays.copyOf(a, a.length);
            else if (isLetterB)
                x = Arrays.copyOf(b, b.length);
            else
                x = BinaryOperations.multiplyByScalar(UnaryOperations.getIdentityMatrix(a.length), -simpleExpression.get(1));
            // Scalar memorized is negative
            simpleExpression.subList(0, 2).clear();
            Fraction[][] f = BinaryOperations.multiplyByScalar(x, -1);
            results.add(f);
            int result = results.indexOf(f) + 128;
            simpleExpression.add(0, result);
        }

        return simpleExpression;
    }

    /**
     * Computes the operations in an expression between scalars and two matrices.
     *
     * @param sequence A raw valid string already checked which contains the expression.
     * @param a        Fraction matrix represented by the letter "A" in the sequence.
     * @param b        Fraction matrix represented by the letter "B" in the sequence.
     * @return A Fraction matrix result of the expression.
     * @throws Exception If there are unchecked conflicts or error in the sequence.
     */
    public static Fraction[][] computeExpression(String sequence, Fraction[][] a, Fraction[][] b) throws Exception {

        /*
         * mainExpression contains the expression which is resolved step by step.
         * Operands, parentheses and letters "A" and "B" are memorized with their character number according to ASCII.
         * Scalars are memorized with their correspondent negative number to avoid conflicts.
         * Integers >= 128 represents the matrices results of binary operations. The number corresponds to their
         * position in the List results minus 128 (to avoid conflicts with scalars, operands, letters and parentheses).
         */

        List<Fraction[][]> results = new ArrayList<>(); // List containing the memorized matrices results of binary or unary operations
        List<Integer> mainExpression = addMulSigns(sequence); // List containing the sequence to resolve

        // Looks for ")", the while loop is for resolve nested round brackets
        while (mainExpression.contains((int) '(')) {
            for (int i = 0; i < mainExpression.size(); i++) {
                if (mainExpression.get(i) < 128 && (char) (int) (mainExpression.get(i)) == ')') {
                    int j = i;
                    while ((char) (int) (mainExpression.get(j)) != '(')
                        j--;
                    List<Integer> simpleSequence = mainExpression.subList(j + 1, i); // Sublist of the simple expression without parentheses
                    Integer result = simpleExpression(simpleSequence, results, a, b); // Position + 128 of the matrix in results
                    mainExpression.subList(j, j + simpleSequence.size() + 2).clear(); // Clears the simple expression solved
                    mainExpression.add(j, result); // Substitutes the old expression with its result
                }
            }
        }

        // Looks for "]"
        for (int i = 0; i < mainExpression.size(); i++) {
            if (mainExpression.get(i) < 128 && (char) (int) (mainExpression.get(i)) == ']') {
                int j = i;
                while ((char) (int) (mainExpression.get(j)) != '[')
                    j--;
                List<Integer> simpleSequence = mainExpression.subList(j + 1, i);
                Integer result = simpleExpression(simpleSequence, results, a, b);
                mainExpression.subList(j, j + simpleSequence.size() + 2).clear();
                mainExpression.add(j, result);
            }
        }

        // Looks for "}"
        for (int i = 0; i < mainExpression.size(); i++) {
            if (mainExpression.get(i) < 128 && (char) (int) (mainExpression.get(i)) == '}') {
                int j = i;
                while ((char) (int) (mainExpression.get(j)) != '{')
                    j--;
                List<Integer> simpleSequence = mainExpression.subList(j + 1, i);
                Integer result = simpleExpression(simpleSequence, results, a, b);
                mainExpression.subList(j, j + simpleSequence.size() + 2).clear();
                mainExpression.add(j, result);
            }
        }

        // The last step is to resolve a simple expression by now without any parentheses
        Integer r = simpleExpression(mainExpression, results, a, b); // Compute the last result matrix and gets the position
        return results.get(r - 128); // Gets the last result matrix referenced in the solved expression
    }

    /**
     * Solve a simple expression without parentheses.
     *
     * @param originalList An expression without parentheses.
     * @param results      List where result matrices can be memorized.
     * @param a            Any square matrix.
     * @param b            Any square matrix.
     * @return The position + 128 of the matrix result of the input expression.
     * @throws Exception If there are conflicts (expression not valid) or other unexpected Exceptions (i.e. Arithmetic Exception).
     */
    private static Integer simpleExpression(List<Integer> originalList, List<Fraction[][]> results, Fraction[][] a, Fraction[][] b) throws Exception {

        List<Integer> simpleSequence = new ArrayList<>(originalList);

        // If sequence start with a plus or minus sign
        if (simpleSequence.get(0) == '+' || simpleSequence.get(0) == '-')
            simpleSequence = resolveUnaryOperands(originalList, results, a, b);

        // Sequence of just a letter A or B, a scalar or a result matrix memorized in results
        if (simpleSequence.size() == 1) {
            Fraction[][] resultMatrix = getExpressionMatrix(simpleSequence.get(0), results, a, b);
            results.add(resultMatrix);
            return results.indexOf(resultMatrix) + 128; // Returns position + 128 of the result matrix in results
        }

        // Looks for "*"
        for (int i = 0; i < simpleSequence.size(); i++) {
            if ((char) (int) (simpleSequence.get(i)) == '*') {
                List<Integer> binaryOperation = simpleSequence.subList(i - 1, i + 2); // i-1=1^ matrix, i=sign, i+1=2^matrix
                Fraction[][] x = getExpressionMatrix(binaryOperation.get(0), results, a, b); // First matrix
                Fraction[][] y = getExpressionMatrix(binaryOperation.get(2), results, a, b); // Second matrix
                Fraction[][] resultMatrix = BinaryOperations.multiply(x, y);
                results.add(resultMatrix);
                simpleSequence.subList(i - 1, i + 2).clear(); // Removes the old binary operation now solved
                simpleSequence.add(i - 1, results.indexOf(resultMatrix) + 128); // Adds the result
                i--;
            }
        }

        // Looks for "+" or "-"
        for (int i = 0; i < simpleSequence.size(); i++) {
            if ((char) (int) (simpleSequence.get(i)) == '+' || (char) (int) (simpleSequence.get(i)) == '-') {
                List<Integer> binaryOperation = simpleSequence.subList(i - 1, i + 2); // i-1=1^ matrix, i=sign, i+1=2^ matrix
                Fraction[][] x = getExpressionMatrix(binaryOperation.get(0), results, a, b); // First matrix
                Fraction[][] y = getExpressionMatrix(binaryOperation.get(2), results, a, b); // Second matrix
                Fraction[][] resultMatrix;
                if ((char) (int) (binaryOperation.get(1)) == '+')
                    resultMatrix = BinaryOperations.add(x, y);
                else
                    resultMatrix = BinaryOperations.sub(x, y);
                results.add(resultMatrix);
                simpleSequence.subList(i - 1, i + 2).clear(); // Removes the old binary operation now solved
                simpleSequence.add(i - 1, results.indexOf(resultMatrix) + 128); // Adds the result
                i--;
            }
        }
        /*
         * First and only element of the original expression now solved.
         * Can be only a matrix, memorized in results or represented by letter A or B, if it was a scalar it was converted to
         * matrix.
         */
        return simpleSequence.get(0);
    }

    /**
     * Provides the matrix referenced by the integer in the expression.
     *
     * @param operand The Integer representing the operand in the expression.
     * @param results List where result matrices can be memorized.
     * @param a       Any square matrix.
     * @param b       Any square matrix.
     * @return The matrix referenced by the integer in the expression.
     */
    private static Fraction[][] getExpressionMatrix(Integer operand, List<Fraction[][]> results, Fraction[][] a, Fraction[][] b) {
        Fraction[][] matrix;
        if ((char) (int) (operand) == 'A')
            matrix = Arrays.copyOf(a, a.length);
        else if ((char) (int) (operand) == 'B')
            matrix = Arrays.copyOf(b, b.length);
        else if (operand > 0) { // Position in results
            matrix = results.get(operand - 128);
        } else { // Scalar by which multiply (memorized as <0)
            int scalar = -operand;
            // Multiply by a scalar is the same that multiply by identity matrix*scalar
            Fraction[][] tmp = UnaryOperations.getIdentityMatrix(a.length);
            matrix = BinaryOperations.multiplyByScalar(tmp, scalar);
        }
        return matrix;
    }

    /**
     * Checks if the input expression is a valid expression.
     *
     * @param inputExpression Any expression written by the user.
     * @return true if expression is valid, false otherwise.
     */
    public static boolean isExpressionValid(String inputExpression) {

        // Null or void expression are not valid
        if (inputExpression == null || inputExpression.equals(""))
            return false;

        List<Integer> mulSigns = addMulSigns(inputExpression); // Adds missing multiplication signs according to math rules
        String expression = ""; // Builds a string from the List
        for (Integer i : mulSigns) {
            if (i < 0) {
                expression += -i + "";
            } else {
                expression += (char) (int) i + "";
            }
        }

        // Delete parentheses from the string
        String withoutPar = expression.replace("(", "").replace(")", "")
                .replace("[", "").replace("]", "")
                .replace("{", "").replace("}", "");

        // First check (withoutPar): string without parentheses, scalars surrounded from just + or - are not valid
        for (int i = 0; i < withoutPar.length(); i++) {
            char num = withoutPar.charAt(i);
            if (num >= '0' && num <= '9') {
                String n = num + "";
                int j = i + 1; // Finds end of the scalar (j end, i start of scalar)
                while (j < withoutPar.length() && withoutPar.charAt(j) >= '0' && withoutPar.charAt(j) <= '9') {
                    n += withoutPar.charAt(j);
                    j++;
                }
                j--;
                if (i == 0 && withoutPar.length() == 1)
                    return false;
                boolean prevSign = (i != 0 && (withoutPar.charAt(i - 1) == '+' || withoutPar.charAt(i - 1) == '-')); // Sign before scalar
                boolean nextSign = (j != withoutPar.length() - 1 && (withoutPar.charAt(j + 1) == '+' || withoutPar.charAt(j + 1) == '-')); // Sign after scalar
                if (i == 0 && nextSign) { // Scalar at the start of expression followed immediately by a + or -
                    return false;
                } else if (j == withoutPar.length() - 1 && prevSign) { // Scalar at the end of expression comes immediately after a + or -
                    return false;
                } else if (prevSign && nextSign) { // Scalar surrounded by + or -
                    return false;
                }
            }
        }

        // Second check (expression): string with parentheses
        List<Character> parentheses = new ArrayList<>(); // Checks the validity of parentheses. Works as a stack.
        int position = 0; // Represents the alternating sequence of operators and operands
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i); // Symbol to check
            if (c == '{') { // If stack isn't empty or it is in position 1 returns false
                if ((parentheses.size() != 0) || position == 1)
                    return false;
                else {
                    parentheses.add(c); // Adds parenthesis to stack
                    position = 0; // Restores position (operands or parentheses can start)
                }
            } else if (c == '[') { // If stack isn't empty and not follows a curly bracket or it is in position 1 returns false
                if ((parentheses.size() != 0 && parentheses.get(parentheses.size() - 1) != '{') || position == 1)
                    return false;
                else {
                    parentheses.add(c); // Adds parenthesis to stack
                    position = 0; // Restores position (operands or parentheses can start)
                }
            } else if (c == '(') { // If stack isn't empty and not follows a curly or square bracket or it is in position 1 returns false
                if ((parentheses.size() != 0 && parentheses.get(parentheses.size() - 1) != '{' && parentheses.get(parentheses.size() - 1) != '[' && parentheses.get(parentheses.size() - 1) != '(') || position == 1)
                    return false;
                else {
                    parentheses.add(c); // Adds parenthesis to stack
                    position = 0; // Restores position (operands or parentheses can start)
                }
            } else if (c == ')') { // If stack is empty or the bracket before doesn't match.
                if ((parentheses.size() == 0 || parentheses.get(parentheses.size() - 1) != '('))
                    return false;
                else {
                    parentheses.remove(parentheses.size() - 1); // Removes the correspondent bracket from stack
                    position = 1; // Position is set to 1 (operators can start)
                }
            } else if (c == ']') { // If stack is empty or the bracket before doesn't match.
                if ((parentheses.size() == 0 || parentheses.get(parentheses.size() - 1) != '['))
                    return false;
                else {
                    parentheses.remove(parentheses.size() - 1); // Removes the correspondent bracket from stack
                    position = 1; // Position is set to 1 (operators can start)
                }
            } else if (c == '}') { // If stack is empty or the bracket before doesn't match.
                if ((parentheses.size() == 0 || parentheses.get(parentheses.size() - 1) != '{'))
                    return false;
                else {
                    parentheses.remove(parentheses.size() - 1); // Removes the correspondent bracket from stack
                    position = 1; // Position is set to 1 (operators can start)
                }
            } else if (c == 'A' || c == 'B') {
                if (position == 1) // Not valid if position is 1 (spot for operators)
                    return false;
                else {
                    position = 1; // Increases position by 1 (an operator must follow)
                }
            } else if (c == '*') { // Not valid if position is 0 (spot for operand) or it is at the end of expression
                if (position == 0 || i == expression.length() - 1)
                    return false;
                else {
                    position = 0; // Increases position by 1 (an operand must follow)
                }
            } else if (c == '+' || c == '-') {
                if (i == 0 || expression.charAt(i - 1) == '(' || expression.charAt(i - 1) == '[' || expression.charAt(i - 1) == '{')
                    position = 1;
                /*
                 * + and - can be valid if:
                 * 1) it is at the start or it follows a bracket
                 * 2) it follows an operand
                 * If it is at the start or it follows a bracket (unary operator), it is valid and as the actual position would be 0,
                 * to make it a valid position is restored to 1.
                 * If it will be valid in the following condition, position will increase by 1: if pos is now set to 1, it will pass
                 * conditions and it will become 0 (a operand can follow).
                 */
                if (position == 0 || i == expression.length() - 1) // If odd position or at the end, it is not valid
                    return false;
                else {
                    position = 0; // Increases position by 1 (an operand must follow)
                }
            } else if (c >= '0' && c <= '9') { // Not valid if position is odd (spot for operators)
                if (position == 1)
                    return false;
                else { // If pos valid, must check if another number doesn't follow, otherwise position won't increase (another n can follow)
                    if (i != expression.length() - 1 && (expression.charAt(i + 1) < '0' || expression.charAt(i + 1) > '9'))
                        position = 1;
                }
            } else { // Any other not valid symbol
                return false;
            }
        }
        return parentheses.size() == 0; // Check if the stack is empty, otherwise returns false
    }
}

/*
// To debug
List<Character> p = new ArrayList<>();
for (int i = 0; i < mainExpression.size(); i++) {
    if (mainExpression.get(i) >= 128)
        p.add('O');
        else if (mainExpression.get(i) < 0)
            p.add((char) ((-mainExpression.get(i)) + '0'));
        else
            p.add((char) (int) (mainExpression.get(i)));
        }
System.err.println(p);
*/