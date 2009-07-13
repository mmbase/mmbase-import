/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

/**
 * A processor that gets a number as a file-size, that is, rounded with kbytes and Mb's and so on.
 *
 * Actually, using setters like {@link FormatQuantity#setUnit(String)}, this class can also be used to postfix all
 * other kinds of units to integers.
 *
 * The unit symbol used is 'B', the IEEE 1541 recommended symbol for a 'byte'. Use {@link
 * #setClassical} or {@link #setUnit} if you prefer the non-standard, but once common 'byte' as a
 * unit.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */

public class FormatFileSize extends FormatQuantity {

    private static final long serialVersionUID = 1L;


    public FormatFileSize() {
        setClassical(false);
    }
    /**
     * It was commonplace to mix SI prefixes with 'binary' factors.
     * If this is set to 'true', then SI prefixes are used and 'byte' will be the unit symbol, and 1024 the
     * basic factor (which basically adheres to no recommendation or standard, but is widely used by
     * e.g. hard disk manufacturers).
     *
     * If this is set to 'false'. then then IEEE prefixes are used, and 'B' will be the unit.
     *
     * Using this method with 'true' is the only way to get a multiplication factor which does not
     * correspond to the set of prefixes. Normally {@link #setBinaryPrefixes} can be used to toggle
     * between IEEE and SI mode.
     *
     * @param c boolean
     * @since MMBase-1.9
     */
    public void setClassical(boolean c) {
        if (c) {
            prefixes = SI;
            unit = "byte";
            k = KIBI;
        } else {
            prefixes = IEEE_BI;
            unit = "B";
            k = KIBI;
        }
    }

    @Override
    public String toString() {
        return "FILESIZE";
    }

    public static class Parser extends FormatQuantity.Parser {
        public void setClassical(boolean c) {
            if (c) {
                prefixes = SI;
                unit = "byte";
                k = KIBI;
            } else {
                prefixes = IEEE_BI;
                unit = "B";
                k = KIBI;
            }
        }
    }
}


