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
 * @version $Id$
 * @since MMBase-1.8
 */

public class FormatFileSize implements Processor {

    private static final long serialVersionUID = 1L;
    private static int KILO     = 1000;
    private static int MEGA     = 1000000;
    private static int GIGA     = 1000000000;
    private static int KBYTE    = 1024;
    private static int MBYTE    = KBYTE * KBYTE;
    private static int GBYTE    = KBYTE * KBYTE * KBYTE;


    public final Object process(Node node, Field field, Object value) {
        int size = node.getIntValue(field.getName());
        if (size < 9 * KILO) {
            return "" + size + " byte";
        } else if (size < 9 * MEGA) {
            return "" + size / KBYTE + " kbyte";
        } else if (size < 9 * GIGA) {
            return "" + size / MBYTE + " Mbyte";
        } else {
            return "" + size / GBYTE + " Gbyte";
        }
    }

    public String toString() {
        return "FILESIZE";
    }
}


