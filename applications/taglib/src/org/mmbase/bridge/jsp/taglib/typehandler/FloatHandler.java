/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.typehandler;

import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.FieldInfoTag;

/**
 * @javadoc
 *
 * @author Gerard van de Looi
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 * @version $Id: FloatHandler.java,v 1.6 2006-02-03 16:02:49 michiel Exp $
 */

public class FloatHandler extends AbstractTypeHandler {

    /**
     * Constructor for FloatHandler.
     * @param tag
     */
    public FloatHandler(FieldInfoTag tag) {
        super(tag);
    }

    protected Object cast(Object value, Node node, Field field) {
        if (value == null || "".equals(value)) return "";
        return  super.cast(value, node, field);
    }


}
