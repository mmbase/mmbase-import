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
 * As NodeTag, but the node (which should be a commnity) will be opened or
 * closed at the start of the body, depending on the action given.
 * This affects all channels of this community.
 *
 * @author Pierre van Rooden
 * @version $Id$
 */
 
public class CommunityTag extends NodeTag {

    public final static String OPEN =  "OPEN";
    public final static String CLOSE =  "CLOSE";

    String action=OPEN;

    public void setCommunity(String c) throws JspTagException {
        setNumber(c);
    }

    public void setAction(String c) throws JspTagException {
        action=getAttributeValue(c).toUpperCase();
        if (!action.equals(OPEN) &&
            !action.equals(CLOSE)) {
            throw new JspTagException("Action need be one of OPEN, CLOSE or READONLY.");
        }
    }

    public int doStartTag() throws JspTagException {
        super.doStartTag() ;
        Module community=getCloudContext().getModule("communityprc");
        Node node=getNodeVar();
        community.getInfo("COMMUNITY-"+node.getNumber()+"-"+action,pageContext.getRequest(),pageContext.getResponse());
        return EVAL_BODY_BUFFERED;
    }
}
