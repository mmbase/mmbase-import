/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.macro;

import javax.servlet.jsp.JspException;
import org.mmbase.bridge.jsp.taglib.NodeListTag;
import org.mmbase.bridge.jsp.taglib.NodeProvider;
/**
*  shortcut for NodeList where the start node is the parent node
*/
public class RelatedTag extends NodeListTag{
	public int doStartTag() throws JspException{
		NodeProvider nodeProvider = findNodeProvider(this,null);
		setNode(nodeProvider.getNodeVar().getStringValue("number"));
		return super.doStartTag();
	}
}
