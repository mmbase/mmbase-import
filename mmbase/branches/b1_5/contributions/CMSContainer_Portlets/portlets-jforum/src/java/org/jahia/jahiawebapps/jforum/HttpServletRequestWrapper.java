package org.jahia.jahiawebapps.jforum;

import javax.servlet.http.*;
import java.util.*;

import net.jforum.util.preferences.SystemGlobals;
import net.jforum.util.preferences.ConfigKeys;
import org.jahia.portlet.util.StringUtil;
import org.apache.log4j.Logger;

/**
 * HttpServletWrapper that emulates a "jforum-servlet" request from a real
 * HttpServletRequest
 *
 * @author Khaled TLILI
 */
public class HttpServletRequestWrapper extends javax.servlet.http.HttpServletRequestWrapper {
    private String defaultRequestUri;
    private String defaultModule;
    private String defaultAction;
    private HttpSessionWrapper session;
    private static Logger logger = Logger.getLogger(HttpServletRequestWrapper.class);


    /**
     * Constructor for the HttpServletRequestWrapper object
     *
     * @param request           render request object
     * @param defaultRequestUri defaut query string if no uri found
     * @param defaultModule     Description of Parameter
     * @param defaultAction     Description of Parameter
     */
    public HttpServletRequestWrapper(HttpServletRequest request, String defaultRequestUri, String defaultModule, String defaultAction) {
        super(request);
        this.defaultRequestUri = defaultRequestUri;
        this.defaultModule = defaultModule;
        this.defaultAction = defaultAction;
        session = new HttpSessionWrapper(super.getSession());

    }


    /**
     * Gets the ParameterNames attribute of the HttpServletRequestWrapper object
     *
     * @return The ParameterNames value
     */
    public Enumeration getParameterNames() {
        Enumeration superParamNames = super.getParameterNames();
        if (isFirstAction()) {
            // at least module and action param name exist
            String strgToken = "module&action";

            // add others name parameters
            if (superParamNames != null) {
                while (superParamNames.hasMoreElements()) {
                    String name = (String) superParamNames.nextElement();
                    if (!name.equalsIgnoreCase("module") &&
                            !name.equalsIgnoreCase("action")) {
                        strgToken = strgToken + "&" + name;
                    }
                }
            }

            // build enumearaiton objct
            Enumeration finalParamNames = new StringTokenizer(strgToken, "&");
            return finalParamNames;
        } else {
            return super.getParameterNames();
        }
    }


    /**
     * Gets the QueryString attribute of the HttpServletRequestWrapper object
     *
     * @return The QueryString value
     */
    public String getQueryString() {
        String s = super.getQueryString();
        if (isFirstAction()) {
            s = "module=" + defaultModule + "&action=" + defaultAction;
        }

        return StringUtil.replaceSpecialGraphics(s);
    }


    /**
     * Gets the RequestURI attribute of the HttpServletRequestWrapper object
     *
     * @return The RequestURI value
     */
    public String getRequestURI() {
        String requestURI = super.getRequestURI();
        if (isFirstAction()) {
            logger.debug("Real requestURI is: " + requestURI + ". replaced whith: " + defaultRequestUri);
            return defaultRequestUri;
        } else {
            // e.g.: jforum.page#3 --> jforum.page
            StringTokenizer st = new StringTokenizer(requestURI, "#");
            requestURI = (String) st.nextToken();
            return requestURI;
        }
    }

    /**
     *  Gets the Method attribute of the HttpServletRequestWrapper object
     */
    /*
      *  public String getMethod() {
      *  String s = getParameter("httpMethod");
      *  if (s == null) {
      *  s = "GET";
      *  }
      *  return s;
      *  }
      */
    /**
     * Gets the Parameter attribute of the HttpServletRequestWrapper object
     *
     * @param name Description of Parameter
     * @return The Parameter value
     */
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if (isFirstAction()) {
            logger.debug("value is null!! for param: " + name);
            if (name.equalsIgnoreCase("module")) {
                logger.debug("Use default value for param: " + name + "," + defaultModule);
                return defaultModule;
            } else if (name.equalsIgnoreCase("action")) {
                logger.debug("Use default value for param: " + name + "," + defaultAction);
                return defaultAction;
            } else {
                logger.debug("No default value for param: " + name);
            }
        } else {
            logger.debug(" Found param: " + name + "," + value);
        }

        // fix bug: remove .page extension
        if (value.endsWith(SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION))) {
            value = value.replaceAll(SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION), "");
        }

        return value;
    }


    /**
     * Gets the Session attribute of the HttpServletRequestWrapper object
     *
     * @return The Session value
     */
    public HttpSession getSession() {
        return session;
    }


    /**
     * Gets the Session attribute of the HttpServletRequestWrapper object
     *
     * @param newSession Description of Parameter
     * @return The Session value
     */
    public HttpSession getSession(boolean newSession) {
        return new HttpSessionWrapper(super.getSession(newSession));
    }


    /**
     * Gets the FirstAction attribute of the HttpServletRequestWrapper object
     *
     * @return The FirstAction value
     */
    private boolean isFirstAction() {
        String requestUri = super.getRequestURI();
        // jforum.page#3 --> jforum.page
        StringTokenizer st = new StringTokenizer(requestUri, "#");
        requestUri = (String) st.nextToken();
        return !requestUri.endsWith(SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION));
    }


    public String getRemoteAddr() {
        String remoteAddr =  super.getRemoteAddr();
        if (remoteAddr == null) {
            return "unknown";
        }

        return remoteAddr;
    }
}
