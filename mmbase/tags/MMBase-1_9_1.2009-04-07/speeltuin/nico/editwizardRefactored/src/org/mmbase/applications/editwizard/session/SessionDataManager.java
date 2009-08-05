/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard.session;

import java.net.MalformedURLException;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.mmbase.applications.editwizard.WizardException;
import org.mmbase.applications.editwizard.action.*;
import org.mmbase.applications.editwizard.util.HttpUtil;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


public class SessionDataManager {

    /** MMbase logging system */
    private static Logger log = Logging.getLoggerInstance(SessionDataManager.class.getName());

    public static SessionData getSessionData(HttpServletRequest request, String sessionKey) throws WizardException, MalformedURLException {
        HttpSession session = request.getSession();

        SessionData sessionData = null;

        // proceed with the current wizard only if explicitly stated,
        // or if this page is a debug page
        boolean proceed = "true".equals(HttpUtil.getParam(request, "proceed")) || (request.getRequestURI().endsWith("debug.jsp"));

        // Look if there is already a configuration in the session.
        Object configObject = session.getAttribute(sessionKey);
        if (proceed && configObject == null) {
            throw new WizardException("Your data cannot be found anymore, you waited too long (more than an hour), or the server was restarted");
        }

        if (configObject == null || ! (configObject instanceof SessionData) || !proceed) { // nothing (ok) in the session
            if (log.isDebugEnabled()) {
                if (proceed) {
                    // when does this happen?
                    log.debug("creating new configuration (in session is " + configObject + ")");
                } else {
                    log.debug("creating new configuration (missing proceed parameter)");
                }
            }
            sessionData = new SessionData();
            session.setAttribute(sessionKey, sessionData);  // put it in the session (if not a search window)(?)
        } else {
            log.debug("using configuration from session");
            sessionData = (SessionData) configObject;
        }

        SessionDataManager.initSessionData(request, sessionData);
        return sessionData;
    }


    private static void initSessionData(HttpServletRequest request, SessionData sessionData) throws MalformedURLException, WizardException {
        if (log.isDebugEnabled()) {
            log.debug("Sessionid : " + sessionData.getSessionId());
        }

        String templates = HttpUtil.getParam(request,"templates","");
        if (!"".equals(templates)) {
            // request contain templates parameter
            sessionData.setTemplates(templates);
        }

        String language = HttpUtil.getParam(request, "language","");
        if (sessionData.getLanguage() == null || !"".equals(language)) {
            // request contain language parameter
            sessionData.setLanguage(language);
        }

        String timezone = HttpUtil.getParam(request, "timezone", "");
        if (sessionData.getTimezone() == null || "".equals(timezone)) {
            //request contains timezone parameter or first time init
            sessionData.setTimezone(timezone);
        }

        // The editwizard need to know the 'backpage' (for 'index' and 'logout' links).
        // It can be specified by a 'referrer' parameter. If this is missing the
        // 'Referer' http header is tried.
        if (sessionData.getBackPage() == null) {
            log.debug("No backpage. Getting from parameters");
            sessionData.setBackPage(ActionUtils.getBackPage(request));
        }

        // if no 'uriResolver' is configured yet, then there is one created right now:
        // the uriResolver is used to find xml's and xsl's.
        if (sessionData.getUriResolver() == null) {
            if (log.isDebugEnabled()) {
                log.trace("creating uriresolver (backpage = " + sessionData.getBackPage() + ")");
            }
            sessionData.setUriResolver(ActionUtils.getURIResolver(request, sessionData));
        }
        int maxsize = HttpUtil.getParam(request, "maxsize", sessionData.getMaxUploadSize());
        sessionData.setMaxUploadSize(maxsize);
    }

    /**
     * find list config in stack.
     */
    public static ListConfig findListConfig(SessionData sessionDate) throws WizardException {
        return findListConfig(sessionDate, null);
    }

    /**
     * find list config in stack.
     */
    public static ListConfig findListConfig(SessionData sessionDate, String popupId) throws WizardException {
        AbstractConfig top = sessionDate.findConfig(popupId);
        if (!(top instanceof ListConfig)) {
            log.debug("The top config on the stack is not suitable for list?");
            return null;
        }
        return (ListConfig)top;
    }

    /**
     * find wizard config in stack.
     */
    public static WizardConfig findWizardConfig(SessionData sessionDate, String popupId) throws WizardException {
        AbstractConfig top = sessionDate.findConfig(popupId);
        if (!(top instanceof WizardConfig)) {
            log.debug("The top config on the stack is not suitable for wizard?");
            return null;
        }
        return (WizardConfig)top;
    }

    /**
     * create list config and store in config stack
     */
    public static ListConfig createListConfig(SessionData sessionDate, String popupId, boolean replace) throws WizardException {
        ListConfig listConfig = new ListConfig();
        if (log.isDebugEnabled()) {
            log.trace("putting new config on the stack for list " + listConfig.getTitle());
        }
        sessionDate.addConfig(listConfig, popupId, replace);
        return listConfig;
    }

    /**
     * create wizard config and store in config stack
     */
    public static WizardConfig createWizardConfig(SessionData sessionDate, String popupId) throws WizardException {
        WizardConfig wizardConfig = new WizardConfig();
        if (log.isDebugEnabled()) {
            log.trace("putting new config on the stack for wizard " + wizardConfig.objectNumber);
        }
        sessionDate.addConfig(wizardConfig, popupId, false);
        return wizardConfig;
    }

    public static AbstractConfig removeCurrentConfig(SessionData sessionData, String popupId) throws WizardException {
        AbstractConfig curConfig = sessionData.removeTop(popupId);
        if (curConfig==null) {
            curConfig = sessionData.removeTop();
        }
        if (curConfig==null) {
            throw new WizardException("could not find the wizard should be closed");
        }
        AbstractConfig config = null;
        if (popupId!=null && !"".equals(popupId)) {
            config = sessionData.getTop(popupId);
        }
        if (config == null) {
            config = sessionData.getTop();
        }
        return config;
    }

    public static void clearSession(HttpServletRequest request, String sessionKey) {
        HttpSession session = request.getSession();
        session.removeAttribute(sessionKey);
    }

    public static boolean closePopupConfig(SessionData sessionData, String popupId) {
        AbstractConfig top = sessionData.getTop();
        Stack stack = (Stack) top.getPopups().get(popupId);
        stack.pop();
        if (stack.size() == 0) {
            top.getPopups().remove(popupId);
            return true;
        }
        return false;
    }

    public static Object getPopupConfig(SessionData sessionData, String popupId) {
        AbstractConfig top = sessionData.getTop();
        Stack stack = (Stack) top.getPopups().get(popupId);
        Object closedObject = stack.peek();
        return closedObject;
    }

    public static void closeConfig(SessionData sessionData) {
        sessionData.removeTop();
    }

    public static boolean isFinished(SessionData sessionData) {
        return sessionData.isEmpty();
    }

    public static boolean isListOnTop(SessionData sessionData) {
        return sessionData.getTop() instanceof ListConfig;
    }

}
