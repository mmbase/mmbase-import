/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.math;
import java.text.*;
import java.util.*;

/**
 * Represents a set of measurement values. The value represents the average value.
 * {@link #toString} presnent the current value, but only the relevant digits. The standard
 * deviation {@link #getStandardDeviation} is used to determin what digits are relevant.
 *
 * @author Michiel Meeuwissen
 * @since  mm-statistics-1.0
 * @version $Id: Measurement.java,v 1.3 2008-10-15 11:45:51 michiel Exp $
 */


public class Measurement extends java.lang.Number {


    private double sum = 0;
    private double squareSum = 0;
    private int count = 0;
    private int minimumExponent = 4;

    public Measurement() {
    }
    protected Measurement(double sum, double squareSum, int count) {
        this.sum = sum;
        this.squareSum = squareSum;
        this.count = count;
    }

    /**
     * Enters a new value.
     */
    public Measurement enter(double d) {
        sum += d;
        squareSum += d * d;
        count++;
        return this;
    }

    /**
     * Assuming that the measurement <code>m</code> is from the same set, add it to the already existing
     * statistics.
     * See also {@link #add(Measurement)} which is something entirely different.
     * @param d
     */
    public Measurement enter(Measurement m) {
        sum += m.sum;
        squareSum += m.squareSum;
        count += m.count;
        return this;
    }

    public double getMean() {
        return sum / count;
    }


    @Override public double doubleValue() {
        return getMean();
    }

    @Override public long longValue() {
        return (long) doubleValue();
    }
    @Override public int intValue() {
        return (int) doubleValue();
    }
    @Override public float floatValue() {
        return (float) doubleValue();
    }

    @Override public byte byteValue() {
        return (byte) doubleValue();
    }
    @Override public short shortValue() {
        return (short) doubleValue();
    }


    public double getStandardDeviation() {
        double mean = getMean();
        return Math.sqrt(squareSum / count - mean * mean);
    }

    public int getCount() {
        return count;
    }

    /**
     * Operator overloading would be very handy here, but java sucks.
     */
    public Measurement div(double d) {
        return new Measurement(sum / d, squareSum / (d * d), count);
    }
    public Measurement times(double d) {
        return new Measurement(sum * d, squareSum * (d * d), count);
    }

    public Measurement add(double d) {
        return new Measurement(sum + d * count, squareSum + d * d * count + 2 * sum * d, count);
    }

    /**
     * Assuming that this measurement is from a different set (the mean is <em>principally
     * different</em>)
     */
    public Measurement add(Measurement m) {
        // think about this...
        return new Measurement(m.count * sum + count + m.sum, /* err */ 0, count * m.count);
    }

    private static NumberFormat SCIENTIFIC = new DecimalFormat("0.###############E0", new DecimalFormatSymbols(Locale.US));

    /**
     * Returns 10 to the power i, a utility in java.lang.Math for that lacks.
     */
    public static double pow10(int i) {
        double result = 1;
        while (i > 0) {
            result *= 10;
            i--;
        }
        while (i < 0) {
            result /= 10;
            i++;
        }
        return result;
    }
    /**
     * Returns an integer in 'superscript' notation, using unicode.
     */
    private static String superscript(int i) {
        StringBuilder bul = new StringBuilder();
        boolean minus = false;
        if (i < 0) {
            minus = true;
            i = -1 * i;
        }
        if (i == 0) {
            bul.insert(0, Character.toChars(0x2070));
        }
        while (i > 0) {
            int j = i % 10;
            i /= 10;
            bul.insert(0, Character.toChars(0x2070 + j)[0]);
        }
        if (minus) bul.insert(0, "\u207B");

        return bul.toString();

    }
    public void setMinimumExponent(int m) {
        minimumExponent = m;
    }


    /**
     * Split a double up in 2 numbers, a double approximeately 1 (the 'coefficent'), and an integer
     * indicating the order of magnitude (the 'exponent').
     *
     */
    private static  class SplitNumber {
        public double coefficient;
        public int   exponent;
        public SplitNumber(double in) {
            try {
                String[] sStd  = SCIENTIFIC.format(in).split("E");
                coefficient = Double.valueOf(sStd[0]);
                exponent = Integer.valueOf(sStd[1]);
            } catch (Exception e) {
                coefficient = in;
                exponent = 0;
            }
        }
        public String toString() {
            return coefficient + "\u00B710" + superscript(exponent);
            //return coefficient + "E" + exponent;
        }

    }

    /**
     * A crude order of magnitude implemention
     */
    private int log10(double d) {
        int result = 0;
        while (d > 1) {
            d /= 10;
            result++;
        }
        while (d < 0.1) {
            d *= 10;
            result--;
        }
        return result;
    }


    /**
     * Represents the mean value in a scientific notation (using unciode characters).
     * The value of the standard deviation is used to determin how many digits can sensibly be shown.
     */
    @Override public String toString() {

        SplitNumber std = new SplitNumber(getStandardDeviation());
        SplitNumber mean = new SplitNumber(getMean());

        // use difference of order of magnitude of std to determin how mean digits of the mean are
        // relevant
        int magnitudeDifference = mean.exponent - std.exponent;
        //System.out.println("Md: " + mean + " " + std + magnitudeDifference);
        int meanDigits = Math.max(0, Math.abs(magnitudeDifference)) + 1;


        // for std starting with '1' we allow an extra digit.
        if (std.coefficient < 2) {
            meanDigits++;
        }

        //System.out.println("number of relevant digits " + meanDigits + " (" + std + ")");


        // The exponent of the mean is leading, so we simply justify the 'coefficient' of std to
        // match the exponent of mean.
        std.coefficient /= pow10(magnitudeDifference);


        // For numbers close to 1, we don't use scientific notation.
        if (Math.abs(mean.exponent) < minimumExponent) {
            double pow = pow10(mean.exponent);
            mean.exponent = 0;
            mean.coefficient *= pow;
            std.coefficient  *= pow;

        }
        //System.out.println("me: " + mean);


        boolean useE = mean.exponent != 0;

        NumberFormat nf = NumberFormat.getInstance(Locale.US);
        int fd = meanDigits -  log10(mean.coefficient);
        //System.out.println(meanDigits + " -> " + mean + " -> " + fd);
        nf.setMaximumFractionDigits(fd);
        nf.setMinimumFractionDigits(fd);
        nf.setGroupingUsed(false);
        return
            (useE ? "(" : "") +
            nf.format(mean.coefficient) +
            " \u00B1 " + /* +/- */
            nf.format(std.coefficient) +
            (useE ?
             (")\u00B710" + /* .10 */
              superscript(mean.exponent))
             : "");
    }

    public static void main(String[] argv) {
        Measurement measurement = new Measurement();
        for (String arg : argv) {
            measurement.enter(Double.valueOf(arg));
        }
        System.out.println(measurement);
    }
}



