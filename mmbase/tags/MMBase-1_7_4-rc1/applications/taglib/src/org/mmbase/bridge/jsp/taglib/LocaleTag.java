/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import java.io.IOException;
import javax.servlet.jsp.JspTagException;
import java.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Provides Locale (language, country) information  to its body. 
 *
 * @author Michiel Meeuwissen
 * @version $Id: LocaleTag.java,v 1.13 2004-03-23 21:42:47 michiel Exp $ 
 */

public class LocaleTag extends ContextReferrerTag  {
    private static final Logger log = Logging.getLoggerInstance(LocaleTag.class);

    private Attribute language = Attribute.NULL;
    private Attribute country =  Attribute.NULL;

    protected Locale locale;
    private String jspvar = null;

    // ------------------------------------------------------------
    // Attributes (documenation can be found in tld).

    public void setLanguage(String lang) throws JspTagException {
        language = getAttribute(lang);       
    }

    public void setCountry(String c) throws JspTagException {
        country = getAttribute(c);
    }

    /**
     * Child tags can call this function to obtain the Locale they must use.
     */
    public Locale getLocale() {
        if (log.isDebugEnabled()) { 
            log.debug("lang: " + locale.getLanguage() + " country: " + locale.getCountry());
        }
        return locale;
    }

    public void setJspvar(String j) {
        jspvar = j;
    }
    
    
    public int doStartTag() throws JspTagException {
        String l = language.getString(this);
        if (! l.equals("")) {
            locale = new Locale(l, country.getString(this));
        } else {
            locale = org.mmbase.bridge.ContextProvider.getDefaultCloudContext().getDefaultLocale();
        }
        if (jspvar != null) {
            pageContext.setAttribute(jspvar, locale);
        }
        return EVAL_BODY;
    }
    public int doAfterBody() throws JspTagException {
        if (EVAL_BODY == EVAL_BODY_BUFFERED) {
            if (bodyContent != null) {
                try {
                    bodyContent.writeOut(bodyContent.getEnclosingWriter());
                } catch (IOException ioe){
                    throw new TaglibException(ioe);
                }        
            }
        }
        return SKIP_BODY;
    }

}

