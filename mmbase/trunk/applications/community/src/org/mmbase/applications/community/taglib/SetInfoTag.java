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

import org.mmbase.bridge.jsp.taglib.*;

/**
 * SetInfo tag stores information in the multipurpose INFO field.
 *
 * @author Pierre van Rooden
 * @version $Id: SetInfoTag.java,v 1.10 2005-01-30 16:46:35 nico Exp $
 */
 
public class SetInfoTag extends NodeReferrerTag {

    protected Node node;
    private String key=null;

    public void setKey(String k) throws JspTagException {
        key = getAttributeValue(k);
    }

    public int doStartTag() throws JspTagException{
        // firstly, search the node:
        node = getNode();

        if (key == null) { // name not null
            throw new JspTagException ("Should use 'key' attribute");
        }
        return EVAL_BODY_BUFFERED;
    }

    /**
     * store the given value
     * @see javax.servlet.jsp.tagext.IterationTag#doAfterBody()
     **/
    public int doAfterBody() throws JspTagException {
        String value=bodyContent.getString();
        Module community=getCloudContext().getModule("communityprc");
        community.getInfo("MESSAGE-"+node.getNumber()+"-SETINFOFIELD-"+key+"-"+value,pageContext.getRequest(),pageContext.getResponse());
        return SKIP_BODY;
    }
}
