package com.finalist.newsletter.builder;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.portalImpl.PageNavigationRenderer;
import com.finalist.pluto.portalImpl.core.PortalEnvironment;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class NewsletterNavigationRenderer extends PageNavigationRenderer {
   private static Logger log = Logging.getLoggerInstance(NewsletterNavigationRenderer.class.getName());
   private static ThreadLocal<String> threadLocal = new ThreadLocal<String>();

   public String getContentType() {
      if (null != threadLocal.get()) {
         return threadLocal.get();
      } else {
         return "text/html";
      }
   }

   public void render(NavigationItem item, HttpServletRequest request, HttpServletResponse response, ServletConfig sc) throws IOException {
      log.debug(String.format("Render Newsletter of %s", request.getHeader("Content-Type")));
      Object contentType = request.getHeader("Content-Type");

      if (null == contentType) {
         contentType = request.getSession(true).getAttribute("contentType");
      }

      if (null != contentType && isSupportMIME((String) contentType)) {
         String type = (String) contentType;
         threadLocal.set(type);
         PortalEnvironment env = PortalEnvironment.getPortalEnvironment(request);
         env.setRequestedMimetype(type);
      }else{
         PortalEnvironment env = PortalEnvironment.getPortalEnvironment(request);
         env.setRequestedMimetype("text/html");
      }

      super.render(item, request, response, sc);
   }

   private boolean isSupportMIME(String s) {
      return ("text/html".equals(s) || "text/plain".equals(s));
   }
}
