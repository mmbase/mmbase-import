/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.functions;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.Condition;
import org.mmbase.bridge.NotFoundException;

import javax.servlet.jsp.JspTagException;
import org.mmbase.util.logging.*;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: HasFunctionTag.java,v 1.4 2008-08-14 13:58:49 michiel Exp $
 * @since MMBase-1.8
 */

public class HasFunctionTag extends AbstractFunctionTag implements Condition {
    private static final Logger log = Logging.getLoggerInstance(HasFunctionTag.class);

    protected Attribute inverse = Attribute.NULL;

    public void setInverse(String b) throws JspTagException {
        inverse = getAttribute(b);
    }
    protected boolean getInverse() throws JspTagException {
        return inverse.getBoolean(this, false);
    }

    public int doStartTag() throws JspTagException {
        initTag();
        boolean found;
        try {
            found = getFunction() != null;
        } catch (NotFoundException nfe) {
            found = false;
        } catch (java.lang.reflect.UndeclaredThrowableException ute) {
            // can happen on modules, which are proxies.
            found = false;
        }
        if (found != getInverse()) {
            return EVAL_BODY;
        } else {
            return SKIP_BODY;
        }
    }
    public int doAfterBody() throws JspTagException {
        if (EVAL_BODY == EVAL_BODY_BUFFERED) { // not needed if EVAL_BODY_INCLUDE
            try{
                if(bodyContent != null) {
                    bodyContent.writeOut(bodyContent.getEnclosingWriter());
                }
            } catch(java.io.IOException e){
                throw new JspTagException("IO Error: " + e.getMessage());
            }
        }
        return SKIP_BODY;
    }
}
