/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;
import java.text.*;

/**
 * A processor that gets a number as a file-size, that is, rounded with kbytes and Mb's and so on.
 *
 * Actually, using setters like {@link FormatQuantity#setUnit(String)}, this class can also be used to postfix all
 * other kinds of units to integers.
 *
 * @todo Why not apply this to floats too. Also support SI prefixes below k then (c, m, micro, n, etc).
 *
 * @author Michiel Meeuwissen
 * @version $Id: FormatQuantity.java,v 1.2 2008-03-25 21:00:25 nklasens Exp $
 * @since MMBase-1.9
 */

public class FormatQuantity implements Processor {

    private static final long serialVersionUID = 1L;
    protected static final int KILO     = 1000; // 10^3
    protected static final int KIBI     = 1024; // 2^10

    public static final String PREFIX_PATTERN = "([kMGTPEZYm\u00b5npfazycdh]|da|Ki|Mi|Gi|Ti|Pi|Ei|Zi|Yi)";
    //                                              3     6         9     12    15    18    21    24
    //                                              1     2         3     4     5     6     7     8
    protected static final String[] IEEE_BI     = {"Ki", "Mi",     "Gi", "Ti", "Pi", "Ei", "Zi", "Yi"}; // 1024
    protected static final String[] SI          = {"k",  "M",      "G",  "T",  "P",  "E",  "Z",  "Y"};  // 10, 1000
    protected static final String[] SI_NEGATIVE = {"m",  "\u00b5", "n",  "p",  "f",  "a",  "z",  "y"};  // 1/10, 1/1000

    protected int      k        = KILO;
    protected String[] prefixes = SI;
    protected String unit = "";
    protected String lowFormat = "0.0 ";
    protected String highFormat = "0 ";
    protected int lowLimit = 15;
    protected int limit= 2 * k;

    /**
     * If  set, will use binary prefixes as recommended by IEEE 1541 . So, Ki, Mi, etc. which
     * are multiples of 1024. Otherwise normal SI prefixes are applied (k, M, G etc), which are multiples of
     * 1000.
     * @since MMBase-1.9
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
     * The unit symbol which is prefixed by the prefixes. Defaults to 'B', the IEEE 1541 recommended
     * symbol for a 'byte'.
     * @since MMBase-1.9
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
        limit = l;
    }

    /**
     * For low numbers, sometimes a bit different pattern is desired.
     * E.g. for number until 20 or so, you may want to show a decimal place.
     * (14.3 KiB vs 64 KiB).
     */
    public void setLowLimit(int l) {
        lowLimit = l;
    }


    public final Object process(Node node, Field field, Object value) {
        if (value == null) return null;

        double v = org.mmbase.util.Casting.toDouble(value);
        double av = v < 0 ? - v : v;
        int factor = 1;
        int power  = 0;
        if (av != 0.0) {
            if (av >= 1.0) {
                while (av > factor * limit && power < prefixes.length) {
                    factor *= k;
                    power++;
                }
            } else {
                double iv = 1.0 / av;
                while (iv > factor * limit && -power < SI_NEGATIVE.length) {
                    factor *= KILO;
                    power--;
                }
            }
        }

        NumberFormat nf = NumberFormat.getInstance(node == null ?
                                                   ContextProvider.getDefaultCloudContext().getDefaultLocale() :
                                                   node.getCloud().getLocale());

        if (power > 0) {
            v /= factor;
        }  else {
            v *= factor;
        }

        if (nf instanceof DecimalFormat) {
            av = v < 0 ? -v : v;
            ((DecimalFormat) nf).applyPattern(av > lowLimit ? highFormat : lowFormat);
        }

        StringBuffer buf = nf.format(v, new StringBuffer(), new FieldPosition(0));
        if (power > 0) {
            buf.append(prefixes[power - 1]);
        } else if (power < 0) {
            buf.append(SI_NEGATIVE[-1 - power]);
        }
        buf.append(unit);
        return buf.toString();
    }

    public String toString() {
        return "[" + unit + "]";
    }
}


