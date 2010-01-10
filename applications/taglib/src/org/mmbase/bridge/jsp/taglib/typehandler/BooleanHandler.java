/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.typehandler;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.storage.search.*;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @since  MMBase-1.6
 * @version $Id: BooleanHandler.java,v 1.3 2005-12-20 19:07:13 michiel Exp $
 */

public class BooleanHandler extends EnumHandler {

    /**
     * Constructor for BooleanTypeHandler.
     */
    public BooleanHandler(FieldInfoTag tag) throws JspTagException {
        super(tag);
    }

}
