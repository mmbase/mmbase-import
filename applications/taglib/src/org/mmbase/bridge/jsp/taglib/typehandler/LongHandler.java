/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.typehandler;

import javax.servlet.jsp.JspTagException;
import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.FieldInfoTag;
import org.mmbase.bridge.jsp.taglib.ParamHandler;
import org.mmbase.storage.search.Constraint;


/**
 * @javadoc
 *
 * @author Gerard van de Looi
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 * @version $Id: LongHandler.java,v 1.12 2006-07-05 15:20:45 pierre Exp $
 */

public class LongHandler extends AbstractTypeHandler {

    /**
     * Constructor for LongHandler.
     * @param tag
     */
    public LongHandler(FieldInfoTag tag) {
        super(tag);
    }

    protected Object cast(Object value, Node node, Field field) {
        if (value == null || "".equals(value)) return "";
        return  super.cast(value, node, field);
    }

}
