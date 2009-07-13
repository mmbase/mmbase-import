/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;
import java.text.*;
import java.util.regex.*;
import java.math.*;

/**
 * A processor that gets a number as a file-size, that is, rounded with kbytes and Mb's and so on.
 *
 * Actually, using setters like {@link FormatQuantity#setUnit(String)}, this class can also be used to postfix all
 * other kinds of units to integers.
 *

 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9
 */

public class FormatQuantity implements Processor {

    private static final long serialVersionUID = 1L;
    protected static final BigDecimal KILO     = new BigDecimal(1000); // 10^3
    protected static final BigDecimal KIBI     = new BigDecimal(1024); // 2^10

    //                                              3     6         9     12    15    18    21    24
    //                                              1     2         3     4     5     6     7     8
    protected static final String[] IEEE_BI     = {"Ki", "Mi",     "Gi", "Ti", "Pi", "Ei", "Zi", "Yi"}; // 1024,  1024^2,
    protected static final String[] SI          = {"k",  "M",      "G",  "T",  "P",  "E",  "Z",  "Y"};  // 1000, 1000000
    protected static final String[] SI_NEGATIVE = {"m",  "\u00b5", "n",  "p",  "f",  "a",  "z",  "y"};  // 1/1000, 1/1000000

    protected BigDecimal      k        = KILO;
    protected String[] prefixes = SI;
    protected String unit = "";
    protected String lowFormat = "0.0 ";
    protected String highFormat = "0 ";
    protected BigDecimal lowLimit = new BigDecimal(15);
    protected BigDecimal limit = k.multiply(new BigDecimal(2));

    /**
     * If  set, will use binary prefixes as recommended by <a href="http://en.wikipedia.org/wiki/IEEE_1541-2002">IEEE 1541</a>. So, Ki, Mi, etc. which
     * are multiples of 1024. Otherwise normal SI prefixes are applied (k, M, G etc), which are multiples of
     * 1000.
     */
    public void setBinaryPrefixes(boolean bi) {
        if (bi) {
            prefixes = IEEE_BI;
            k = KIBI;
        } else {
            prefixes = SI;
            k = KILO;
        }
    }

    /**
     * The unit symbol which is prefixed by the prefixes. No unit on default.
     */
    public void setUnit(String u) {
        unit = u;
    }
    public String getUnit() {
        return unit;
    }

    /**
     * MessageFormat to format the number. Defaults to '0 '.
     * @since MMBase-1.9
     *
     */
    public void setFormat(String mf) {
        if (mf == null) throw new IllegalArgumentException();
        lowFormat = mf;
        highFormat = mf;
    }
    public String getFormat() {
        return lowFormat;
    }

    public void setHighFormat(String mf) {
        if (mf == null) throw new IllegalArgumentException();
        highFormat = mf;
    }
    public String getHighFormat() {
        return highFormat;
    }

    public void setLowFormat(String mf) {
        if (mf == null) throw new IllegalArgumentException();
        highFormat = mf;
    }
    public String getLowFormat() {
        return lowFormat;
    }

    /**
     * How big size / prefix must be to enter realm of next bigger prefix.
     * @since MMBase-1.9
     */
    public void setLimit(int l) {
        limit = new BigDecimal(l);
    }

    /**
     * For low numbers, sometimes a bit different pattern is desired.
     * E.g. for number until 20 or so, you may want to show a decimal place.
     * (14.3 KiB vs 64 KiB).
     */
    public void setLowLimit(int l) {
        lowLimit = new BigDecimal(l);
    }


    public  Object process(Node node, Field field, Object value) {
        if (value == null) return null;

        BigDecimal v = org.mmbase.util.Casting.toDecimal(value);
        BigDecimal av = v.abs();
        BigDecimal factor = BigDecimal.ONE;
        int power  = 0;
        if (av.unscaledValue().intValue() != 0) {
            if (av.compareTo(BigDecimal.ONE) > 0) {
                while (av.compareTo(factor.multiply(limit)) > 0
                       && power < prefixes.length) {
                    factor =  factor.multiply(k);
                    power++;
                }
            } else {
                BigDecimal inverse = BigDecimal.ONE.divide(av, RoundingMode.HALF_UP);
                while (inverse.compareTo(factor.multiply(limit)) > 0
                       && -power < SI_NEGATIVE.length) {
                    factor =  factor.multiply(KILO);
                    power--;
                }
                factor =  factor.multiply(KILO);
                power--;
            }
        }

        NumberFormat nf = NumberFormat.getInstance(node == null ?
                                                   org.mmbase.util.LocalizedString.getDefault() :
                                                   node.getCloud().getLocale());

        if (power > 0) {
            v = v.divide(factor, 0, RoundingMode.HALF_UP);
        }  else {
            v = v.multiply(factor);
            //v.setScale(0, RoundingMode.HALF_UP);

        }

        if (nf instanceof DecimalFormat) {
            av = v.abs();
            ((DecimalFormat) nf).applyPattern(av.compareTo(lowLimit) > 0 ? highFormat : lowFormat);
        }

        StringBuffer buf = nf.format(v.doubleValue(), new StringBuffer(), new FieldPosition(0));
        if (power > 0) {
            buf.append(prefixes[power - 1]);
        } else if (power < 0) {
            buf.append(SI_NEGATIVE[-1 - power]);
        }
        buf.append(unit);
        return buf.toString();
    }

