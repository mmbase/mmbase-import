/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.portalImpl;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.PortletContainerException;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.beans.om.Site;
import com.finalist.cmsc.navigation.*;
import com.finalist.cmsc.services.ServiceManager;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.pluto.portalImpl.core.PortalEnvironment;
import com.finalist.pluto.portalImpl.core.PortalURL;
import com.finalist.pluto.portalImpl.core.PortletContainerEnvironment;
import com.finalist.pluto.portalImpl.core.PortletContainerFactory;
import com.finalist.pluto.portalImpl.factory.FactoryAccess;
import com.finalist.pluto.portalImpl.services.factorymanager.FactoryManager;
import com.finalist.pluto.portalImpl.services.log.CommonsLogging;

/**
 * Portal controller servlet. Alle portal requests gaan door deze servlet.
 * 
 * @author Wouter Heijke
 */
@SuppressWarnings("serial")
public class PortalServlet extends HttpServlet {

   private static Log log = LogFactory.getLog(PortalServlet.class);

   protected static String CONTENT_TYPE = "text/html";
   protected static final String PATH_SP = "/";
   protected static ServletConfig sc;


   public String getServletInfo() {
      return "CMSC Portal Driver";
   }


   public void init(ServletConfig config) throws ServletException {
      super.init(config);

      // [FP] register the navigation item managers, this should be done by the
      // modules in the future
      NavigationManager.registerNavigationManager(new SiteNavigationItemManager());
      NavigationManager.registerNavigationManager(new PageNavigationItemManager());

      PortalServlet.sc = getServletConfig();

      String charset = config.getInitParameter("charset");
      if (charset != null && charset.length() > 0) {
         CONTENT_TYPE = "text/html; charset=" + charset;
      }

      try {
         ServiceManager.init(config);
      }
      catch (Throwable exc) {
         log.error("Initialization failed!", exc);
         throw new UnavailableException("Initialization of one or more services failed.");
      }

      try {
         ServiceManager.postInit(config);
      }
      catch (Throwable expos) {
         log.error("Post initialization failed!", expos);
         throw new UnavailableException("Post initialization of one or more services failed.");
      }

      if (!PortletContainerFactory.getPortletContainer().isInitialized()) {
         String uniqueContainerName = "CMSC-Portal-Driver";

         log.info("Initializing PortletContainer [" + uniqueContainerName + "]...");

         PortletContainerEnvironment environment = new PortletContainerEnvironment();

         environment.addContainerService(new CommonsLogging());
         environment.addContainerService(FactoryManager.getService());
         environment.addContainerService(FactoryAccess.getInformationProviderContainerService());

         Properties properties = new Properties();
         properties.put("portletcontainer.supportsBuffering", Boolean.FALSE);
         try {
            PortletContainerFactory.getPortletContainer().init(uniqueContainerName, config, environment, properties);
         }
         catch (PortletContainerException exc) {
            log.error("Initialization of the portlet container failed!", exc);
            throw (new javax.servlet.UnavailableException("Initialization of the portlet container failed."));
         }
      }
      else if (log.isInfoEnabled()) {
         log.info("PortletContainer already initialized");
      }

      log.info("||| PortletContainer is ready to serve you. |||");
   }


   public void destroy() {
      log.info("Shutting down portlet container. . .");

      try {
         PortletContainerFactory.getPortletContainer().shutdown();
         // destroy all services
         ServiceManager.destroy(getServletConfig());
         System.gc();
      }
      catch (Throwable t) {
         log.error("Destruction failed!", t);
      }
   }


   public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
      log.debug("===>PortalServlet.doGet START!");
      response.setContentType(CONTENT_TYPE);
      log.debug("===>REQ spth='" + request.getServletPath() + "'");
      log.debug("===>REQ qry='" + request.getQueryString() + "'");


      PortalEnvironment env = PortalEnvironment.getPortalEnvironment(request);
      PortalURL currentURL = env.getRequestedPortalURL();
      log.debug("===>URL='" + currentURL.toString() + "'");
      log.debug("===>URL='" + currentURL.getBasePortalURL(request) + "'");
      log.debug("===>NAV='" + currentURL.getGlobalNavigationAsString() + "'");

      if (shouldRedirect(currentURL)) {
         List<Site> sites = SiteManagement.getSites();
         if (!sites.isEmpty()) {
            response.sendRedirect(sites.get(0).getUrlfragment());
            return;
         }
      }

      processRenderPhase(request, response, currentURL);
      log.debug("===>PortalServlet.doGet EXIT!");
   }
   
   /**
    * Sets the locale on the request if the site corresponding to the given path specifies one.
    */
   protected void setSiteLocale(HttpServletRequest request, String path) {
      // NIJ-519: language can be defined per site, read by CmscPortlet
      Site site = SiteManagement.getSiteFromPath(path);
      if (site != null) {
         String language = site.getLanguage();
         // NIJ-519 r2: Locale accepts anything, also whitespace
         if (!StringUtils.isBlank(language)) {
             Locale locale = new Locale(language.trim());
             request.setAttribute("siteLocale", locale);
         }
      }
   }

   private void processRenderPhase(HttpServletRequest request, HttpServletResponse response,
         PortalURL currentURL) {
      try {
         String path = extractPath(request, currentURL);
         log.debug("===>getScreen:'" + path + "'");

         setSiteLocale(request, path);

         boolean renderSucceed = doRender(request, response, path);
         if (!renderSucceed) {
            log.error("Failed to find something to render for: " + currentURL.getGlobalNavigationAsString());
         }
      }
      catch (Throwable t) {
         log.fatal("Error processing", t);
      }
   }

   protected boolean doRender(HttpServletRequest request, HttpServletResponse response, String path)
       throws IOException {
     NavigationItem item = SiteManagement.getNavigationItemFromPath(path);
     if (item != null) {
        NavigationItemRenderer manager = NavigationManager.getRenderer(item);
        if (manager != null) {
            manager.render(item, request, response, sc);
            return true;
        }
     }
      return false;
   }


   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      service(request, response);
   }


   public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      service(request, response);
   }


   private boolean shouldRedirect(PortalURL currentURL) {
      String path = currentURL.getGlobalNavigationAsString();
      if (!ServerUtil.useServerName() && (path == null || path.equals("") || path.equals(PATH_SP))) {
         return true;
      }
      return false;
   }


   public static boolean isNavigation(HttpServletRequest request, HttpServletResponse response) {
      PortalEnvironment env = new PortalEnvironment(request, response, sc);
      PortalURL currentURL = env.getRequestedPortalURL();
      String path = extractPath(request, currentURL);
      if (path == null) {
         return false;
      }
      return SiteManagement.isNavigation(path);
   }


   protected static String extractPath(HttpServletRequest request, PortalURL currentURL) {
      String path = currentURL.getGlobalNavigationAsString();
      if (ServerUtil.useServerName()) {
         path = request.getServerName() + PATH_SP + path;
      }
      else {
         if (path == null || path.equals("") || path.equals(PATH_SP)) {
            List<Site> sites = SiteManagement.getSites();
            if (!sites.isEmpty()) {
               path = sites.get(0).getUrlfragment();
            }
         }
      }
      return path;
   }
}
