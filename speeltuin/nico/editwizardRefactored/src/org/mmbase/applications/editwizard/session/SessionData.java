/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard.session;

import java.io.UnsupportedEncodingException;
import java.util.*;

import org.mmbase.applications.editwizard.WizardException;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.xml.URIResolver;

/**
 * This struct contains configuration information for the jsps. This
 * thing is put in the session. This object manage the stack of config
 * objects.
 *
 * @author  Michiel Meeuwissen
 * @since   MMBase-1.6
 * @version $Id: SessionData.java,v 1.4 2006-02-02 12:18:33 pierre Exp $
 */

public class SessionData {

    public static final String ATTR_URIRESOLVER = "uriresolver";
    public static final String ATTR_SESSIONID = "sessionid";
    public static final String ATTR_BACKPAGE = "referrer";
    public static final String ATTR_TEMPLATES = "templatedir";
    public static final String ATTR_LANGUAGE = "language";
    public static final String ATTR_TIMEZONE = "timezone";
    /**
     * Default maximum upload size for files (4 MB).
     */
    public final static int DEFAULT_MAX_UPLOAD_SIZE = 4 * 1024 * 1024;

    private static final Logger log = Logging.getLoggerInstance(SessionData.class);

//    private URIResolver uriResolver = null;

    private int maxupload = DEFAULT_MAX_UPLOAD_SIZE;

    // stores the Lists and Wizards' config object.
    private final Stack configStack = new Stack();

    private final Map attributes = new HashMap();

    protected String getAttribute(String attrName) {
        return (String)attributes.get(attrName);
    }

    protected void setAttribute(String attrName, String attrValue) {
        attributes.put(attrName,attrValue);
    }

    /**
     * get attribute in session scape
     * @return
     */
    public Map getAttributes() {
        return attributes;
    }

    /**
     *
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return configStack.toString();
    }

    /**
     * get config manage stack
     * @param popupId
     * @return stack of config manage
     * @throws WizardException
     */
    private Stack getConfigStack(String popupId) throws WizardException {
        AbstractConfig config = (AbstractConfig) configStack.peek();
        if (config == null) {
            //TODO: popup without parent config
            throw new WizardException("popup without parent config");
        }
        Stack stack = (Stack)config.getPopups().get(popupId);
        if (stack==null){
            log.debug("No popup stack found, create new!");
            stack = new Stack();
            config.getPopups().put(popupId,stack);
        }
        return stack;
    }

    /**
     * indicate whether the stack of configs is empty.
     * @return
     */
    boolean isEmpty() {
        return configStack.empty();
    }

    /**
     * indicate whether the stack of configs is empty.
     * @param popoupId popup's id
     * @return
     * @throws WizardException
     */
    boolean isEmpty(String popupId) throws WizardException {
        Stack stack = getConfigStack(popupId);
        return stack.empty();
    }

    /**
     * get current config in stack.
     * @return
     */
    AbstractConfig getTop() {
        if (configStack.empty()) {
            return null;
        }
        return (AbstractConfig)configStack.peek();
    }

    /**
     * get current config in stack.
     * @param popoupId popup's id
     * @return
     * @throws WizardException
     */
    AbstractConfig getTop(String popupId) throws WizardException {
        Stack stack = getConfigStack(popupId);
        if (stack.empty()) {
            return null;
        }
        return (AbstractConfig)stack.peek();
    }

    /**
     * remove current stack
     * @return
     */
    AbstractConfig removeTop() {
        if (configStack.empty()) {
            return null;
        }
        return (AbstractConfig)configStack.pop();
    }

    /**
     * remove current stack
     * @param popoupId popup's id
     * @return
     * @throws WizardException
     */
    AbstractConfig removeTop(String popupId) throws WizardException {
        Stack stack = getConfigStack(popupId);
        if (stack.empty()) {
            return null;
        }
        return (AbstractConfig)stack.pop();
    }

    /**
     * push current config into stack
     * @param config
     * @return
     */
    AbstractConfig push(AbstractConfig config) {
        return (AbstractConfig)configStack.push(config);
    }

    /**
     * push current config into stack
     * @param config
     * @return
     * @throws WizardException
     */
    AbstractConfig push(AbstractConfig config, String popupId) throws WizardException {
        Stack stack = getConfigStack(popupId);
        return (AbstractConfig)stack.push(config);
    }

