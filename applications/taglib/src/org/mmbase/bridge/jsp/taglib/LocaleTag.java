/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import java.io.IOException;
import javax.servlet.jsp.JspTagException;
import java.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Provides Locale (language, country) information  to its body. 
 *
 * @author Michiel Meeuwissen
 **/

public class LocaleTag extends ContextReferrerTag  {
    private static Logger log = Logging.getLoggerInstance(LocaleTag.class.getName());

    private String language = null;
    private String country = "";

    private Locale locale;

    // ------------------------------------------------------------
    // Attributes (documenation can be found in tld).

    public void setLanguage(String lang) throws JspTagException {
        language = getAttributeValue(lang);       
    }

    public void setCountry(String c) throws JspTagException {
        country = getAttributeValue(country);
    }

    /**
     * Child tags can call this function to obtain the Locale they must use.
     */
    public Locale getLocale() {
        if (log.isDebugEnabled()) log.debug("lang: " + locale.getLanguage() + " country: " + locale.getCountry());
        return locale;
    }
    
    
    public int doStartTag() throws JspTagException {
        if (language != null && (! language.equals(""))) {
            locale = new Locale(language, country);
        } else {
            locale = null; // default;
        }
        return EVAL_BODY_BUFFERED;
    }
    public int doAfterBody() throws JspTagException {
        try {
            bodyContent.writeOut(bodyContent.getEnclosingWriter());
            return SKIP_BODY;
        } catch (IOException ioe){
            throw new JspTagException(ioe.toString());
        }        
    }

}

