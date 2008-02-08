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
 * As NodeTag, but the node (which should be a channel) will be opened or
 * closed at the start of the body, depending on the action given.
 *
 * @author Pierre van Rooden
 * @version $Id: ChannelTag.java,v 1.4 2003-06-18 20:03:56 michiel Exp $
 */
 
public class ChannelTag extends NodeTag {

    public final static String OPEN =  "OPEN";
    public final static String READONLY =  "READONLY";
    public final static String CLOSE =  "CLOSE";

    String action=OPEN;

    public void setChannel(String c) throws JspTagException {
        setNumber(c);
    }

    public void setAction(String c) throws JspTagException {
        action=getAttributeValue(c).toUpperCase();
        if (!action.equals(OPEN) &&
            !action.equals(CLOSE) &&
            !action.equals(READONLY)) {
            throw new JspTagException("Action need be one of OPEN, CLOSE or READONLY.");
        }
    }

    public int doStartTag() throws JspTagException {
        super.doStartTag() ;
        Module community=getCloudContext().getModule("communityprc");
        Node node=getNodeVar();
        community.getInfo("CHANNEL-"+node.getNumber()+"-"+action,pageContext.getRequest(),pageContext.getResponse());
        return EVAL_BODY_BUFFERED;
    }
}
