/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.macro;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import org.mmbase.bridge.jsp.taglib.NodeListTag;
import org.mmbase.bridge.jsp.taglib.NodeProvider;
/**
* Shortcut for NodeList where the start node is the parent node
*
* FIXME: cannot indicate from which node ('node' attribute is already occupied...)
*/
public class RelatedTag extends NodeListTag {
    public int doStartTag() throws JspException {
 

        // Hmm, well, we are repeating a little code from NodeReferrer
        // here.
        // Not too nice. Perhaps NodeReferrer simply should be an
        // interface, then we could at least logically represent this
        // repetition.

        // Sigh, class RelatedTag extends NodeListTag, NodeReferrerTag
        // would be really convenient...
        try {
            NodeProvider nodeProvider = (NodeProvider) findAncestorWithClass((Tag)this, Class.forName("org.mmbase.bridge.jsp.taglib.NodeProvider")); 
            if (nodeProvider == null) {
                throw new JspException ("Could not find parent nodeProvider");  
            } 
            setNode(nodeProvider.getNodeVar().getStringValue("number"));
        }catch (java.lang.ClassNotFoundException e) {
            throw new JspException ("Could not find NodeProvider class");  
        }

        return super.doStartTag();
    }
}
