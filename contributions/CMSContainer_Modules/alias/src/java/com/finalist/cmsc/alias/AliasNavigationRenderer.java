package com.finalist.cmsc.alias;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.http.*;

import org.apache.commons.lang.StringUtils;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.alias.beans.om.Alias;
import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.navigation.*;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.pluto.portalImpl.core.PortalEnvironment;

public class AliasNavigationRenderer implements NavigationItemRenderer {

    /** MMbase logging system */
   private static Logger log = Logging.getLoggerInstance(AliasNavigationRenderer.class.getName());
    
   public String getContentType() {
       return "text/html";
   }
   
   public void render(NavigationItem item, HttpServletRequest request, HttpServletResponse response,
           ServletConfig servletConfig) throws IOException {
       
      if (item instanceof Alias) {
          Alias alias = (Alias) item;
          if (alias.getPage() > 0 ) {
             NavigationItem pageItem = SiteManagement.getNavigationItem(alias.getPage());
             String path = SiteManagement.getPath(pageItem, !ServerUtil.useServerName());

             HttpServletRequest aliasRequest = new AliasHttpServletRequest(request, path); 
             PortalEnvironment aliasEnv = new PortalEnvironment(aliasRequest, response, servletConfig);
             
             if (pageItem != null) {
                NavigationItemRenderer manager = NavigationManager.getRenderer(pageItem);
                if (manager != null) {
                    String contentType = manager.getContentType();
                    String charset = servletConfig.getInitParameter("charset");
                    if (charset != null && charset.length() > 0) {
                        contentType += "; charset=" + charset;
                    }
                    response.setContentType(contentType);
                    
                    manager.render(pageItem, aliasRequest, response, servletConfig);
                }
             }
         }
         else {
             String url = alias.getUrl();
             if (!StringUtils.isBlank(url)) {
                 String redirect = response.encodeRedirectURL(url);
                 try {
                    response.sendRedirect(redirect);
                }
                catch (IOException e) {
                    log.debug("" + e.getMessage(), e);
                }
             }
             else {
                throw new IllegalArgumentException(
                            "Trying to resolve Alias without related pages id:"+item.getId());
             }
         }
      }
      else {
         throw new IllegalArgumentException(
               "Got a wrong type in the AliasNavigationRenderer (only wants Alias), was" + item.getClass());
      }
   }


   class AliasHttpServletRequest extends HttpServletRequestWrapper {

         private String pagePath;


         public AliasHttpServletRequest(HttpServletRequest request, String pagePath) {
            super(request);
            this.pagePath = pagePath;
         }


         @Override
         public String getServletPath() {
            return pagePath;
         }

      }
}
