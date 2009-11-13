/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.Cloud;

import java.io.IOException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.util.*;

/**
 * Provides Locale (language, country) information  to its body.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class LocaleTag extends CloudReferrerTag  {

    public static final String KEY = "javax.servlet.jsp.jstl.fmt.locale.request";
    public static final String TZ_KEY = "org.mmbase.timezone";
    public static final int SCOPE = PageContext.REQUEST_SCOPE;
    private Attribute language = Attribute.NULL;
    private Attribute country =  Attribute.NULL;
    private Attribute variant =  Attribute.NULL;

    private Attribute timezone =  Attribute.NULL;

    protected Locale locale;
    protected Locale prevCloudLocale = null;
    protected Locale prevJstlLocale = null;
    protected Cloud  cloud;
    private String jspvar = null;

    protected Set<String> varyHeaders;

    // ------------------------------------------------------------
    // Attributes (documenation can be found in tld).

    public void setLanguage(String lang) throws JspTagException {
        language = getAttribute(lang);
    }

    public void setCountry(String c) throws JspTagException {
        country = getAttribute(c);
    }

    public void setVariant(String v) throws JspTagException {
        variant = getAttribute(v);
    }
    /**
     * @since MMBase-1.8.1
     */
    public void setTimezone(String t) throws JspTagException {
        timezone = getAttribute(t);
    }

    /**
     * Child tags can call this function to obtain the Locale they must use.
     */
    public Locale getLocale() {
//        if (locale == null) {
//            locale = org.mmbase.bridge.ContextProvider.getDefaultCloudContext().getDefaultLocale();
//        }
        return locale;
    }

    public void setJspvar(String j) {
        jspvar = j;
    }


    public int doStartTag() throws JspTagException {
        determineLocale();
        if (locale != null) {
            if (jspvar != null) {
                pageContext.setAttribute(jspvar, locale);
            }
            // compatibility with jstl fmt tags:
            // should use their constant, but that would make compile-time dependency.
            prevJstlLocale = (Locale) pageContext.findAttribute(KEY);
            pageContext.setAttribute(KEY, locale, SCOPE);
            CloudProvider cloudProvider = findCloudProvider(false);
            if (cloudProvider != null) {
                cloud = cloudProvider.getCloudVar();
                prevCloudLocale = cloud.getLocale();
                cloud.setLocale(locale);
            } else {
                cloud = null;
            }
        }
        String tz = timezone.getString(this);
        if (tz.length() != 0) {
            pageContext.setAttribute(TZ_KEY, TimeZone.getTimeZone(tz), SCOPE);
        } else {
            if (pageContext.getAttribute(TZ_KEY, SCOPE) == null) {
                pageContext.setAttribute(TZ_KEY, org.mmbase.bridge.ContextProvider.getDefaultCloudContext().getDefaultTimeZone(), SCOPE);
            }
        }
        return EVAL_BODY;
    }

    /**
     * @throws JspTagException
     */
    protected void determineLocale() throws JspTagException {
        determineLocaleFromAttributes();
        if (locale == null) {
            determineFromCloudProvider();
        }
        if (locale == null) {
            locale  = (Locale) pageContext.getAttribute(KEY, SCOPE);
        }
        if (locale == null) {
            locale = org.mmbase.bridge.ContextProvider.getDefaultCloudContext().getDefaultLocale();
        }
    }

    /**
     * @throws JspTagException
     */
    protected void determineFromCloudProvider() throws JspTagException {
        CloudProvider cloudProvider = findCloudProvider(false);
        if (cloudProvider != null) {
            locale = cloudProvider.getCloudVar().getLocale();
        }
    }

    protected void addVary(String header) {
        if (varyHeaders == null) varyHeaders = new HashSet<String>();
        varyHeaders.add(header);
    }

    /**
     * @throws JspTagException
     */
    protected void determineLocaleFromAttributes() throws JspTagException {
        String l = language.getString(this);
        if (l.length() != 0) {
            if (l.equalsIgnoreCase("client")) {
                locale = pageContext.getRequest().getLocale();
                addVary("Accept-Language");
            } else {
                String c = country.getString(this);
                if ("".equals(c)) {
                    locale = org.mmbase.util.LocalizedString.getLocale(l);
                } else {
                    locale = new Locale(l, c, variant.getString(this));
                }
            }
        }
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

    public int doEndTag() throws JspTagException {
        if (locale != null) {
            if (prevCloudLocale != null) {
                if (cloud != null) {
                    cloud.setLocale(prevCloudLocale);
                }
            }
            
            if (prevJstlLocale != null) {
                pageContext.setAttribute(KEY, prevJstlLocale, SCOPE);
            } else {
                pageContext.removeAttribute(KEY, SCOPE);
            }
        }
        cloud = null;
        varyHeaders = null;
        return super.doEndTag();
    }

    public void doFinally() {
        cloud = null;
        locale = null;
        prevCloudLocale = null;
        prevJstlLocale = null;
        jspvar = null;
        super.doFinally();
    }
}

