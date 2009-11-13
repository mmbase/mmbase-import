/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * An utility to work with clouds on the session or as a ThreadLocal
 * @since MMBase-1.9
 * @version $Id$
 */
public class CloudUtil {

    public static final String DEFAULT_SESSIONNAME = "cloud_mmbase";
    public static final String DEFAULT_CLOUD_NAME = "mmbase";
    public static final String DEFAULT_AUTHENTICATION = "name/password";

    private static final Logger log = Logging.getLoggerInstance(CloudUtil.class);

    private CloudUtil() {
        //Utility
    }

    /**
     * Create a new cloud in the session based on the info of the request (username/password)
     * The cloud is stored in the session under the DEFAULT_SESSIONNAME
     * @param req HttpServletRequest
     * @return The new cloud
     */
    public static Cloud createCloud(HttpServletRequest req) {
        return createCloud(req, DEFAULT_SESSIONNAME);
    }

    /**
     * Create a new cloud in the session based on the info of the request (username/password)
     * @param req HttpServletRequest
     * @param sessionname session attribute name to store the cloud in.
     * @return The new cloud
     */
    public static Cloud createCloud(HttpServletRequest req, String sessionname) {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        Cloud cloud = null;
        if (!isEmptyOrWhitespace(username) && !isEmptyOrWhitespace(password)) {
            String authenticate = req.getParameter("authenticate");
            final Map<String, String> loginInfo = getUserCredentials(username, password);

            cloud = createCloud(req, sessionname, authenticate, loginInfo);
        }

        return cloud;
    }

    /**
     * Create a new cloud in the session based on the info of the request (username/password)
     * @param req HttpServletRequest
     * @param authenticate Authentication method (eg. "name/password")
     * @param loginInfo Map with login information
     * @return The new cloud
     */
    public static Cloud createCloud(HttpServletRequest req, String authenticate, final Map<String, ?> loginInfo) {
        return createCloud(req, DEFAULT_SESSIONNAME, authenticate, loginInfo);
    }

    /**
     * Create a new cloud in the session based on the info of the request (username/password)
     * @param req HttpServletRequest
     * @param sessionname session attribute name to store the cloud in.
     * @param authenticate Authentication method (eg. "name/password")
     * @param loginInfo Map with login information
     * @return The new cloud
     */
    public static Cloud createCloud(HttpServletRequest req, String sessionname, String authenticate, final Map<String, ?> loginInfo) {
        String cloudName = req.getParameter("cloud");
        if (isEmptyOrWhitespace(cloudName)) {
            cloudName = DEFAULT_CLOUD_NAME;
        }
        if (isEmptyOrWhitespace(authenticate)) {
            authenticate = DEFAULT_AUTHENTICATION;
        }

        final CloudContext context = ContextProvider.getCloudContext("local");
        Cloud cloud = context.getCloud(cloudName, authenticate, loginInfo);

        HttpSession session = req.getSession();
        if (session != null) {
            session.setAttribute(sessionname, cloud);
        } else {
            log.warn("Could not get or create a session to put the cloud on.");
        }
        return cloud;
    }

    /**
     * Get Map with login credentials to pass to MMbase bridge
     * @param username username for login
     * @param password password for login
     * @return Map with Login information
     */
    public static Map<String, String> getUserCredentials(String username, String password) {
        Map<String, String> result = new HashMap<String, String>(3, 0.7f);
        result.put("username", username);
        result.put("password", password);
        return result;
     }

    /**
     * Get cloud from session. The DEFAULT_SESSIONNAME is used as attribute name
     * @param request HttpServletRequest
     * @return Stored cloud
     */
    public static Cloud getCloudFromSession(HttpServletRequest request) {
        return getCloudFromSession(request, DEFAULT_SESSIONNAME);
    }

    /**
     * Get cloud from session. The DEFAULT_SESSIONNAME is used as attribute name
     * @param request HttpServletRequest
     * @param sessionname session attribute name to retrieve the cloud from
     * @return Stored cloud
     */
    public static Cloud getCloudFromSession(HttpServletRequest request, String sessionname) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Cloud cloud = (Cloud) session.getAttribute(sessionname);
            if (cloud != null) {
                log.debug("cloud for user " + cloud.getUser().getIdentifier());
                return cloud;
            }
        }
        return null;
    }

    /**
     * Checks if a cloud is on the session with given the default sessionname.
     * @param request HttpServletRequest to search for the cloud.
     * @return true if a cloud is found, false otherwise.
     */
    public static boolean hasCloud(HttpServletRequest request) {
       return hasCloud(request, DEFAULT_SESSIONNAME);
    }


    /**
     * Checks if a cloud is on the session with given sessionname.
     * @param request HttpServletRequest to search for the cloud.
     * @param sessionname The name of the cloud on the session.
     * @return true if a cloud is found, false otherwise.
     */
    public static boolean hasCloud(HttpServletRequest request, String sessionname) {
       HttpSession session = request.getSession(false);
       if (session != null) {
          Cloud cloud = (Cloud) session.getAttribute(sessionname);
          if (cloud != null) {
             log.debug("cloud for user " + cloud.getUser().getIdentifier());
             return true;
          }
       }
       return false;
    }

    /**
     * Add cloud from the http session to the current thread
     * @param request HttpServletRequest with the session
     */
    public static void addCloudToThread(HttpServletRequest request) {
        if (hasCloud(request)) {
            Cloud cloud = getCloudFromSession(request);
            addCloudToThread(cloud);
        }
    }

    /**
     * Add cloud to the current thread
     * @param cloud cloud to add to thread
     */
    public static void addCloudToThread(Cloud cloud) {
        CloudThreadLocal.bind(cloud);
    }

    /**
     * Remove cloud from current thread
     */
    public static void removeCloudFromThread() {
        CloudThreadLocal.unbind();
    }

    /**
     * Get cloud from current thread
     * @return Cloud from thread
     */
    public static Cloud getCloudFromThread() {
        return CloudThreadLocal.currentCloud();
    }

    /**
     * is Empty Or Whitespace.String
     *
     * @param str String to check emptiness
     * @return boolean is it empty
     */
    public static boolean isEmptyOrWhitespace(String str) {
        return (str == null) || "".equals(str.trim());
    }
}
