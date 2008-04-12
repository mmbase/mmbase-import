package com.finalist.cmsc.portalImpl;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.beans.om.Layout;
import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.beans.om.Page;
import com.finalist.cmsc.beans.om.Portlet;
import com.finalist.cmsc.beans.om.PortletDefinition;
import com.finalist.cmsc.beans.om.View;
import com.finalist.cmsc.navigation.NavigationItemRenderer;
import com.finalist.cmsc.portalImpl.registry.PortalRegistry;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.pluto.portalImpl.aggregation.EmptyFragment;
import com.finalist.pluto.portalImpl.aggregation.PortletFragment;
import com.finalist.pluto.portalImpl.aggregation.ScreenFragment;

public class PageNavigationRenderer implements NavigationItemRenderer {

   private static Log log = LogFactory.getLog(PageNavigationRenderer.class);

   public void render(NavigationItem item, HttpServletRequest request, HttpServletResponse response,
         ServletConfig sc) {
      if (item instanceof Page) {
         ScreenFragment screen = getScreen((Page) item, sc);

         PortalRegistry registry = PortalRegistry.getPortalRegistry(request);
         
         registry.setScreen(screen);
         log.debug("===>SERVICE");
         try {
            screen.service(request, response);
         }
         catch (ServletException e) {
            throw new RenderException("ServletException while rendering", e);
         }
         catch (IOException e) {
            // TODO Auto-generated catch block
            throw new RenderException("IOException while rendering", e);
         }
         log.debug("===>SERVICE DONE");
      }
      else {
         throw new IllegalArgumentException(
               "Got a wrong type in the PageNavigationRenderer (only wants Page or derived classes), was"
                     + item.getClass());
      }
   }


   protected ScreenFragment getScreen(Page page, ServletConfig sc) {
      try {
         Layout layout = SiteManagement.getLayout(page.getLayout());
         ScreenFragment sf = new ScreenFragment(sc, page, layout);
         // place portletfragments and emptyfragments in the screenfragment

         Set<String> names = layout.getNames();
         for (String layoutId : names) {
            Integer portletId = page.getPortlet(layoutId);
            Portlet portlet = SiteManagement.getPortlet(portletId);
            if (portlet != null) {
               PortletDefinition definition = SiteManagement.getPortletDefinition(portlet.getDefinition());
               View view = SiteManagement.getView(portlet.getView());
               PortletFragment pf = new PortletFragment(sc, sf, layoutId, portlet, definition, view);
               if (pf != null) {
                  sf.addChild(pf);
               }
            }
            else {
               PortletFragment pf = createDefaultPortlet(sf, page, layoutId, sc);
               if (pf != null) {
                  sf.addChild(pf);
               }
            }
         }

         return sf;
      }
      catch (Exception e) {
         log.error("Error while constructing screen for page: '" + page.getId() + "'", e);
      }
      return null;
   }


   private PortletFragment createDefaultPortlet(ScreenFragment sf, Page page, String layoutId, ServletConfig sc) {
      try {
         Portlet empty = SiteManagement.getPortlet(-1);
         PortletDefinition definition = SiteManagement.getPortletDefinition(empty.getDefinition());
         page.addPortlet(layoutId, -1);
         EmptyFragment ef = new EmptyFragment(sc, sf, layoutId, empty, definition);
         return ef;
      }
      catch (Exception e) {
         log.error("cannot create default portlet");
         if (log.isDebugEnabled()) {
            log.debug(e);
         }
         return null;
      }
   }

}
