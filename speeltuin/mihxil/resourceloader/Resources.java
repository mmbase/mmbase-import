/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;

/**

 * @author Michiel Meeuwissen
 * @version $Id: Resources.java,v 1.1 2004-10-02 17:36:08 michiel Exp $
 * @since   MMBase-1.8
 * @see    
 */
public class Resources extends Attachments {
    private static final Logger log = Logging.getLoggerInstance(Resources.class);

    // these should perhaps be configurable:
    public static final String    RESOURCENAME_FIELD  = "name";
    public static final String    FILENAME_FIELD      = "filename";
    public static final String    HANDLE_FIELD        = "handle";
    public static final String    DEFAULT_CONTEXT     = "admin";



    public Object getValue(MMObjectNode node, String field) {
        if (field.equals(FILENAME_FIELD)) {
            String s = node.getStringValue(RESOURCENAME_FIELD);
            int i = s.lastIndexOf("/");
            if (i > 0) {
                return s.substring(i);
            } else {
                return s;
            }
        } else {
            return super.getValue(node, field);
        }
    }

}
