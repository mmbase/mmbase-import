/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.macro;

import javax.servlet.jsp.JspTagException;
import org.mmbase.bridge.jsp.taglib.ListConditionTag;
/**
 *  Shortcut for listCondition changed
 *  @version $Id: ListChangedTag.java,v 1.4 2003-03-25 13:21:03 michiel Exp $
 */
public class ListChangedTag extends ListConditionTag{
    protected int getValue() throws JspTagException {
        return CONDITION_CHANGED;
    }
}
