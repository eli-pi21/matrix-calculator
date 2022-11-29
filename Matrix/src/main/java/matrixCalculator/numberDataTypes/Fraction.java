package matrixCalculator.numberDataTypes;

import java.util.Objects;

/**
 * Simulates math fractions manipulating numerator and denominator.
 * Once converted, works also with double and integer.
 * Every methods throws an exception if an overflow occurs.
 */

public class Fraction {

    /**
     * Fraction's numerator.
     */
    private final int numerator;
    /**
     * Fraction's denominator.
     */
    private final int denominator;

    /**
     * Creates a Fraction Object.
     *
     * @param numerator   Any Integer
     * @param denominator Any Integer (if denominator is 0 or null it is converted to 1, if numerator is null, it is converted to 0)
     */
    public Fraction(Integer numerator, Integer denominator) {
        if (numerator == null)
            numerator = 0;
        if (denominator == null || denominator == 0)
            denominator = 1;
        this.numerator = numerator;
        this.denominator = denominator;
    }

    /**
     * @return Fraction numerator.
     */
    public int getNumerator() {
        return this.numerator;
    }

    /**
     * @return Fraction denominator.
     */
    public int getDenominator() {
        return this.denominator;
    }

    /**
     * Multiplies two fractions.
     *
     * @param x Any non null fraction.
     * @param y Any non null fraction.
     * @return The product of the two fractions reduced to the lowest terms.
     * @throws ArithmeticException If an overflow occurs.
     */
    public static Fraction multiply(Fraction x, Fraction y) throws ArithmeticException {
        Objects.requireNonNull(x);
        Objects.requireNonNull(y);
        int n = Math.multiplyExact(x.getNumerator(), y.getNumerator());
        int d = Math.multiplyExact(x.getDenominator(), y.getDenominator());
        Fraction f = new Fraction(n, d);
        return f.toLowestTerms();
    }

    /**
     * Multiplies any number of fractions.
     *
     * @param fractions Any number of fraction.
     * @return The product of the fractions reduced to the lowest terms.
     * @throws ArithmeticException If an overflow occurs.
     */
    public static Fraction multiply(Fraction... fractions) throws ArithmeticException {
        Fraction f = new Fraction(1, 1);
        for (Fraction n : fractions) {
            f = Fraction.multiply(f, n);
        }
        return f.toLowestTerms();
    }

    /**
     * Divides a fraction by another.
     *
     * @param x Any non null fraction.
     * @param y Any non null fraction.
     * @return The division of the first fraction by the second.
     * @throws ArithmeticException If an overflow occurs.
     */
    public static Fraction divide(Fraction x, Fraction y) throws ArithmeticException {
        Objects.requireNonNull(x);
        Objects.requireNonNull(y);
        int n = Math.multiplyExact(x.getNumerator(), y.getDenominator()); // Num and den of the second fraction are switched
        int d = Math.multiplyExact(x.getDenominator(), y.getNumerator());
        Fraction f = new Fraction(n, d);
        return f.toLowestTerms();
    }

    /**
     * Adds two fraction.
     *
     * @param x Any non null fraction.
     * @param y Any non null fraction.
     * @return The addition of the two fractions reduced to the lowest terms.
     * @throws ArithmeticException If an overflow occurs.
     */
    public static Fraction add(Fraction x, Fraction y) throws ArithmeticException {
        Objects.requireNonNull(x);
        Objects.requireNonNull(y);
        int lcd = Math.multiplyExact(x.denominator, y.denominator); // Lowest common denominator
        int xTmp = Math.multiplyExact((lcd / x.denominator), x.numerator);
        int yTmp = Math.multiplyExact((lcd / y.denominator), y.numerator);
        int n = Math.addExact(xTmp, yTmp);
        Fraction f = new Fraction(n, lcd);
        return f.toLowestTerms();
    }

    /**
     * Adds any number of fractions.
     *
     * @param fractions Any number of fraction.
     * @return The addition of the fractions reduced to the lowest terms.
     * @throws ArithmeticException If an overflow occurs.
     */
    public static Fraction add(Fraction... fractions) throws ArithmeticException {
        Fraction f = new Fraction(0, 1);
        for (Fraction n : fractions) {
            Fraction x = Fraction.add(f, n);
            f = x;
        }
        return f.toLowestTerms();
    }

