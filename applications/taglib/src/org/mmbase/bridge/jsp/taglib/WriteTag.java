/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.JspTagException;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The writetag can take a variable from the context and put it in a
 * jsp variable, or write it to the page.
 *
 * This is also more or less the simplest possible implemententation
 * of a 'Writer' tag.
 *
 * @author Michiel Meeuwissen */

public class WriteTag extends ContextReferrerTag implements Writer {

    private static Logger log = Logging.getLoggerInstance(WriteTag.class.getName());

    protected WriterHelper helper = new WriterHelper(); 
    // sigh, we would of course prefer to extend, but no multiple inheritance possible in Java..

    public void setVartype(String t) throws JspTagException { 
        helper.setVartype(t);
    }
    public void setJspvar(String j) {
        helper.setJspvar(j);
    }
    public void setWrite(String w) throws JspTagException {
        helper.setWrite(getAttributeBoolean(w));
    }
    public Object getValue() {
        return helper.getValue();
    }

    private String sessionvar;

    public void setSession(String s) throws JspTagException {
        sessionvar = getAttributeValue(s);
    }

    
    
    protected Object getObject() throws JspTagException {
        if (log.isDebugEnabled()) {
            log.debug("getting object " + getReferid());
        }
        if (getReferid() == null) { // get from parent Writer.
            Writer w =  (Writer) findParentTag("org.mmbase.bridge.jsp.taglib.Writer", null);
            return w.getValue();
        }
        if (helper.getVartype() == WriterHelper.TYPE_BYTES) {
            return getContextTag().getBytes(getReferid()); // a hack..            
        }
        return getObject(getReferid());
    }


    public int doStartTag() throws JspTagException {    
        helper.setValue(getObject());        
        helper.setJspvar(pageContext);  
        if (getId() != null) {
            getContextTag().register(getId(), helper.getValue());
        }
        if (sessionvar != null) {
            pageContext.getSession().setAttribute(sessionvar, helper.getValue());
            helper.overrideWrite(false); // default behavior is not to write to page if wrote to session.
        }
        return EVAL_BODY_TAG;
    }    
    
    public int doAfterBody() throws JspTagException {
        helper.setBodyContent(bodyContent);
        return helper.doAfterBody();
    }

}
