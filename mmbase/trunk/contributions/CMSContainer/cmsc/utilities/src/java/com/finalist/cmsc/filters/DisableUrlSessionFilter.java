/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.filters;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * This Filter disables Url Rewriting and adds Url encoding for all urls. URL
 * Encoding is a process of transforming user input to a CGI form so it is fit
 * for travel across the network -- basically, stripping spaces and punctuation
 * and replacing with escape characters. URL Decoding is the reverse process.
 * URL Rewriting is a technique for saving state information on the user's
 * browser between page hits. It's sort of like cookies, only the information
 * gets stored inside the URL, as an additional parameter. The HttpSession API,
 * which is part of the Servlet API, sometimes uses URL Rewriting when cookies
 * are unavailable.
 */
public class DisableUrlSessionFilter implements Filter {

   /**
    * Filters requests to disable URL-based session identifiers.
    */
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
         ServletException {

      if (!(request instanceof HttpServletRequest)) {
         chain.doFilter(request, response);
         return;
      }

      HttpServletRequest httpRequest = (HttpServletRequest) request;
      HttpServletResponse httpResponse = (HttpServletResponse) response;

      // clear session if session id in URL
      if (httpRequest.isRequestedSessionIdFromURL()) {
         HttpSession session = httpRequest.getSession();
         if (session != null)
            session.invalidate();
      }

      // wrap response to remove URL encoding
      HttpServletResponseWrapper wrappedResponse = new HttpServletResponseWrapper(httpResponse) {

         @Override
         public String encodeRedirectUrl(String url) {
            return getEncodedUrl(url);
         }


         @Override
         public String encodeRedirectURL(String url) {
            return getEncodedUrl(url);
         }


         @Override
         public String encodeUrl(String url) {
            return getEncodedUrl(url);
         }


         @Override
         public String encodeURL(String url) {
            return getEncodedUrl(url);
         }


         /**
          * Translates a string into
          * <code>application/x-www-form-urlencoded</code>
          * 
          * @param url
          * @return
          */
         private String getEncodedUrl(String url) {
            // try {
            // return URLEncoder.encode(url,"UTF-8");
            // }
            // catch (UnsupportedEncodingException e) {
            // // should not happen UTF-8 is mandatory
            return url;
            // }
         }
      };

      chain.doFilter(request, wrappedResponse);
   }


   public void init(FilterConfig config) {
      // nothing
   }


   public void destroy() {
      // nothing
   }
}