/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;
import org.mmbase.bridge.Node;

import org.mmbase.bridge.jsp.taglib.containers.FunctionContainerReferrer;
import org.mmbase.bridge.jsp.taglib.util.Attribute;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

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
 * @version $Id: WriteTag.java,v 1.40 2003-11-20 11:11:05 michiel Exp $ 
 */

public class WriteTag extends ContextReferrerTag implements Writer, FunctionContainerReferrer {

    public static final int MAX_COOKIE_AGE = 60*60*24*30*6; // half year
    public static final String COOKIE_PATH    = "/";
    private static final Logger log = Logging.getLoggerInstance(WriteTag.class);

    private Attribute sessionvar = Attribute.NULL;
    private Attribute cookie     = Attribute.NULL;
    // private Attribute page       = Attribute.NULL;

    private Attribute value      = Attribute.NULL;
    private Attribute container = Attribute.NULL;

    public void setSession(String s) throws JspTagException {
        sessionvar = getAttribute(s);
    }

    public void setCookie(String s) throws JspTagException {
        cookie = getAttribute(s);
    }

    /*
      // A page attribute is not needed, because we have already taglib vars, which take the same function (and are actually stored here)
      public void setPage(String s) throws JspTagException {
      page = getAttribute(s);
    }
    */
    public void setValue(String v) throws JspTagException {
        value = getAttribute(v);
    }
    public void setContainer(String c) throws JspTagException {
        container = getAttribute(c); // not yet implemented
    }


    protected Object getObject() throws JspTagException {
        if (log.isDebugEnabled()) {
            log.debug("getting object " + getReferid());
        }
        if (getReferid() == null && value == Attribute.NULL) { // get from parent Writer.
            return findWriter().getWriterValue();
        }

        if (value != Attribute.NULL) {
            if (getReferid() != null) {
                 throw new JspTagException("Cannot specify the 'value' atribute and the 'referid' attribute at the same time");
            }
            return value.getString(this); // with value attribute only strings, of course.
        }

        if (helper.getVartype() == WriterHelper.TYPE_BYTES) {
            return getContextTag().getBytes(getReferid()); // a hack..
        }
        return getObject(getReferid());
    }


    public int doStartTag() throws JspTagException {
        if (log.isDebugEnabled()) {
            log.debug("start writetag id: '" +getId() + "' referid: '" + getReferid() + "' value '" + value + "'");
        }
        
        helper.setValue(getObject());


        if (getId() != null) {
            getContextProvider().getContextContainer().register(getId(), helper.getValue());
        }
        if (sessionvar != Attribute.NULL) {
            if (sessionvar != Attribute.NULL) {
                if (pageContext.getSession() == null) {
                    throw new JspTagException("Cannot write to session if session is disabled");
                }
                pageContext.getSession().setAttribute(sessionvar.getString(this), helper.getValue());
            } 
            helper.overrideWrite(false); // default behavior is not to write to page if wrote to session.
        }
        if (cookie != Attribute.NULL) {
            Object v = helper.getValue();
            String cookievalue;
            if (v instanceof Node) {
                cookievalue = "" + ((Node) v).getNumber();
            } else if (v instanceof String || v instanceof Number) {
                cookievalue =  "" + v;
            } else {
                throw new JspTagException(v.toString() + " is not of the right type to write to cookie. It is a (" +  v.getClass().getName() + ")");
            }

            // remove all cookies with given name
            HttpServletRequest request = ((HttpServletRequest)pageContext.getRequest());
            HttpServletResponse response = ((HttpServletResponse)pageContext.getResponse());
            
            if (log.isDebugEnabled()) {
                log.debug("Writing cookie " + cookie + " / " + v);
            }
            // count present cookies of this name
            int cookiecount = 0;
            Cookie[] cookies = request.getCookies();
            if (cookies != null) { 
                for (int i=0; i< cookies.length; i++) {
                    Cookie c = cookies[i];
                    if (c.getName().equals(cookie)) {
                        cookiecount++;
                    }
                }
            }


            {  // on root (keep things simple)
                Cookie c = new Cookie(cookie.getString(this), cookievalue);
                c.setPath(COOKIE_PATH);               
                c.setMaxAge(MAX_COOKIE_AGE);
                response.addCookie(c);
            }
            if (cookiecount > 1) { //also in current dir (in case it was there already)
                Cookie c = new Cookie(cookie.getString(this), cookievalue);
                c.setMaxAge(MAX_COOKIE_AGE);
                response.addCookie(c);
            }
            helper.overrideWrite(false);
        }
        return EVAL_BODY_BUFFERED;
    }

    public int doAfterBody() throws JspException {
        return helper.doAfterBody();
    }


    public int doEndTag() throws JspTagException {
        if (log.isDebugEnabled()) {
            log.debug("End writetag id: '" +getId() + "' referid: '" + getReferid() + "' value '" + value + "'");
        }
        return helper.doEndTag();
    }
}
