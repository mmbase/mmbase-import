package org.jahia.portlet.jforum;

import javax.portlet.*;
import javax.servlet.http.*;

import portlet.wrappers.ServletRequestWrapper;

import java.security.Principal;
import java.util.*;

import org.jahia.portlet.util.StringUtil;
import net.jforum.util.preferences.SystemGlobals;
import net.jforum.util.preferences.ConfigKeys;
import org.apache.log4j.Logger;

/**
 * HttpServletWrapper that emulates a "jforum-servlet" request from a
 * RenderRequest
 *
 * @author Khaled TLILI
 */
public class HttpServletRequestWrapper extends ServletRequestWrapper implements
         HttpServletRequest {

   private String defaultRequestUri;
   private String defaultModule;
   private String defaultAction;
   private String defaultHttpMethod;
   public final static int HTTP_GET = 0;
   public final static int HTTP_POST = 1;
   private static Logger logger = Logger.getLogger(HttpServletRequestWrapper.class);
   String postBody;


   /**
    * Constructor for the HttpServletRequestWrapper object
    *
    * @param request           render request object
    * @param defaultRequestUri defaut query string if no uri found
    * @param defaultModule     Description of Parameter
    * @param defaultAction     Description of Parameter
    */
   public HttpServletRequestWrapper(RenderRequest request, String defaultRequestUri, String defaultModule, String defaultAction, int httpMethod, String postBody) {
      super(request);
      this.defaultRequestUri = defaultRequestUri;
      this.defaultModule = defaultModule;
      this.defaultAction = defaultAction;
      this.postBody = postBody;
      if (httpMethod == HTTP_POST) {
         setPostMethod();
      } else {
         setGetMethod();
      }
   }


   /**
    * Sets the CharacterEncoding attribute of the HttpServletRequestWrapper
    * object
    *
    * @param charSet The new CharacterEncoding value
    */
   public void setCharacterEncoding(String charSet) {
      logger.debug("characterEncoding: " + charSet);
      logger.debug("cannot setCharacterEncoding on a PortletRequest!");
   }


   /**
    * Gets the RemoteAddr attribute of the HttpServletRequestWrapper object
    *
    * @return The RemoteAddr value
    */
   public String getRemoteAddr() {
      logger.debug(" cannot getRemoteAddr on a PortletRequest!");
      return "unknown";
   }


   /**
    * Gets the ContentType attribute of the HttpServletRequestWrapper object
    *
    * @return The ContentType value
    */
   public String getContentType() {
      return "application/x-www-form-urlencoded";
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
         return request.getParameterNames();
      }
   }


   /**
    * Gets the QueryString attribute of the HttpServletRequestWrapper object
    *
    * @return The QueryString value
    */
   public String getQueryString() {
      //   String s = getParameter("queryString");
//        if (s == null && isFirstAction()) {
//            s = "module=" + defaultModule + "&action=" + defaultAction;
//        }
      // return s;
      return "aaa";
   }


   /**
    * Gets the RequestURI attribute of the HttpServletRequestWrapper object
    *
    * @return The RequestURI value
    */
   public String getRequestURI() {
      return getParameter("requestURI");
   }


   /**
    * Gets the Method attribute of the HttpServletRequestWrapper object
    *
    * @return The Method value
    */
   public String getMethod() {
      String s = getParameter("httpMethod");
      if (s == null) {
         if (defaultHttpMethod == null) {
            defaultHttpMethod = "GET";
         }
         s = defaultHttpMethod;
         logger.debug("Get default HttpMetod --> [" + s + "]");
      }
      return s;
   }

   public void setPostMethod() {
      this.defaultHttpMethod = "POST";
   }

   public void setGetMethod() {
      this.defaultHttpMethod = "GET";
   }


   /**
    * Gets the Parameter attribute of the HttpServletRequestWrapper object
    *
    * @param name Description of Parameter
    * @return The Parameter value
    */
   public String getParameter(String name) {
      if ("message".equals(name)) {
         return postBody;
      }
      String value = super.getParameter(name);
      if (value == null && isFirstAction()) {
         logger.debug("value is null!! for param: " + name);
         if (name.equalsIgnoreCase("module")) {
            return defaultModule;
         } else if (name.equalsIgnoreCase("action")) {
            return defaultAction;
         } else if (name.equalsIgnoreCase("requestURI")) {
            return defaultRequestUri;
         }
      } else {
         logger.debug(" Found param: " + name + "," + value);
      }

      // fix bug: remove .page extension
      if (!name.equalsIgnoreCase("requestUri") && value != null && value.endsWith(SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION))) {
         value = value.replaceAll(SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION), "");
      }

      return value;
   }


   /**
    * Returns the name of the authentication scheme used to protect the
    * servlet.
    *
    * @return one of the static members BASIC_AUTH, FORM_AUTH,
    *         CLIENT_CERT_AUTH, DIGEST_AUTH (suitable for == comparison) or the
    *         container-specific string indicating the authentication scheme, or
    *         <code>null</code> if the request was not authenticated.
    * @todo Implement this javax.servlet.http.HttpServletRequest method
    */
   public String getAuthType() {
      return "";
   }


   /**
    * Returns the portion of the request URI that indicates the context of the
    * request.
    *
    * @return a <code>String</code> specifying the portion of the request URI
    *         that indicates the context of the request
    * @todo Implement this javax.servlet.http.HttpServletRequest method
    */
   public String getContextPath() {
      return super.request.getContextPath();
   }


   /**
    * Returns an array containing all of the <code>Cookie</code> objects the
    * client sent with this request.
    *
    * @return an array of all the <code>Cookies</code> included with this
    *         request, or <code>null</code> if the request has no cookies
    * @todo Implement this javax.servlet.http.HttpServletRequest method
    */
   public Cookie[] getCookies() {
      return null;
   }


   /**
    * Returns the value of the specified request header as a <code>long</code>
    * value that represents a <code>Date</code> object.
    *
    * @param name a <code>String</code> specifying the name of the header
    * @return a <code>long</code> value representing the date specified in
    *         the header expressed as the number of milliseconds since January 1,
    *         1970 GMT, or -1 if the named header was not included with the request
    * @todo Implement this javax.servlet.http.HttpServletRequest method
    */
   public long getDateHeader(String name) {
      return 0L;
   }


   /**
    * Returns the value of the specified request header as a <code>String</code>
    * .
    *
    * @param name a <code>String</code> specifying the header name
    * @return a <code>String</code> containing the value of the requested
    *         header, or <code>null</code> if the request does not have a header of
    *         that name
    * @todo Implement this javax.servlet.http.HttpServletRequest method
    */
   public String getHeader(String name) {
      return null;
   }


   /**
    * Returns an enumeration of all the header names this request contains.
    *
    * @return an enumeration of all the header names sent with this request;
    *         if the request has no headers, an empty enumeration; if the servlet
    *         container does not allow servlets to use this method, <code>null</code>
    * @todo Implement this javax.servlet.http.HttpServletRequest method
    */
   public Enumeration getHeaderNames() {
      return null;
   }


   /**
    * Returns all the values of the specified request header as an <code>Enumeration</code>
    * of <code>String</code> objects.
    *
    * @param name a <code>String</code> specifying the header name
    * @return an <code>Enumeration</code> containing the values of the
    *         requested header. If the request does not have any headers of that
    *         name return an empty enumeration. If the container does not allow
    *         access to header information, return null
    * @todo Implement this javax.servlet.http.HttpServletRequest method
    */
   public Enumeration getHeaders(String name) {
      if (super.request instanceof HttpServletRequest) {
         return ((HttpServletRequest) super.request).getHeaders(name);
      } else {
         return null;
      }
   }


   /**
    * Returns the value of the specified request header as an <code>int</code>.
    *
    * @param name a <code>String</code> specifying the name of a request header
    * @return an integer expressing the value of the request header or -1
    *         if the request doesn't have a header of this name
    * @todo Implement this javax.servlet.http.HttpServletRequest method
    */
   public int getIntHeader(String name) {
      return 0;
   }


   /**
    * Returns any extra path information associated with the URL the client
    * sent when it made this request.
    *
    * @return a <code>String</code>, decoded by the web container, specifying
    *         extra path information that comes after the servlet path but before
    *         the query string in the request URL; or <code>null</code> if the URL
    *         does not have any extra path information
    * @todo Implement this javax.servlet.http.HttpServletRequest method
    */
   public String getPathInfo() {
      return "";
   }


   /**
    * Returns any extra path information after the servlet name but before the
    * query string, and translates it to a real path.
    *
    * @return a <code>String</code> specifying the real path, or <code>null</code>
    *         if the URL does not have any extra path information
    * @todo Implement this javax.servlet.http.HttpServletRequest method
    */
   public String getPathTranslated() {
      return "";
   }


   /**
    * Returns the login of the user making this request, if the user has been
    * authenticated, or <code>null</code> if the user has not been
    * authenticated.
    *
    * @return a <code>String</code> specifying the login of the user making
    *         this request, or <code>null</code> if the user login is not known
    * @todo Implement this javax.servlet.http.HttpServletRequest method
    */
   public String getRemoteUser() {
      return super.request.getRemoteUser();
   }


   /**
    * Reconstructs the URL the client used to make the request.
    *
    * @return a <code>StringBuffer</code> object containing the reconstructed
    *         URL
    * @todo Implement this javax.servlet.http.HttpServletRequest method
    */
   public StringBuffer getRequestURL() {
      return null;
   }


   /**
    * Returns the session ID specified by the client.
    *
    * @return a <code>String</code> specifying the session ID, or <code>null</code>
    *         if the request did not specify a session ID
    * @todo Implement this javax.servlet.http.HttpServletRequest method
    */
   public String getRequestedSessionId() {
      return this.getSession().getId();
   }


   /**
    * Returns the part of this request's URL that calls the servlet.
    *
    * @return a <code>String</code> containing the name or path of the servlet
    *         being called, as specified in the request URL, decoded, or an empty
    *         string if the servlet used to process the request is matched using
    *         the "/*" pattern.
    * @todo Implement this javax.servlet.http.HttpServletRequest method
    */
   public String getServletPath() {
      return "";
   }


   /**
    * Returns the current session associated with this request, or if the
    * request does not have a session, creates one.
    *
    * @return the <code>HttpSession</code> associated with this request
    * @todo Implement this javax.servlet.http.HttpServletRequest method
    */
   public HttpSession getSession() {
      return new HttpSessionWrapper(super.request.
               getPortletSession());
   }


   /**
    * Returns the current <code>HttpSession</code> associated with this request
    * or, if there is no current session and <code>create</code> is true,
    * returns a new session.
    *
    * @param create <code>true</code> to create a new session for this request
    *               if necessary; <code>false</code> to return <code>null</code> if
    *               there's no current session
    * @return the <code>HttpSession</code> associated with this request
    *         or <code>null</code> if <code>create</code> is <code>false</code> and
    *         the request has no valid session
    * @todo Implement this javax.servlet.http.HttpServletRequest method
    */
   public HttpSession getSession(boolean create) {
      return new portlet.wrappers.HttpSessionWrapper(super.request.
               getPortletSession(create));
   }


   /**
    * Returns a <code>java.security.Principal</code> object containing the name
    * of the current authenticated user.
    *
    * @return a <code>java.security.Principal</code> containing the name of
    *         the user making this request; <code>null</code> if the user has not
    *         been authenticated
    * @todo Implement this javax.servlet.http.HttpServletRequest method
    */
   public Principal getUserPrincipal() {
      return super.request.getUserPrincipal();
   }


   /**
    * Checks whether the requested session ID came in as a cookie.
    *
    * @return <code>true</code> if the session ID came in as a cookie;
    *         otherwise, <code>false</code>
    * @todo Implement this javax.servlet.http.HttpServletRequest method
    */
   public boolean isRequestedSessionIdFromCookie() {
      return false;
   }


   /**
    * Checks whether the requested session ID came in as part of the request
    * URL.
    *
    * @return <code>true</code> if the session ID came in as part of a URL;
    *         otherwise, <code>false</code>
    * @todo Implement this javax.servlet.http.HttpServletRequest method
    */
   public boolean isRequestedSessionIdFromURL() {
      return false;
   }


   /**
    * @return boolean
    * @todo Implement this javax.servlet.http.HttpServletRequest method
    * @deprecated As of Version 2.1 of the Java Servlet API, use {@link
    *             #isRequestedSessionIdFromURL} instead.
    */
   public boolean isRequestedSessionIdFromUrl() {
      return false;
   }


   /**
    * Checks whether the requested session ID is still valid.
    *
    * @return <code>true</code> if this request has an id for a valid session
    *         in the current session context; <code>false</code> otherwise
    * @todo Implement this javax.servlet.http.HttpServletRequest method
    */
   public boolean isRequestedSessionIdValid() {
      return false;
   }


   /**
    * Returns a boolean indicating whether the authenticated user is included
    * in the specified logical "role".
    *
    * @param role a <code>String</code> specifying the name of the role
    * @return a <code>boolean</code> indicating whether the user making
    *         this request belongs to a given role; <code>false</code> if the user
    *         has not been authenticated
    * @todo Implement this javax.servlet.http.HttpServletRequest method
    */
   public boolean isUserInRole(String role) {
      return super.request.isUserInRole(role);
   }


   /**
    * Description of the Method
    *
    * @param newUrl Description of Parameter
    */
   public void updateHttpServletRequest(String newUrl) {

   }


   /**
    * Gets the FirstAction attribute of the HttpServletRequestWrapper object
    *
    * @return The FirstAction value
    */
   private boolean isFirstAction() {
      String requestUri = super.getParameter("requestURI");
      return requestUri == null;
   }

}
