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
 * As NodeTag, but the body is evaluated only if the condiiton specified evalueates
 * to true.
 *
 * @author Pierre van Rooden
 * @version $Id$
 */
 
public class TestChannelTag extends NodeTag {

    public final static String OPEN =  "OPEN";
    public final static String READONLY =  "READONLY";
    public final static String CLOSED =  "CLOSED";

    String condition=OPEN;
    boolean reverse=false;

    public void setChannel(String c) throws JspTagException {
        setNumber(c);
    }

    public void setCondition(String c) throws JspTagException {
        condition=getAttributeValue(c).toUpperCase();
        if (!condition.equals(OPEN) &&
            !condition.equals(CLOSED) &&
            !condition.equals(READONLY)) {
            throw new JspTagException("Condition need be one of OPEN, CLOSED or READONLY.");
        }
    }

    public void setReverse(boolean r) {
        reverse=r;
    }

    /**
     *
     */
    public int doStartTag() throws JspTagException {
        if (super.doStartTag()==EVAL_BODY_BUFFERED) {
            Node node=getNodeVar();
            Module community=getCloudContext().getModule("communityprc");
            String state=community.getInfo("CHANNEL-"+node.getNumber()+"-ISOPEN",pageContext.getRequest(),pageContext.getResponse());
            boolean result=state.equalsIgnoreCase(condition);
            if (result!=reverse)
                return EVAL_BODY_BUFFERED;
        }
        return SKIP_BODY;
    }

}
