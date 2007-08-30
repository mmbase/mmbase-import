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
 * @author Michiel Meeuwissen
 * @version $Id: FormatFileSize.java,v 1.4 2007-08-30 22:25:36 michiel Exp $
 * @since MMBase-1.8
 */

public class FormatFileSize implements Processor {

    private static final long serialVersionUID = 1L;
    private static final int KILO     = 1000;
    private static final int KIBI     = 1024;


    private static final String[] SI_BI = {"Ki", "Mi", "Gi", "Ti", "Pi", "Ei", "Zi", "Yi"};
    private static final String[] SI    = {"k",  "M",  "G",  "T",  "P",  "E",  "Z",  "Y"};

    protected int      k        = KIBI;
    protected String[] prefixes = SI_BI;
    protected String unit = "B";
    protected String format = "{0,number,#} ";
    protected int limitFactor = 9 * k;

    /**
     * If  set, will use binary prefixes as recommended by IEEE 1541 . So, Ki, Mi, etc. which
     * are multiples of 1024. Otherwise normal SI prefixes are applied (k, M, G etc), which are multiples of
     * 1000.
     * @since MMBase-1.9
     */
    public void setBinaryPrefixes(boolean bi) {
        if (bi) {
            prefixes = SI_BI;
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

    /**
     * It was commonplace to mix SI prefixes with 'binary' factors.
     * If this is set to 'true', then SI prefixes are used 'byte' will be the unit symbol, and 1024 the
     * basic factor (which basicly adhers to no recomendation or standard, but is widely used by
     * e.g. hard disk manufacturers).
     * @since MMBase-1.9
     */
    public void setClassical(boolean c) {
        if (c) {
            prefixes = SI;
            unit = "byte";
            k = KIBI;
        } else {
            prefixes = SI_BI;
            unit = "B";
            k = KIBI;
        }
    }
    /**
     * MessageFormat to format the number. Defaults to '{0,number,#} '.
     * @since MMBase-1.9
     */
    public void setFormat(String mf) {
        if (mf == null) throw new IllegalArgumentException();
        format = mf;
    }
    public String getFormat() {
        return format;
    }

    /**

     * @since MMBase-1.9
     */
    public void setLimit(int l) {
        limitFactor = l;
    }


    public final Object process(Node node, Field field, Object value) {
        int size = node.getIntValue(field.getName());
        int factor = 1;
        int power  = 0;
        while (size > factor * limitFactor) {
            factor *= k;
            power++;
        }

        StringBuffer buf = new StringBuffer();
        MessageFormat mf = new MessageFormat(format,  node.getCloud().getLocale());
        mf.format(new Object[] { (float) size  / factor}, buf, null);
        if (power > 0) {
            buf.append(prefixes[power - 1]);
        }
        buf.append(unit);
        return buf.toString();
    }

    public String toString() {
        return "FILESIZE";
    }
}