    public Parser getParser() {
        Parser parser = new Parser();
        parser.k = k;
        parser.prefixes = prefixes;
        return parser;
    }

    @Override
    public String toString() {
        return "[" + unit + "]";
    }


    public static final String PREFIX_PATTERN = "(Ki|Mi|Ti|Gi|Pi|Ei|Zi|Yi|da|[hkMGTPEZYcdm\u00b5npfazy])";
    private static final Pattern PARSER = Pattern.compile("([0-9]+[\\.,]?[0-9]*)\\s?(" + PREFIX_PATTERN + ")?.*");

    public static class Parser extends FormatQuantity {
        private static final long serialVersionUID = 1923784124279730073L;

        protected BigDecimal factor(String prefix) {
            for (int i = 0 ; i < prefixes.length; i++) {
                if (prefixes[i].equals(prefix)) {
                    return k.pow(i + 1);
                }
            }
            if (prefixes != IEEE_BI) {
                for (int i = 0 ; i < IEEE_BI.length; i++) {
                    if (IEEE_BI[i].equals(prefix)) {
                        return KIBI.pow(i + 1);
                    }
                }
            }
            if (prefixes != SI) {
                for (int i = 0 ; i < SI.length; i++) {
                    if (SI[i].equals(prefix)) {
                        return KILO.pow(i + 1);
                    }
                }
            }
            for (int i = 0 ; i < SI_NEGATIVE.length; i++) {
                if (SI_NEGATIVE[i].equals(prefix)) {
                    return BigDecimal.ONE.divide(KILO.pow(i + 1));
                }
            }
            // support for remaining SI prefixes (no powers of 1000)
            if (prefix.equals("da")) {
                return new BigDecimal(10);
            } else if (prefix.equals("h")) {
                return new BigDecimal(100);
            } else if (prefix.equals("d")) {
                return BigDecimal.ONE.divide(new BigDecimal(10));
            } else if (prefix.equals("c")) {
                return BigDecimal.ONE.divide(new BigDecimal(100));
            }
            return BigDecimal.ONE;
        }


        @Override
        public final Object process(Node node, Field field, Object value) {
            if (value == null) return null;
            String string = (String) value;
            if (string.endsWith(unit)) {
                string = string.substring(0, string.length() - unit.length());
            }
            Matcher matcher = PARSER.matcher(string);
            String number;
            BigDecimal factor;
            if (matcher.matches()) {
                number = matcher.group(1).trim();
                String prefix = matcher.group(2);
                if (prefix != null) {
                    factor = factor(prefix);
                } else {
                    factor = BigDecimal.ONE;
                }
            } else {
                number = string.trim();
                factor = BigDecimal.ONE;
            }

            BigDecimal dec = new BigDecimal(number);
            MathContext context  = new MathContext(dec.precision());
            return dec.multiply(factor).round(context);
        }
    }


    public static void main(String[] argv) {
        FormatQuantity format = new FormatQuantity();
        FormatQuantity fileSize = new FormatFileSize();

        Parser parser = new Parser();
        BigDecimal in = (BigDecimal) parser.process(null, null, argv[0]);

        String formatted = "" + format.process(null, null, in);
        String formattedAsFileSize = "" + fileSize.process(null, null, in);


        //String parsed = "" + parser.process(null, null, formatted);
        //String parsedFileSize = "" + fsparser.process(null, null, formattedAsFileSize);
        System.out.println("" + argv[0] + " -> '" + formatted + "', '" + formattedAsFileSize + "'");
        System.out.println("-> " + parser.process(null, null, formatted) + "," + parser.process(null, null, formattedAsFileSize));

    }
}


