/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.pageflow;

import javax.servlet.jsp.JspTagException;

/**
 * The inverse of PresentTag.
 *
 * @see PresentTag
 * @author Michiel Meeuwissen
 * @version $Id: NotPresentTag.java,v 1.11 2003-08-11 15:27:28 michiel Exp $
 */

public class NotPresentTag extends PresentTag {

    public int doStartTag() throws JspTagException {
        if ((! getContextProvider().getContextContainer().isPresent(getReferid())) != getInverse()) {
            return EVAL_BODY_BUFFERED;
        } else {
            return SKIP_BODY;
        }
    }

}
