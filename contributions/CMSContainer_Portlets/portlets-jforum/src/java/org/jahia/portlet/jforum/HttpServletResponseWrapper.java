package org.jahia.portlet.jforum;

import javax.portlet.*;

import portlet.wrappers.ServletResponseWrapper;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.ServletOutputStream;
import java.util.StringTokenizer;
import java.util.Enumeration;

import org.jahia.portlet.util.StringUtil;
import org.apache.log4j.Logger;
import net.jforum.util.preferences.SystemGlobals;
import net.jforum.util.preferences.ConfigKeys;

/**
 * HttpServletResponseWrapper that emulates a "jforum-servlet" response from
 * a RenderResponse
 *
 * @author Khaled TLILI
 */
public class HttpServletResponseWrapper extends ServletResponseWrapper implements HttpServletResponse {
   private RenderResponse pResponse;
   private String redirect = null;
   private ServletOutputStream servletOutputStream;
   private static Logger logger = Logger.getLogger(HttpServletResponseWrapper.class);


   /**
    * Constructor for the HttpServletResponseWrapper object
    *
    * @param pResponse Description of Parameter
    */
   public HttpServletResponseWrapper(RenderResponse pResponse) {
      super(pResponse);
      this.pResponse = pResponse;
      servletOutputStream = new ServletOutputStreamWrapper();
   }


   /**
    * Sets a response header with the given name and date-value.
    *
    * @param name the name of the header to set
    * @param date the assigned date value
    * @todo Implement this javax.servlet.http.HttpServletResponse method
    */
   public void setDateHeader(String name, long date) {
   }


   /**
    * Sets a response header with the given name and value.
    *
    * @param name  the name of the header
    * @param value the header value If it contains octet string, it should be
    *              encoded according to RFC 2047 (http://www.ietf.org/rfc/rfc2047.txt)
    * @todo Implement this javax.servlet.http.HttpServletResponse method
    */
   public void setHeader(String name, String value) {
   }


   /**
    * Sets a response header with the given name and integer value.
    *
    * @param name  the name of the header
    * @param value the assigned integer value
    * @todo Implement this javax.servlet.http.HttpServletResponse method
    */
   public void setIntHeader(String name, int value) {
   }


   /**
    * @param sc the status code
    * @param sm the status message
    * @todo Implement this javax.servlet.http.HttpServletResponse method
    * @deprecated As of version 2.1, due to ambiguous meaning of the message
    *             parameter.
    */
   public void setStatus(int sc, String sm) {
   }


   /**
    * Sets the status code for this response.
    *
    * @param sc the status code
    * @todo Implement this javax.servlet.http.HttpServletResponse method
    */
   public void setStatus(int sc) {
   }


   /**
    * Gets the Redirect attribute of the HttpServletResponseWrapper object
    *
    * @return The Redirect value
    */
   public boolean isRedirect() {
      return redirect != null;
   }


   /**
    * Gets the EncodedRedirect attribute of the HttpServletResponseWrapper
    * object
    *
    * @return The EncodedRedirect value
    */
   public String getEncodedRedirect() {
      return encodeURL(redirect);
   }


   /**
    * Gets the OutputStream attribute of the HttpServletResponseWrapper object
    *
    * @return The OutputStream value
    * @throws IOException Description of Exception
    */
   public ServletOutputStream getOutputStream() throws IOException {
      return servletOutputStream;
   }


