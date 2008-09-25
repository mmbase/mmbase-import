package com.finalist.cmsc.alias;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.alias.beans.om.Alias;
import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.navigation.*;
import com.finalist.cmsc.portalImpl.InternalDispatchNavigationRequest;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.pluto.portalImpl.core.PortalEnvironment;
import com.finalist.pluto.portalImpl.core.PortalURL;

public class AliasNavigationRenderer implements NavigationItemRenderer {

    /** MMbase logging system */
   private static final Logger log = Logging.getLoggerInstance(AliasNavigationRenderer.class.getName());

   public String getContentType() {
       return "text/html";
   }

   public void render(NavigationItem item, HttpServletRequest request, HttpServletResponse response,
           ServletConfig servletConfig) throws IOException {

      if (item instanceof Alias) {
          Alias alias = (Alias) item;
          if (alias.getPage() > 0 ) {
             NavigationItem pageItem = SiteManagement.getNavigationItem(alias.getPage());

             if (pageItem == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND,
                      "Trying to resolve Alias without related pages id:" + item.getId());
             }
             else {
                if (ServerUtil.useServerName()) {
                   String currentSite = SiteManagement.getSite(item);
                   String aliasSite = SiteManagement.getSite(pageItem);
                   if (currentSite.equals(aliasSite)) {
                      forwardInternal(request, response, servletConfig, pageItem);
                   }
                   else {
                      String link = SiteManagement.getPath(pageItem, false);
                      PortalURL u = new PortalURL(aliasSite, request, link);
                      redirectToUrl(response, u.toString());
                   }
                }
                else {
                   forwardInternal(request, response, servletConfig, pageItem);
                }
             }
         }
         else {
             String url = alias.getUrl();
             if (StringUtils.isNotBlank(url)) {
                 String redirect = response.encodeRedirectURL(url);
                 redirectToUrl(response, redirect);
             }
             else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND,
                     "Trying to resolve Alias without related pages id:" + item.getId());
             }
         }
      }
      else {
         throw new IllegalArgumentException(
               "Got a wrong type in the AliasNavigationRenderer (only wants Alias), was" + item.getClass());
      }
   }

   private void redirectToUrl(HttpServletResponse response, String redirect) {
      try {
           response.sendRedirect(redirect);
       }
       catch (IOException e) {
           log.info(e.getMessage(), e);
       }
   }

   private void forwardInternal(HttpServletRequest request, HttpServletResponse response,
         ServletConfig servletConfig, NavigationItem pageItem) throws IOException {

       NavigationItemRenderer manager = NavigationManager.getRenderer(pageItem);
       if (manager != null) {
           String contentType = manager.getContentType();
           String charset = servletConfig.getInitParameter("charset");
           if (charset != null && charset.length() > 0) {
               contentType += "; charset=" + charset;
           }
           response.setContentType(contentType);

           HttpServletRequest aliasRequest = new InternalDispatchNavigationRequest(request, pageItem);
           PortalEnvironment aliasEnv = new PortalEnvironment(aliasRequest, response);
           manager.render(pageItem, aliasRequest, response, servletConfig);
       }
   }
}
