/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;
import org.mmbase.bridge.Node;
import javax.servlet.jsp.JspTagException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The writetag can take a variable from the context and put it in a
 * jsp variable, or write it to the page.
 *
 * This is also more or less the simplest possible implemententation
 * of a 'Writer' tag.
 *
 * @author Michiel Meeuwissen 
 **/

public class WriteTag extends ContextReferrerTag implements Writer, WriterReferrer {

    public static int MAX_COOKIE_AGE = 60*60*24*30*6; // half year
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
    public Object getWriterValue() {
        return helper.getValue();
    }

    private String sessionvar;
    private String cookie;
    private String value;

    public void setSession(String s) throws JspTagException {
        sessionvar = getAttributeValue(s);
    }

    public void setCookie(String s) throws JspTagException {
        cookie = getAttributeValue(s);
    }
    public void setValue(String v) throws JspTagException {
        value = getAttributeValue(v);
    }
    private String writerid = null;
    public void setWriter(String w) throws JspTagException {
        writerid = getAttributeValue(w);
        
    }

    
    
    protected Object getObject() throws JspTagException {
        if (log.isDebugEnabled()) {
            log.debug("getting object " + getReferid());
        }
        if (getReferid() == null && value == null) { // get from parent Writer.
            Writer w =  (Writer) findParentTag("org.mmbase.bridge.jsp.taglib.Writer", writerid);
            return w.getWriterValue();
        }

        if (value != null) {
            if (getReferid() != null) {
                throw new JspTagException("Cannot specify the 'value' atribute and the 'referid' attribute at the same time");
            }
            return value;
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
        if (cookie != null) {
            Object v = helper.getValue();
            Cookie c;
            if (v instanceof String) {
                c = new Cookie(cookie, (String) v); 
            } else if (v instanceof Integer) {
                c = new Cookie(cookie, "" + v); 
            } else if (v instanceof Node) {
                c = new Cookie(cookie, "" + ((Node) v).getNumber()); 
            } else {
                throw new JspTagException(v.toString() + " is not of the right type to write to cookie. It is a (" +  v.getClass().getName() + ")");
            }
            c.setMaxAge(MAX_COOKIE_AGE);
            ((HttpServletResponse)pageContext.getResponse()).addCookie(c); 
        }
        return EVAL_BODY_TAG;
    }    
    
    public int doAfterBody() throws JspTagException {
        helper.setBodyContent(bodyContent);
        return helper.doAfterBody();
    }

}
