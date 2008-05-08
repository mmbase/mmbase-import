/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.portalImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;

import net.sf.mmapps.commons.web.FriendlyUrlFilter;

import java.io.IOException;
import java.util.Enumeration;

/**
 * The controller filter filters out all navigation requests and dispatches them
 * through to the portal servlet.
 *
 * @author R.W. van 't Veer
 * @author Wouter Heijke
 */
public class ControllerFilter extends FriendlyUrlFilter {

   private static ThreadLocal threadLocal = new ThreadLocal();

   protected String getServlet() {
      return PortalConstants.CMSC_PORTAL_SERVLET;
   }


   protected boolean isFriendlyUrl(HttpServletRequest req, HttpServletResponse resp) {


      return PortalServlet.isNavigation(req, resp);
   }

   public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
      HttpServletRequest req = (HttpServletRequest) servletRequest;

      Object contentType = req.getSession(true).getAttribute("contenttype");
      if(null!=contentType){
         threadLocal.set(contentType);   
      }else{
         threadLocal.set(req.getHeader("content-type"));   
      }
      super.doFilter(servletRequest, servletResponse, filterChain);
   }

   public static String getContentType() {
      return (String) threadLocal.get();
   }
}
