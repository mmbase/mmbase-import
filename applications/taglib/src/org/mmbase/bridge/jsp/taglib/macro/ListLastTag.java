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
 * Macro tag for listContdition last
 *
 * @version $Id: ListLastTag.java,v 1.3 2003-06-06 10:03:24 pierre Exp $
 */

public class ListLastTag extends ListConditionTag {
    protected int getValue() throws JspTagException {
        return CONDITION_LAST;
    }
}
