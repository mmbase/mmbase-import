package org.jahia.jahiawebapps.jforum;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jahia.portlet.util.StringUtil;

/**
 * HttpServletResponseWrapper that emulates a "jforum-servlet" response from
 * a RenderResponse
 *
 * @author Khaled TLILI
 */
public class HttpServletResponseWrapper extends javax.servlet.http.HttpServletResponseWrapper {
   private String redirect = null;
   private ServletOutputStream servletOutputStream;
   private static Logger logger = Logger.getLogger(HttpServletRequestWrapper.class);


   /**
    * Constructor for the HttpServletResponseWrapper object
    *
    * @param response Description of Parameter
    */
   public HttpServletResponseWrapper(HttpServletResponse response) {
      super(response);
      servletOutputStream = new ServletOutputStreamWrapper();

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
    * @param urlValue Description of Parameter
    * @return Description of the Returned Value
    */
   public String encodeURL(String urlValue) {
      return super.encodeURL(StringUtil.replaceSpecialGraphics(urlValue));
   }


   /**
    * Override send redirect
    *
    * @param url Description of Parameter
    */
   public void sendRedirect(String url) {
      logger.debug("call redirect, new url is: " + url);
      redirect = url;
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
}
