/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.finalist.cmsc.util.ServerUtil;
import com.finalist.pluto.portalImpl.core.PortalEnvironment;
import com.finalist.pluto.portalImpl.core.PortalURL;

public class CmscTag extends SimpleTagSupport {

   protected String getPath() {
      PageContext ctx = (PageContext) getJspContext();
      HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
      PortalEnvironment env = PortalEnvironment.getPortalEnvironment(request);
      PortalURL currentURL = env.getRequestedPortalURL();
      String path = currentURL.getGlobalNavigationAsString();
      if (ServerUtil.useServerName()) {
         path = request.getServerName() + "/" + path;
      }
      return path;
   }

}
