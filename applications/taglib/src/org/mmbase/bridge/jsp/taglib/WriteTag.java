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

    public void setType(String t) throws JspTagException { 
        helper.setType(t);
    }
    public void setJspvar(String j) {
        helper.setJspvar(j);
    }
    public void setSession(String s) throws JspTagException {
        helper.setSession(getAttributeValue(s));
    }
    public void setWrite(String w) throws JspTagException {
        helper.setWrite(getAttributeBoolean(w));
    }
    public Object getValue() {
        return helper.getValue();
    }
    
    
    protected Object getObject() throws JspTagException {
        if (log.isDebugEnabled()) {
            log.debug("getting object " + getReferid());
        }
        if (getReferid() == null) { // get from parent Writer.
            Writer w =  (Writer) findParentTag("org.mmbase.bridge.jsp.taglib.Writer", null);
            return w.getValue();
        }
        if (helper.getType() == WriterHelper.TYPE_BYTES) {
            return getContextTag().getBytes(getReferid()); // a hack..            
        }
        return getObject(getReferid());
    }


    public int doStartTag() throws JspTagException {    
        helper.setValue(getObject());        
        helper.setJspVar(pageContext);  
        if (getId() != null) {
            getContextTag().register(getId(), helper.getValue());
        }
        return EVAL_BODY_TAG;
    }    
    
    public int doAfterBody() throws JspTagException {
        helper.setBodyContent(bodyContent);
        return helper.doAfterBody();
    }

}
