/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.community;

import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.Node;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import org.mmbase.bridge.jsp.taglib.*;

/**
* GetInfo tag obtains information from the multipurpose INFO field.
*
* @author Pierre van Rooden
*/
public class GetInfoTag extends NodeReferrerTag {

    private static Logger log = Logging.getLoggerInstance(FieldTag.class.getName());

    protected Node node;
    private String key=null;
    private String value=null;
    private String jspvar=null;
    private boolean skiponempty=true;

    public void setKey(String k) throws JspTagException {
        key=k;
    }

    public void setJspvar(String v) throws JspTagException {
        jspvar = v;
    }

    public void setSkiponempty(boolean skip) {
        skiponempty=skip;
    }

    public int doStartTag() throws JspTagException{
        // firstly, search the node:
        node = findNodeProvider().getNodeVar();

        // found the node now. Now we can decide what must be shown:
        if (key==null) key="name";
        value=node.getStringValue("getinfovalue("+key+")");
        if ((value == null) || (value.length()==0)) {
            if (skiponempty) {
                return SKIP_BODY;
            } else {
                value="";
            }
        }
        return EVAL_BODY_TAG;
    }

    /**
     * write the value of the field.
     **/
    public void doInitBody() throws JspTagException {
        if (jspvar!=null) {
            pageContext.setAttribute(jspvar, value);
        } else {
            try {
                bodyContent.print(value);
            } catch (java.io.IOException e) {
                throw new JspTagException (e.toString());
            }
        }
    }

    /**
     * write the value of the field.
     **/
    public int doAfterBody() throws JspTagException {
        try {
            bodyContent.writeOut(bodyContent.getEnclosingWriter());
        } catch (java.io.IOException e) {
            throw new JspTagException (e.toString());
        }
        return SKIP_BODY;
    }
}
