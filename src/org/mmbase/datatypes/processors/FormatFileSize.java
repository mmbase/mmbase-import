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
 * Actually, using setters like {@link #setUnit()}, this class can also be used to postfix all
 * other kinds of units to integers.
 *
 * @todo Why not apply this to floats too. Also support SI prefixes below k then (c, m, micro, n, etc).
 *
 * @author Michiel Meeuwissen
 * @version $Id: FormatFileSize.java,v 1.7 2008-02-03 17:33:57 nklasens Exp $
 * @since MMBase-1.8
 */

public class FormatFileSize extends FormatQuantity {

    private static final long serialVersionUID = 1L;


    public FormatFileSize() {
        setClassical(false);
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
            prefixes = IEEE_BI;
            unit = "B";
            k = KIBI;
        }
    }

    public String toString() {
        return "FILESIZE";
    }
}


