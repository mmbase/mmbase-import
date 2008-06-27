/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.portalImpl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.*;
import javax.servlet.http.*;

import net.sf.mmapps.commons.util.HttpUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.beans.om.Site;
import com.finalist.cmsc.navigation.ServerUtil;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.pluto.portalImpl.core.*;
import com.finalist.util.version.VersionUtil;

@SuppressWarnings("serial")
public class PortalErrorServlet extends PortalServlet {

   private static Log log = LogFactory.getLog(PortalErrorServlet.class);

   public static final String ERROR_STATUS_CODE = "javax.servlet.error.status_code";
   public static final String ERROR_EXCEPTION_TYPE = "javax.servlet.error.exception_type";
   public static final String ERROR_MESSAGE = "javax.servlet.error.message";
   public static final String ERROR_EXCEPTION = "javax.servlet.error.exception";
   public static final String ERROR_REQUEST_URI = "javax.servlet.error.request_uri";

   private static final String SIMPLE_404 = "(.*/editors/.*[.](jpg$|gif$|png$|css$|js$|ico$))|robots.txt";
   
   protected ServletConfig config;
   private Pattern excludePattern = Pattern.compile(SIMPLE_404);
   
   protected static final String[] vars = { ERROR_STATUS_CODE, ERROR_EXCEPTION_TYPE, 
                                            ERROR_MESSAGE, ERROR_EXCEPTION, ERROR_REQUEST_URI };

   @Override
   public void init(ServletConfig config) {
      // do not start the portal
      this.config = config;
   }


   public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
      log.debug("===>PortalErrorServlet.doGet START!");
      
      // fail fast on static resources which are in the editors
      if (request.getHeader("Referer") != null) {
          int statusCode = (Integer) request.getAttribute(ERROR_STATUS_CODE);
          if (statusCode == 404) {
              String path = (String) request.getAttribute(ERROR_REQUEST_URI);
              if (path != null) {
                  if (excludePattern != null && excludePattern.matcher(path).find()) {
                      return;              
                  }
              }
          }
      }
      
      if (PortletContainerFactory.getPortletContainer().isInitialized()) {
          String errorUri = (String) request.getAttribute(ERROR_REQUEST_URI); 
          if (errorUri != null) {
              if (request.getContextPath() != null
                      && !request.getContextPath().equals("/")) {
                 errorUri = errorUri.substring(request.getContextPath().length());
              }
          }
          // The incoming request has a servletPath of /PortalError. The mapped url to this servlet.
          // Pretend it is  the uri which has the error in it
          HttpServletRequest errorUriRequest = new InternalDispatchNavigationRequest(request, errorUri);
          
         PortalEnvironment env = new PortalEnvironment(errorUriRequest, response, config);
         PortalURL currentURL = env.getRequestedPortalURL();
         try {
            String path = extractPath(request, currentURL);
            Integer statusCode = (Integer) request.getAttribute(ERROR_STATUS_CODE);
            log.debug("===>getErrorScreen:'" + path + "'");
            Site site = SiteManagement.getSiteFromPath(path);
            Site errorPageSite = null;
            if (site != null) {
               if (SiteManagement.isNavigation(site.getUrlfragment() + PATH_SP + statusCode)) {
                  errorPageSite = site;
               }
            }
            else {
               List<Site> sites = SiteManagement.getSites();
               for (Site site2 : sites) {
                  if (SiteManagement.isNavigation(site2.getUrlfragment() + PATH_SP + statusCode)) {
                     errorPageSite = site2;
                     break;
                  }
               }
            }
            logError(request); 
            if (errorPageSite != null) {
                HttpServletRequest errorRequest = new InternalDispatchNavigationRequest(request, errorPageSite.getUrlfragment(), String.valueOf(statusCode)); 
                PortalEnvironment errorEnv = new PortalEnvironment(errorRequest, response, config);

                response.setContentType(CONTENT_TYPE);

                String errorPagePath = errorPageSite.getUrlfragment() + PATH_SP + statusCode;
                setSiteLocale(request, errorPagePath);

                boolean renderSucceed = doRender(errorRequest, response, errorPagePath);
                if (!renderSucceed) {
                   defaultError(request, response, statusCode);
                }
            }
            else {
               defaultError(request, response, statusCode);
            }
         }
         catch (Throwable t) {
            log.fatal("Error processing", t);
         }
      }
      else {
         basicErrorPage(request, response);
      }
      log.debug("===>PortalErrorServlet.doGet EXIT!");
   }


   private void defaultError(HttpServletRequest request, HttpServletResponse response, Integer statusCode)
         throws ServletException, IOException {
      RequestDispatcher rd = config.getServletContext().getRequestDispatcher("/error/" + statusCode + ".jsp");
      if (rd != null) {
         rd.forward(request, response);
      }
      else {
         basicErrorPage(request, response);
      }
   }


   private void basicErrorPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
      response.setContentType(CONTENT_TYPE);
      PrintWriter out = response.getWriter();
      out.println("<html><head><title>" + request.getAttribute(ERROR_STATUS_CODE) + " "
            + request.getAttribute(ERROR_MESSAGE) + "</title></head><body><table>");
      // Print vars
     for (String var : vars) {
         out.println("<tr><td>" + var + "</td><td>" + request.getAttribute(var) + "</td></tr>");
      }
      out.println("</table></body></html>");
   }


   public void logError(HttpServletRequest request) {
      Integer statusCode = (Integer) request.getAttribute(ERROR_STATUS_CODE);
      if (statusCode == 500) {
         String version = VersionUtil.getApplicationVersion(config.getServletContext());
         // prepare error ticket
         long ticket = System.currentTimeMillis();

         Throwable exception = (Throwable) request.getAttribute(ERROR_EXCEPTION);
         String msg = HttpUtil.getErrorInfo(request, exception, ticket, version);

         String message = "";
         if (exception != null) {
            message = exception.getMessage();
            if (message == null) {
               message = exception.toString();
            }
         }
         // write errors to mmbase log
         log.error(ticket + ":\n" + msg);
      }
      if (statusCode == 404) {
         if (!ServerUtil.isProduction()) {
            String path = (String) request.getAttribute(ERROR_REQUEST_URI);
            log.error("missing resource: " + path);
         }
      }
   }
   
}
