/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors.xml;
import org.mmbase.util.logging.*;

/**
 * XML-modes. XML-modes can be attributed to a Cloud (using {@link org.mmbase.bridge.Cloud#setProperty}
 * and {@link org.mmbase.bridge.Cloud#PROP_XMLMODE}) and influence how an XML field must behave
 * itself. XML is all about flexibility of presentation, and this mode regulates that.
 *
 * It boils down to the fact that 'processors' can be plugged on XML fields which can use
 * this 'mode' to behave differently.
 *
 * Most modes will influence especially {@link org.mmbase.bridge.Node#setStringValue(String,
 * String)} and {@link org.mmbase.bridge.Node#getStringValue(String)}. (depending on the
 * datatypes.xml), but some modes could also set/get XMLValue.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */

public abstract class Modes {
    private static final Logger log = Logging.getLoggerInstance(Modes.class);

    /**
     * The 'XML' mode should mean that the XML will be sent and expected as 'pure' as possible. E.g.
     * getString of an XML value in 'XML' mode should normally return a straight-forward
     * stringification of the XML.
     */
    public static final int XML   = 0;


    /**
     * PRETTYXML is like XML, but one could expect extra indentation and newlines to make the XML
     * more readable for humans. So, on getString you could expect a String which is not quite the
     * XML from the database, but chances are that it is equivalent.
     */
    public static final int PRETTYXML   = 1;

    /**
     * FLAT would return only the text from an XML field, so plain text without all XML
     * markup. Setting an XML value in 'FLAT' mode would generally be far from perfect.
     */
    public static final int FLAT  = 2;

    /**
     * WIKI is a bit like FLAT, but effort is made to give a better representation of the XML in
     * plain text. This mode could probably even be used when setting the field (This works e.g. quite
     * well for 'mmxf' fields).
     */
    public static final int WIKI  = 3;

    /**
     * KUPU-mode should trigger relations to be followed (on get) and be created (on set), and
     * should give and receive XHTML which will be (on get) or was (on set) edited by the 'kupu'
     * editor. See <a href="http://kupu.oscom.org">kupu</a>.
     */
    public static final int KUPU  = 4;


    /**
     * Makes the field look like Docbook XML. So, this could be implemented on get/set XMLValue as well.
     */
    public static final int DOCBOOK  = 5;


    /**
     * Converts a String identifier to one of the constants in this class
     */
    public static int getMode(Object m) {
        if (m == null) return XML;
        String mode = ("" + m).toLowerCase();
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
        } else if ("docbook".equals(mode)) {
            return DOCBOOK;
        } else {
            log.warn("Unknown mode " + mode, new Exception());
            return XML;
        }
    }
}
