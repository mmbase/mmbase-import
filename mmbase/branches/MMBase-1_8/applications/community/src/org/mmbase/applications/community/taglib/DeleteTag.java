/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.community.taglib;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.Module;

import org.mmbase.bridge.jsp.taglib.NodeTag;

/**
 *
 * As NodeTag, but the node (which should be a message) will be removed after the body.
 * Also removed are all replies to the message.
 *
 * @author Pierre van Rooden
 * @version $Id: DeleteTag.java,v 1.5 2003-06-18 20:03:57 michiel Exp $
 */
 
public class DeleteTag extends NodeTag {

    public void setMessage(String msg) throws JspTagException {
        setNumber(msg);
    }

    public int doEndTag() throws JspTagException {
        Module community=getCloudContext().getModule("communityprc");
        if (community==null)
            throw new JspTagException("Community module not available.");
        Node node=getNodeVar();
        community.getInfo("MESSAGE-"+node.getNumber()+"-DEL",pageContext.getRequest(),pageContext.getResponse());
        return super.doEndTag();
    }
}
