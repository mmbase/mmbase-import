/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.macro;

import javax.servlet.jsp.JspException;
import org.mmbase.bridge.jsp.taglib.ListConditionTag;
/**
*  shortcut for listContdition first<BR>
* NOTE: we need to have the same names in condition and marco;s
*/
public class ListHeadTag extends ListConditionTag{
	public int doStartTag() throws JspException{
		setValue("first");
		return super.doStartTag();
	}
}
