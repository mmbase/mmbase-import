/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import java.io.IOException;

import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
* The writetag can take a variable from the context and put it in a
* jsp variable, or write it to the page.
*
* @author Michiel Meeuwissen 
*/

public class WriteTag extends ContextReferrerTag {

    private static Logger log = Logging.getLoggerInstance(WriteTag.class.getName());

    protected String  jspvar = null;
    private String  extraBodyContent = null;
    protected String  type = null;

    public void setType(String t) {
        // nothing to do, the type property is only used in the TEI.
        type = t;
    }

    public void setJspvar(String j) {
        jspvar = j;
    }


    public int doStartTag() throws JspTagException {

        if (log.isDebugEnabled()) {
            log.debug("getting object " + getReferid() + "-> " + getContextTag().getObject(getReferid()));
        }
        Object value = getContextTag().getObject(getReferid());

        extraBodyContent = null;
        if (value != null) {
            if (jspvar != null) {                
                pageContext.setAttribute(jspvar, value);
            } else {
                if (type != null) {
                    throw new JspTagException("It does not make sense to specify the type attribute (" + type + ") without the jspvar attribute");
                }
                extraBodyContent = value.toString();
            }
        }
        return EVAL_BODY_TAG;

    }
    public int doAfterBody() throws JspTagException {
        try {
            if (extraBodyContent != null) {
                log.debug("extra body content" + extraBodyContent);
                String body = bodyContent.getString();
                bodyContent.clearBody();
                bodyContent.print(extraBodyContent + body);
            }
            bodyContent.writeOut(bodyContent.getEnclosingWriter());
            return SKIP_BODY;
        } catch (IOException ioe){
            throw new JspTagException(ioe.toString());
        }

    }

}
