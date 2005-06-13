/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.io.*;
import java.util.regex.*;

/**
 * Static utilities to deal with roman numbers, and non static functions to transform strings
 * representing decimal numbers to roman numbers and back.
 *
 * @author Michiel Meeuwissen 
 * @since MMBase-1.8
 */

public class RomanTransformer extends StringTransformer {

    public static Pattern NUMERIC = Pattern.compile("\\d+");
    public static Pattern ROMAN   = Pattern.compile("(?i)[ivxlcdm]+");


    /**
     * Constants for roman numbers
     */
    public final static int I = 1, V = 5, X = 10, L = 50, C = 100, D = 500, M = 1000;

    /**
     * Converts one of the letters from the roman number system to an int.
     * @return <code>0</code> if could not be converted
     */
    public static int romanToDecimal(char r) {
        if (r == 'i') return I;
        if (r == 'v') return V;
        if (r == 'x') return X;
        if (r == 'l') return L;
        if (r == 'c') return C;
        if (r == 'd') return D;
        if (r == 'm') return M;
        return 0;
    }
    /**
     * Converts an integer to on the letters of the roman number system, or ' ' if no such number.
     * @see #decimalToRoman(int)
     */
    
    public static char decimalToRomanDigit(int i) {
        switch(i) {
        case M: return 'm';
        case D: return 'd';
        case C: return 'c';
        case L: return 'l';
        case X: return 'x';
        case V: return 'v';
        case I: return 'i';
        default: return ' ';
        }
    }
    
    /**
     * Converts roman number to int.
     */
     public static int romanToDecimal(String roman) {
        roman = roman.toLowerCase();
        int tot = 0;
        int mode = I;
        for (int i = roman.length() - 1; i >= 0 ; i--) {
            int value = romanToDecimal(roman.charAt(i));

            if (value > mode) mode = value;
            if (value < mode) {
                tot -= value;
            } else {
                tot += value;
            }
        }
            
        return tot;
    }
    /** 
     * Converts int to roman nunmber (if bigger than 0, smaller then 4000), other wise return the
     * integer as a string.
    */
    public static String decimalToRoman(int value) {
        if (value < 1 || value > 3999) {
            // throw new IllegalArgumentException("Only natural numbers smaller than 4000 can be
            // present in roman number");
            return "" + value;
        } 
        final StringBuffer buf = new StringBuffer();
        int mode = M;
        while (value > 0) {
            while (value < mode) mode /= 10;
            if (value >= 9 * mode && mode < M) {
                buf.append(decimalToRomanDigit(mode));
                buf.append(decimalToRomanDigit(mode * 10));
                value -= 9 * mode;
                continue;
            }
            if (value >= 4 * mode && mode < M) {
                if (value < 5 * mode) {
                    buf.append(decimalToRomanDigit(mode));
                    value += mode;
                } 
                buf.append(decimalToRomanDigit(5 * mode));
                value -= 5 * mode;
            }
            while (value >= mode) {
                buf.append(decimalToRomanDigit(mode));
                value -= mode;
            }
        }
        return buf.toString();
    }


    // javadoc inherited
    public  String transform(String r) {
        try {
            int i = Integer.parseInt(r);
            return decimalToRoman(i);
        } catch (Exception e) {
            return r;
        }
    }
        
    public String transformBack(String r) {
        return "" + romanToDecimal(r);
    }

    // javadoc inherited
    /** 
     * Just to test
     */
    public static void main(String argv[]) {
        if (argv.length == 0) {
            System.out.println("Use roman or decimal argument");
            return;
        }    
        if (argv.length == 1) {
            if (NUMERIC.matcher(argv[0]).matches()) {
                System.out.println(decimalToRoman(Integer.parseInt(argv[0])));
            } else {
                System.out.println(romanToDecimal(argv[0]));
            }
        }
    }

}
