/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.typehandler;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.jsp.taglib.*;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @since  MMBase-1.6
 * @version $Id: BooleanHandler.java,v 1.4 2007-02-10 16:49:27 nklasens Exp $
 */

public class BooleanHandler extends EnumHandler {

    /**
     * Constructor for BooleanTypeHandler.
     */
    public BooleanHandler(FieldInfoTag tag) throws JspTagException {
        super(tag);
    }

}