    /**
     * Subtracts one fraction from another.
     *
     * @param x Any non null fraction.
     * @param y Any non null fraction.
     * @return The subtraction of the second fraction from the first reduced to the lowest terms.
     * @throws ArithmeticException If an overflow occurs.
     */
    public static Fraction sub(Fraction x, Fraction y) throws ArithmeticException {
        Objects.requireNonNull(x);
        Objects.requireNonNull(y);
        return add(x, y.negative());
    }

    /**
     * Raises a fraction to a power.
     *
     * @param x Any non null fraction.
     * @param y The power to which raise the fraction.
     * @return The fraction raised to the input power reduced to the lowest terms.
     * @throws ArithmeticException If an overflow occurs.
     */
    public static Fraction pow(Fraction x, int y) throws ArithmeticException {
        Objects.requireNonNull(x);
        if (y >= 0) { // If power is positive
            int n = powExact(x.numerator, y);
            int d = powExact(x.denominator, y);
            Fraction f = new Fraction(n, d);
            return f.toLowestTerms();
        } else { // If power is negative must be raised to the power the fraction inverse
            y = Math.abs(y); // y becomes positive
            int n = powExact(x.denominator, y); // Denominator becomes numerator
            int d = powExact(x.numerator, y); // Numerator becomes denominator
            Fraction f = new Fraction(n, d);
            return f.toLowestTerms();
        }
    }

    /**
     * Raises an integer to a power.
     *
     * @param x Any integer.
     * @param y Any integer.
     * @return The integer raised to the input power.
     * @throws ArithmeticException If an overflow occurs.
     */
    private static int powExact(int x, int y) {
        long pow = (long) Math.pow(x, y);
        if (pow > Integer.MAX_VALUE)
            throw new ArithmeticException();
        return (int) pow;
    }

    /**
     * Converts an Integer in the correspondent Fraction Object.
     *
     * @param n Any non null Integer.
     * @return The input Integer converted to Fraction reduced to the lowest terms.
     */
    public static Fraction toFraction(Integer n) {
        Objects.requireNonNull(n);
        return new Fraction(n, 1);
    }

    /**
     * Converts a Double in the correspondent Fraction Object.
     *
     * @param n Any non null Double.
     * @return The input Double converted to Fraction reduced to the lowest terms.
     */
    public static Fraction toFraction(Double n) {
        Objects.requireNonNull(n);
        String s = Double.toString(Math.abs(n));
        int lengthDec = s.length() - (Integer.toString((int) (double) n).length() + 1); // Counts the decimal digits
        int den = (int) Math.pow(10, lengthDec); // Denominator a power of 10 according to math rules
        int num = (int) (n * den); // Makes the double an integer numerator (divided by denominator gives the input number)
        Fraction f = new Fraction(num, den);
        return f.toLowestTerms();
    }

    /**
     * Computes the inverse of the calling Fraction.
     *
     * @return The inverse of the calling Fraction.
     */
    public Fraction getInverse() {
        int d = this.numerator;
        int n = this.denominator;
        Fraction f = new Fraction(n, d);
        return f.toLowestTerms();
    }

    /**
     * Provides the calling Fraction to the lowest terms.
     *
     * @return The calling Fraction reduced to the lowest terms.
     */
    public Fraction toLowestTerms() {
        // Works with the positive fraction, at the end adds the sign
        int absNum = Math.abs(this.numerator); // Numerator absolute value
        int absDen = Math.abs(this.denominator); // Denominator absolute value
        int divider = 2; // Start divider to find
        int minNum = absNum / 2; // Minimum number for the numerator to looks for a divider (divider can't be greater than dividend's half)
        int minDen = absDen / 2;
        while ((minNum >= divider) || (minDen >= divider)) {
            int numAct = absNum % divider; // Possible result of the division ("actual" numerator)
            int denAct = absDen % divider;
            if ((numAct == 0) && (denAct == 0)) { // Numerator and denominator are divisible by the actual divider
                absNum = absNum / divider;
                absDen = absDen / divider;
                // Divider isn't increased as fraction could be divided again by the same number
            } else {
                ++divider; // Looks for another divider
            }
        }
        Fraction f;
        if (this.isNegative()) // Adds sign
            f = new Fraction(-absNum, absDen);
        else
            f = new Fraction(absNum, absDen);
        return f;
    }

