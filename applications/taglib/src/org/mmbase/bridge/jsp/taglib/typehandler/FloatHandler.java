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
 * @version $Id: FloatHandler.java,v 1.7 2009-01-12 12:48:20 michiel Exp $
 */

public class FloatHandler extends AbstractTypeHandler {

    /**
     * Constructor for FloatHandler.
     * @param tag
     */
    public FloatHandler(FieldInfoTag tag) {
        super(tag);
    }

    @Override protected Object cast(Object value, Node node, Field field) {
        if (value == null || "".equals(value)) return "";
        return  super.cast(value, node, field);
    }


}