    /**
     * find proper config in config stack
     * @param popupId identify name of popup window; null, if not a popup window.
     * @return current config
     * @throws WizardException
     */
    AbstractConfig findConfig(String popupId) throws WizardException{
        AbstractConfig top = null;
        boolean isPopup = isPopup(popupId);

        if (!isEmpty()) {
            if (!isPopup) {
                log.debug("This is not a popup window");
                top  = getTop();
            } else {
                log.debug("this is a popup window");
                top = getTop(popupId);
            }
        } else {
            log.debug("nothing found on stack");
            if (isPopup) {
                throw new WizardException("Popup without parent window");
            }
        }
        return top;
    }


    public void addConfig(AbstractConfig listConfig, String popupId, boolean replace) throws WizardException {
        boolean isPopup = isPopup(popupId);
        if (isPopup) {
            if (log.isDebugEnabled()) log.trace("putting new config in popup map");
            listConfig.setPopupId(popupId);
            if (replace && isEmpty(popupId)==false) {
                removeTop(popupId);
            }
            push(listConfig,popupId);
        } else {
            if (log.isDebugEnabled()) log.trace("putting new config on the stack");
            push(listConfig);
        }
    }

    /**
     * get previous config.
     * @param popupId
     * @return
     * @throws WizardException
     */
    AbstractConfig getPrevConfig(String popupId) throws WizardException {
        boolean isPopup = isPopup(popupId);
        AbstractConfig config = null;
        if (isPopup) {
            config = getTop(popupId);
        }
        if (config==null) {
            config = getTop();
        }
        return config;
    }

    /**
     * indicate whether it is a popup window.
     * @param popupId
     * @return
     */
    private boolean isPopup(String popupId) {
        return (popupId!=null) && ("".equals(popupId)==false);

    }

    /**
     * @param maxupload The maxupload to set.
     */
    public void setMaxUploadSize(int maxupload) {
        this.maxupload = maxupload;
    }

    /**
     * @return Returns the maxupload.
     */
    public int getMaxUploadSize() {
        return maxupload;
    }

    /**
     * @param uriResolver The uriResolver to set.
     */
    public void setUriResolver(URIResolver uriResolver) {
        attributes.put(ATTR_URIRESOLVER, uriResolver);
    }

    /**
     * @return Returns the uriResolver.
     */
    public URIResolver getUriResolver() {
        return (URIResolver) attributes.get(ATTR_URIRESOLVER);
    }

    /**
     * @param sessionId The sessionId to set.
     */
    public void setSessionId(String sessionId) {
        setAttribute(ATTR_SESSIONID, sessionId);
    }

    /**
     * @return Returns the sessionId.
     */
    public String getSessionId() {
        return getAttribute(ATTR_SESSIONID);
    }

    /**
     * @param backPage The backPage to set.
     */
    public void setBackPage(String backPage) {
        backPage = backPage.replace('\\','/');
        setAttribute(ATTR_BACKPAGE, backPage);
        try {
            setAttribute("referrer_encoded", java.net.URLEncoder.encode(backPage,"UTF8"));
        }
        catch (UnsupportedEncodingException e) {
            log.debug("What happened to the required encodings?" + e.getMessage(), e);
        }
    }

    /**
     * @return Returns the backPage.
     */
    public String getBackPage() {
        return getAttribute(ATTR_BACKPAGE);
    }

    /**
     * @param templates The templates to set.
     */
    public void setTemplates(String templates) {
        setAttribute(ATTR_TEMPLATES, templates);
    }

    /**
     * @return Returns the templates.
     */
    public String getTemplates() {
        return getAttribute(ATTR_TEMPLATES);
    }

    /**
     * @param language The language to set.
     */
    public void setLanguage(String language) {
        setAttribute(ATTR_LANGUAGE, language);
    }

    /**
     * @return Returns the language.
     */
    public String getLanguage() {
        return getAttribute(ATTR_LANGUAGE);
    }

    /**
     * @param timezone The timezone to set.
     */
    public void setTimezone(String timezone) {
        setAttribute(ATTR_TIMEZONE, timezone);
    }

    /**
     * @return Returns the timezone.
     */
    public String getTimezone() {
        return getAttribute(ATTR_TIMEZONE);
    }

}