    /**
     * Computes the correspondent double value of calling Fraction.
     *
     * @return Correspondent double value of calling Fraction.
     */
    public double toDouble() {
        return (double) this.numerator / (double) this.denominator;
    }

    /**
     * Computes the correspondent negative value of calling Fraction.
     *
     * @return Correspondent negative value of calling Fraction.
     */
    public Fraction negative() {
        return new Fraction(-this.numerator, this.denominator);
    }

    /**
     * Checks if calling Fraction is negative.
     *
     * @return true if calling Fraction is negative, false otherwise.
     */
    public boolean isNegative() {
        return (this.numerator > 0 && this.denominator < 0) || (this.numerator < 0 && this.denominator > 0);
    }

    /**
     * Checks if calling Fraction can be seen just as a Fraction and not an Integer. If Fraction denominator is 1, it
     * will be considered as Integer.
     *
     * @return true if calling Fraction can be seen just as s Fraction, false otherwise.
     */
    public boolean isFraction() {
        return this.denominator != 1 && this.denominator != -1 && this.numerator != 0;
    }

    /**
     * Creates an exact copy of the input Fraction.
     *
     * @param fraction Any Fraction.
     * @return A copy of the input Fraction.
     */
    public static Fraction copyOf(Fraction fraction) {
        if (fraction == null)
            return null;
        return new Fraction(fraction.numerator, fraction.denominator);
    }

    /**
     * Converts the calling Fraction in Latex form.
     *
     * @return Calling Fraction in Latex form.
     */
    public String toLatex() {
        if (this.denominator == 1 || this.denominator == -1) { // Can be seen as an integer
            if (this.isNegative())
                return Integer.toString(-Math.abs(this.numerator));
            else
                return Integer.toString(Math.abs(this.numerator));
        } else if (this.numerator == 0) // Is 0
            return Integer.toString(0);
        else { // Is only a fraction
            String fraction = "\\frac{" + Math.abs(this.numerator) + "}{" + Math.abs(this.denominator) + "}";
            if (this.isNegative())
                return "-" + fraction;
            else
                return fraction;
        }
    }

    /**
     * Compares two objects. Can compares the calling Fraction even with Integers or Double.
     * If the correspondent Fraction form matches, returns true.
     * 3 condition: same absolute numerators, same absolute denominators, both negative or positive.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof Integer) && !(o instanceof Double) && !(o instanceof Fraction)) return false;
        Fraction fraction;
        if (o instanceof Integer) { // Convert Integer to Fraction
            fraction = toFraction((Integer) o);
        } else if (o instanceof Double) { // Convert the Double to Fraction
            fraction = toFraction((Double) o);
        } else {
            fraction = (Fraction) o;
        }
        if (fraction.numerator == 0 && this.numerator == 0) // If both numerator equals 0, returns true
            return true;
        return Math.abs(fraction.toLowestTerms().numerator) == Math.abs(this.toLowestTerms().numerator) && Math.abs(fraction.toLowestTerms().denominator) == Math.abs(this.toLowestTerms().denominator) && (this.isNegative() == fraction.isNegative());
    }

    /**
     * Follows the equals() method. The Fraction is hashed according to absolute numerator, absolute denominator, sign.
     */
    @Override
    public int hashCode() {
        int hashCode;
        Fraction f = this.toLowestTerms();
        int n = Math.abs(f.numerator);
        int d = Math.abs(f.denominator);
        if (n == 0)
            hashCode = Objects.hash(0);
        else
            hashCode = Objects.hash(n, d, f.isNegative());
        return hashCode;
    }

    /**
     * @return Calling Fraction to String.
     */
    @Override
    public String toString() {
        int n;
        int d;
        n = Math.abs(this.numerator);
        d = Math.abs(this.denominator);
        if (n == 0) // Return just 0
            return Integer.toString(0);
        else if (d == 1) { // Returns an Integer
            if (this.isNegative())
                return "-" + n;
            else
                return Integer.toString(n);
        } else { // Puts the sign just before the denominator
            String fraction = n + "/" + d;
            if (this.isNegative())
                return "-" + fraction;
            else
                return fraction;
        }
    }
}