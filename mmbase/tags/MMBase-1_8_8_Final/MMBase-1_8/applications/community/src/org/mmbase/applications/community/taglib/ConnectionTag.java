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
 * As NodeTag, but the node (which should be a user) will be connected to
 * the indicated channel at the start of the body.
 *
 * @author Pierre van Rooden
 * @version $Id: ConnectionTag.java,v 1.4 2003-06-18 20:03:56 michiel Exp $
 */
 
public class ConnectionTag extends NodeTag {

    public final static String JOIN =  "JOIN";
    public final static String LEAVE =  "LEAVE";
    public final static String STILLACTIVE =  "STILLACTIVE";

    String channel=null;
    String action=STILLACTIVE;

    public void setChannel(String c) throws JspTagException {
        channel=getAttributeValue(c);
    }

    public void setAction(String a) throws JspTagException {
        action=getAttributeValue(a).toUpperCase();
        if (!action.equals(JOIN) &&
            !action.equals(LEAVE) &&
            !action.equals(STILLACTIVE)) {
            throw new JspTagException("Action need be one of JOIN, LEAVE or STILLACTIVE.");
        }
    }

    public void setUser(String u) throws JspTagException {
        setNumber(u);
    }

    public int doStartTag() throws JspTagException {
        super.doStartTag() ;
        Module community=getCloudContext().getModule("communityprc");
        Node node=getNodeVar();
        community.getInfo("CHANNEL-"+channel+"-"+action+"-"+node.getNumber(),pageContext.getRequest(),pageContext.getResponse());
        return EVAL_BODY_BUFFERED;
    }
}
