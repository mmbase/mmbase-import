/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.languageredirect;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.servlet.BridgeServlet;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.navigation.ServerUtil;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.pluto.portalImpl.core.PortalURL;

public class LanguageRedirectServlet extends BridgeServlet {

   private static final long serialVersionUID = -6415261962186866668L;

   private static Log log = LogFactory.getLog(LanguageRedirectServlet.class);
   private static final String PARAMETER_ID = "id";
   private static final String PARAMETER_LANGUAGE = "lan";


   @Override
   protected Map getAssociations() {
      Map a = super.getAssociations();
      a.put("language", Integer.valueOf(50));
      return a;
   }


   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      doRedirect(request, response);
   }


   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      doRedirect(request, response);
   }


   private void doRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
      // Check if the parameters are filled.
      if (request.getParameter(PARAMETER_LANGUAGE) == null || request.getParameter(PARAMETER_LANGUAGE).equals("")) {
         log.error("No language parameter given or empty.");
         response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No language parameter given or empty.");
         return;
      }
      String language = request.getParameter(PARAMETER_LANGUAGE);

      if (request.getParameter(PARAMETER_ID) == null || request.getParameter(PARAMETER_ID).equals("")) {
         log.error("No id parameter given or empty.");
         response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No id parameter given or empty.");
         return;
      }
      int id = Integer.parseInt(request.getParameter(PARAMETER_ID));

      String pagePath = LanguageRedirectUtil.translate(language, id);
      if (pagePath == null) {
         response.sendError(HttpServletResponse.SC_NOT_FOUND);
      }
      String redirect = getRedirectUrl(request, pagePath);
      if (redirect == null) {
         response.sendError(HttpServletResponse.SC_NOT_FOUND, "pagePath: " + pagePath);
      }
      else {
         response.sendRedirect(redirect);
      }
   }


   private String getRedirectUrl(HttpServletRequest request, String path) {
      NavigationItem item = SiteManagement.getNavigationItemFromPath(path);
      if (item == null) {
         return null;
      }
      
      String link = SiteManagement.getPath(item, !ServerUtil.useServerName());

      String host = null;
      if (ServerUtil.useServerName()) {
         host = SiteManagement.getSite(item);
      }
      PortalURL u = new PortalURL(host, request, link);
      return u.toString();
   }

}