   /**
    * Description of the Method
    *
    * @param url Description of Parameter
    * @return Description of the Returned Value
    */
   public String encodeURL(String url) {
      logger.debug("Encode url: " + url);

      // replace special graphe before parsing
      url = StringUtil.replaceSpecialGraphics(url);
      //remove anchor
      int indexSharp = url.indexOf('#');
      if (indexSharp > -1) {
         Enumeration e = new StringTokenizer(url, "#");
         url = (String) e.nextElement();
         logger.debug("Found anchor: " + e.nextElement());
      } else {
         logger.debug("No anchor in " + url);
      }

      // get a portlet url
      PortletURL pUrl = pResponse.createActionURL();

      // extract uri and query
      Enumeration uriAndQuery = new StringTokenizer(url, "?");
      String uri = (String) uriAndQuery.nextElement();
      logger.debug("Found uri: " + uri);

      //add extension if not exist
      if (!uri.endsWith(SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION))) {
         uri = uri + SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION);
      }
      // set requestUri
      pUrl.setParameter("requestURI", uri);

      // set query
      if (uriAndQuery.hasMoreElements()) {

         //query is like: a='xxx'& b='xxx'&c='xxx'&d='xxx'
         String query = (String) uriAndQuery.nextElement();
         logger.debug("Found query string: " + query);
         // set query
         //upgrade to Jform 2.1.8
         //pUrl.setParameter("queryString", query);

         // paramNameAndValue is a collection if string like: a='xxx'
         Enumeration paramNameAndValue = new StringTokenizer(query, "&");
         while (paramNameAndValue.hasMoreElements()) {

            // pAnd is like: a="xxx"
            String pAndV = (String) paramNameAndValue.nextElement();
            Enumeration param = new StringTokenizer(pAndV, "=");
            // name is like: a
            String name = (String) param.nextElement();
            // value is like: xxx
            String value = (String) param.nextElement();
            pUrl.setParameter(name, value);
            logger.debug("Found param, " + name + " whith value " + value);

         }
      } else {
         logger.debug("There is no query string");
      }

      return pUrl.toString();
   }


   /**
    * Description of the Method
    *
    * @param url Description of Parameter
    */
   public void sendRedirect(String url) {
      logger.debug("call redirect, new url is: " + url);
      redirect = url;
   }


   /**
    * Adds the specified cookie to the response.
    *
    * @param cookie the Cookie to return to the client
    * @todo Implement this javax.servlet.http.HttpServletResponse
    * method
    */
   public void addCookie(Cookie cookie) {
   }


   /**
    * Adds a response header with the given name and date-value.
    *
    * @param name the name of the header to set
    * @param date the additional date value
    * @todo Implement this javax.servlet.http.HttpServletResponse method
    */
   public void addDateHeader(String name, long date) {
   }


   /**
    * Adds a response header with the given name and value.
    *
    * @param name  the name of the header
    * @param value the additional header value If it contains octet string, it
    *              should be encoded according to RFC 2047
    *              (http://www.ietf.org/rfc/rfc2047.txt)
    * @todo Implement this javax.servlet.http.HttpServletResponse method
    */
   public void addHeader(String name, String value) {
   }


   /**
    * Adds a response header with the given name and integer value.
    *
    * @param name  the name of the header
    * @param value the assigned integer value
    * @todo Implement this javax.servlet.http.HttpServletResponse method
    */
   public void addIntHeader(String name, int value) {
   }


   /**
    * Returns a boolean indicating whether the named response header has
    * already been set.
    *
    * @param name the header name
    * @return <code>true</code> if the named response header has already
    *         been set; <code>false</code> otherwise
    * @todo Implement this javax.servlet.http.HttpServletResponse method
    */
   public boolean containsHeader(String name) {
      return false;
   }


   /**
    * Encodes the specified URL for use in the <code>sendRedirect</code> method
    * or, if encoding is not needed, returns the URL unchanged.
    *
    * @param url the url to be encoded.
    * @return the encoded URL if encoding is needed; the unchanged URL
    *         otherwise.
    * @todo Implement this javax.servlet.http.HttpServletResponse method
    */
   public String encodeRedirectURL(String url) {
      return url;
   }


   /**
    * @param url the url to be encoded.
    * @return the encoded URL if encoding is needed; the unchanged URL
    *         otherwise.
    * @todo Implement this javax.servlet.http.HttpServletResponse method
    */
   public String encodeRedirectUrl(String url) {
      return url;
   }


   /**
    * @param url the url to be encoded.
    * @return the encoded URL if encoding is needed; the unchanged URL
    *         otherwise.
    * @todo Implement this javax.servlet.http.HttpServletResponse method
    */
   public String encodeUrl(String url) {
      return encodeURL(url);
   }


   /**
    * Sends an error response to the client using the specified status.
    *
    * @param sc  the error status code
    * @param msg the descriptive message
    * @throws IOException If an input or output exception occurs
    * @todo Implement this javax.servlet.http.HttpServletResponse
    * method
    */
   public void sendError(int sc, String msg) throws IOException {
   }


   /**
    * Sends an error response to the client using the specified status code and
    * clearing the buffer.
    *
    * @param sc the error status code
    * @throws IOException If an input or output exception occurs
    * @todo Implement this javax.servlet.http.HttpServletResponse
    * method
    */
	public void sendError(int sc) throws IOException {
	}

}
