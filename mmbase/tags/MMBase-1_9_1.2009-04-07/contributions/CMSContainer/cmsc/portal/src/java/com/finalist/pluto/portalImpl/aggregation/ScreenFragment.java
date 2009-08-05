package com.finalist.pluto.portalImpl.aggregation;

import java.io.IOException;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.beans.om.Layout;
import com.finalist.cmsc.beans.om.Page;
import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.services.security.LoginSession;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;

/**
 * @author Wouter Heijke
 */
public class ScreenFragment extends AbstractFragment {
   private static Log log = LogFactory.getLog(ScreenFragment.class);

   private List<PortletFragment> children = new ArrayList<PortletFragment>();

   private Page page;
   private Layout layout;


   public ScreenFragment(ServletConfig config, Page page, Layout layout) throws Exception {
      super(null, config, null);
      this.page = page;
      this.layout = layout;
      log.debug("Create - page: " + page.getId() + " layout: " + page.getLayout());
   }


   public void processAction(HttpServletRequest request, HttpServletResponse response, String actionFragment)
         throws IOException {
      PortletFragment pf = getFragment(actionFragment);
      setupRequest(request);
      pf.processAction(request, response);
      cleanRequest(request);
   }


   public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      setupRequest(request);

      int expirationCache = -1;

      Iterator<PortletFragment> portlets = this.getChildFragments().iterator();
      while (portlets.hasNext()) {
          PortletFragment portlet = portlets.next();
         // let the Portlet do it's thing
         portlet.service(request, response);

         int portletExpiration = portlet.getExpirationCache();
         if (portletExpiration > -1) {
            if (expirationCache == -1) {
               expirationCache = portletExpiration;
            }
            else {
               expirationCache = Math.min(expirationCache, portletExpiration);
            }
         }
      }

      LoginSession ls = SiteManagement.getLoginSession(request);
      if (!ls.isAuthenticated()) {
         if (expirationCache > -1) {
            response.setHeader("Cache-Control", "maxage=" + expirationCache);
            if (expirationCache > 30) {
               /*
               If a response includes both an Expires header and a max-age
               directive, the max-age directive overrides the Expires header, even
               if the Expires header is more restrictive.  This rule allows an
               origin server to provide, for a given response, a longer expiration
               time to an HTTP/1.1 (or later) cache than to an HTTP/1.0 cache.  This
               might be useful if certain HTTP/1.0 caches improperly calculate ages
               or expiration times, perhaps due to desynchronized clocks.
               */
               long now = System.currentTimeMillis();
               response.setDateHeader("Date", now);
               long expires = now + (expirationCache * 1000); // seconds to milliseconds
               response.setDateHeader("Expires", expires);
            }
         }
      }

      if (page != null) {
         if (layout != null) {
             log.debug("using layout:'" + layout.getResource() + "' for page:'" + page.getTitle() + "'");

             request.setAttribute(PortalConstants.FRAGMENT, this);

             FragmentResouceRender render = FragmentResouceRenderFactory.getRender(layout.getResource());

             if (null != render) {
                 render.render(layout.getResource(), request, response);
             } else {
                 RequestDispatcher rd = getMainRequestDispatcher(layout.getResource(), response.getContentType());
                 rd.include(request, response);
             }
             request.removeAttribute(PortalConstants.FRAGMENT);
         } else {
            log.error("No layout for Screen");
         }
      }
      else {
         log.error("No page for ScreenFragment");
      }

      cleanRequest(request);
   }


   public void setupRequest(HttpServletRequest request) {
      request.setAttribute(PortalConstants.CMSC_OM_PAGE_ID, String.valueOf(getPage().getId()));
   }

   private void cleanRequest(HttpServletRequest request) {
       request.removeAttribute(PortalConstants.CMSC_OM_PAGE_ID);
   }


   public Page getPage() {
      return page;
   }


   public Layout getLayout() {
      return layout;
   }


   public String getKey() {
      return this.getId();
   }


   public Collection<PortletFragment> getChildFragments() {
      return children;
   }


   public void addChild(PortletFragment child) {
      children.add(child);
   }


   public PortletFragment getFragment(String id) {
      PortletFragment fragment = null;
      Iterator<PortletFragment> iterator = this.getChildFragments().iterator();
      while (iterator.hasNext()) {
         PortletFragment tmp = iterator.next();
         if (tmp != null) {
            if (tmp.getKey().equalsIgnoreCase(id)) {
               fragment = tmp;
               break;
            }
         }
      }
      log.debug("getFragment: '" + id + "':'" + fragment + "'");
      return fragment;
   }

    @Override
    public String toString() {
        return String.format(
                "Screen[page:%s(%s) layout:%s(%s)]",
                page.getTitle(), page.getId(), layout.getNames(), layout.getId()
        );
    }
}
