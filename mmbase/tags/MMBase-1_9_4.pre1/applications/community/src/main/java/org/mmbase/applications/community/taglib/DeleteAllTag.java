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
 * As NodeTag, but all messages belonging to the node (which should be a
 * channel) will be removed after the body.
 *
 * @author Pierre van Rooden
 * @version $Id$
 */
 
public class DeleteAllTag extends NodeTag {

    public void setChannel(String c) throws JspTagException {
        setNumber(c);
    }

    public int doEndTag() throws JspTagException {
        Module community=getCloudContext().getModule("communityprc");
        Node node=getNodeVar();
        community.getInfo("CHANNEL-"+node.getNumber()+"-DELALLMESSAGES",pageContext.getRequest(),pageContext.getResponse());
        return super.doEndTag();
    }
}
