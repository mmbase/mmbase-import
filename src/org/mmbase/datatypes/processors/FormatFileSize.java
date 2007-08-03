/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;

/**
 * A processor that gets a number as a file-size, that is, rounded with kbytes and Mb's and so on.
 *
 * @author Michiel Meeuwissen
 * @version $Id: FormatFileSize.java,v 1.2 2007-08-03 14:49:19 michiel Exp $
 * @since MMBase-1.8
 */

public class FormatFileSize implements Processor {

    private static final long serialVersionUID = 1L;
    private static final int KILO     = 1000;
    private static final int MEGA     = 1000000;
    private static final int GIGA     = 1000000000;
    private static final int KIBIBYTE    = 1024;
    private static final int MEBIBYTE    = KIBIBYTE * KIBIBYTE;
    private static final int GIBIBYTE    = KIBIBYTE * KIBIBYTE * KIBIBYTE;


    public final Object process(Node node, Field field, Object value) {
        int size = node.getIntValue(field.getName());
        if (size < 9 * KILO) {
            return "" + size + " B";
        } else if (size < 9 * MEGA) {
            return "" + size / KIBIBYTE + " KiB";
        } else if (size < 9 * GIGA) {
            return "" + size / MEBIBYTE + " MiB";
        } else {
            return "" + size / GIBIBYTE + " GiB";
        }
    }

    public String toString() {
        return "FILESIZE";
    }
}


