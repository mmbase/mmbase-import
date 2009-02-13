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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.beans.om.Site;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.cmsc.util.HttpUtil;
import com.finalist.cmsc.util.ServerUtil;
import com.finalist.pluto.portalImpl.core.*;
import com.finalist.util.version.VersionUtil;

/**
 * Servlet which handles all requests which have an error.
 * This Servlet is mapped in the web.xml on all <error-page>'s
 *
 * This servlet resolves the most appropriate error page for the requested url
 * - All static resources inside the editors are not processed further
 * - the requested uri is matched against the sites.
 * - If a site matches then a child page with the statuscode as urlfragment is used as the error page
 * - No site found then the first site with a page with the statuscode as urlfragment is used as the error page
 * - No child page with the statuscode found then the default error jsp's are used
 * - When the default jsp's are missing then a basic error page is rendered.
 *
 *
 * Rendering a custom error page can result in the following exception
 * "ClientAbortException: java.net.SocketException: Software caused connection abort: socket write error"
 *
 * The error is caused by a page referring to a missing image.
 * The exception stack doesn't involve the actual error page processing. It is apparently
 * due to the browser preemptively aborting on 404. It's reasonable that it figures that
 * on 404, you're not going to return content, and the browser is going to do it's own
 * thing anyway, especially if it starts getting HTML content (our error page)
 * for an IMG tag.
 *
 * We can't distinguish between a request for an image from a referring page or a direct image
 * request. The "Referer" http header is send in both cases.
 */
@SuppressWarnings("serial")
public class PortalErrorServlet extends PortalServlet {

   private static Log log = LogFactory.getLog(PortalErrorServlet.class);

   public static final String ERROR_STATUS_CODE = "javax.servlet.error.status_code";
   public static final String ERROR_EXCEPTION_TYPE = "javax.servlet.error.exception_type";
   public static final String ERROR_MESSAGE = "javax.servlet.error.message";
   public static final String ERROR_EXCEPTION = "javax.servlet.error.exception";
   public static final String ERROR_REQUEST_URI = "javax.servlet.error.request_uri";

   private static final String SIMPLE_404 = "(.*/editors/.*[.](jpg$|gif$|png$|css$|js$|ico$))|robots.txt";

   private Pattern excludePattern = Pattern.compile(SIMPLE_404);

   protected static final String[] vars = { ERROR_STATUS_CODE, ERROR_EXCEPTION_TYPE,
                                            ERROR_MESSAGE, ERROR_EXCEPTION, ERROR_REQUEST_URI };
   @Override
   protected void startPortal() {
      // do not start the portal. We only share the render code for navigation items.
   }

   @Override
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
          // Pretend it is the uri which has the error in it
          HttpServletRequest errorUriRequest = new InternalDispatchNavigationRequest(request, errorUri);

         PortalEnvironment env = new PortalEnvironment(errorUriRequest, response);
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
                PortalEnvironment errorEnv = new PortalEnvironment(errorRequest, response);

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
      RequestDispatcher rd = getServletConfig().getServletContext().getRequestDispatcher("/error/" + statusCode + ".jsp");
      if (rd != null) {
         rd.forward(request, response);
      }
      else {
         basicErrorPage(request, response);
      }
   }


   private void basicErrorPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
      response.setContentType("text/html");
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
         String version = VersionUtil.getApplicationVersion(getServletConfig().getServletContext());
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
