/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.portalImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.navigation.ServerUtil;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;

public class InternalDispatchNavigationRequest extends HttpServletRequestWrapper {

   protected static final String PATH_SP = "/";
   
   private String servletPath;
   private String serverName;

   public InternalDispatchNavigationRequest(HttpServletRequest request, String pagePath) {
      super(request);
      this.servletPath = pagePath;
   }

   public InternalDispatchNavigationRequest(HttpServletRequest request, String sitePath,
         String pagePath) {
      super(request);
      if (ServerUtil.useServerName()) {
         this.serverName = sitePath;
         this.servletPath = pagePath;
      }
      else {
         this.servletPath = sitePath + PATH_SP + pagePath;
      }
   }

   public InternalDispatchNavigationRequest(HttpServletRequest request, NavigationItem item) {
      super(request);
      this.servletPath = SiteManagement.getPath(item, !ServerUtil.useServerName());
      if (ServerUtil.useServerName()) {
         this.serverName = SiteManagement.getSite(item);
      }
   }

   @Override
   public String getServletPath() {
      return servletPath;
   }

   @Override
   public String getServerName() {
      if (serverName != null) {
         return serverName;
      }
      return super.getServerName();
   }
}
