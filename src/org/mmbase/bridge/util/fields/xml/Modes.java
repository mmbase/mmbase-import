/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.util.fields.xml;
import org.mmbase.util.logging.*;

/**
 * XML-modes
 *
 * @author Michiel Meeuwissen
 * @version $Id: Modes.java,v 1.1 2005-06-28 14:19:54 michiel Exp $
 * @since MMBase-1.8
 */

public abstract class Modes {
    private static final Logger log = Logging.getLoggerInstance(Modes.class);

    public static final int XML   = 0;
    public static final int PRETTYXML   = 1;
    public static final int FLAT  = 2;
    public static final int WIKI  = 3;
    public static final int KUPU  = 4;
    

    public static int getMode(Object mode) {
        if ("xml".equals(mode)) {
            return XML;
        } else if ("prettyxml".equals(mode)) {
            return PRETTYXML;
        } else if ("flat".equals(mode)) {
            return FLAT;
        } else if ("wiki".equals(mode)) {
            return WIKI;
        } else if ("kupu".equals(mode)) {
            return KUPU;
        } else {
            log.warn("Unknown mode " + mode);
            return XML;
        }
    }
}
