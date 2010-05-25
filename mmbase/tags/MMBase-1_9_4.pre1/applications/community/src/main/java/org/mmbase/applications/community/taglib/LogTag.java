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
 * As NodeTag, but logging will be started on the node (which should be a channel)
 * at the start of the  body tag.
 *
 * @author Pierre van Rooden
 * @version $Id$
 */
 
public class LogTag extends NodeTag {

    public final static String STOP =  "STOP";
    public final static String FILE =  "FILE";

    String action=FILE;
    String file="chat.log";

    public void setChannel(String c) throws JspTagException {
        setNumber(c);
    }

    public void setFile(String f) throws JspTagException {
        file=getAttributeValue(f);
    }

    public void setAction(String a) throws JspTagException {
        action=getAttributeValue(a).toUpperCase();
        if (!action.equals(STOP) &&
            !action.equals(FILE)) {
            throw new JspTagException("Action need be one of STOP or FILE.");
        }
    }

    public int doStartTag() throws JspTagException {
        super.doStartTag() ;
        Module community=getCloudContext().getModule("communityprc");
        Node node=getNodeVar();
        if (action.equals(FILE)) {
            community.getInfo("CHANNEL-"+node.getNumber()+"-RECORD-FILE-"+file,pageContext.getRequest(),pageContext.getResponse());
        } else {
            community.getInfo("CHANNEL-"+node.getNumber()+"-RECORD-STOP",pageContext.getRequest(),pageContext.getResponse());
        }
        return EVAL_BODY_BUFFERED;
    }
}
