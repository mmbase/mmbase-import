package com.finalist.cmsc.portlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.pluto.core.impl.PortletRequestImpl;

import com.finalist.cmsc.beans.om.Portlet;
import com.finalist.cmsc.portalImpl.registry.PortalRegistry;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.pluto.portalImpl.aggregation.Fragment;
import com.finalist.pluto.portalImpl.aggregation.PortletFragment;

import net.sf.mmapps.commons.util.StringUtil;

public class RelatedContentPortlet extends AbstractContentPortlet {

   protected void doView(RenderRequest req, RenderResponse res) throws PortletException, IOException {
      String window = req.getPreferences().getValue(WINDOW, null);
      if (!StringUtil.isEmpty(window)) {
         String elementId = getElementIdFromRequestParameters(req, window);
         if (StringUtil.isEmpty(elementId)) {
            elementId = getElementIdFromScreen(req, window);
         }

         if (!StringUtil.isEmpty(elementId)) {
            setAttribute(req, ELEMENT_ID, elementId);
         }
      }
      super.doView(req, res);
   }


   private String getElementIdFromScreen(RenderRequest req, String window) {
      HttpServletRequest servletRequest = getServletRequest(req);
      PortalRegistry pr = PortalRegistry.getPortalRegistry(servletRequest);
      Fragment fragment = pr.getScreen().getFragment(window);
      if (fragment == null) {
         return null;
      }
      Portlet portlet = ((PortletFragment) fragment).getPortlet();
      if (portlet == null) {
         return null;
      }
      return portlet.getParameterValue("contentelement");

      // Fragment fragment = pr.getScreen().getFragment(window);
      // return
      // ((PortletFragment)fragment).getPortlet().getParameterValue("contentelement");
   }


   private HttpServletRequest getServletRequest(RenderRequest req) {
      return (HttpServletRequest) ((PortletRequestImpl) req).getRequest();
   }


   private String getElementIdFromRequestParameters(RenderRequest req, String window) {
      String requestURL = getServletRequest(req).getRequestURL().toString();
      String paramName = "/_rp_" + window + "_elementId/1_";
      int startIndex = requestURL.indexOf(paramName);
      if (startIndex != -1) {
         String elementId = requestURL.substring(startIndex + paramName.length());
         int endIndex = elementId.indexOf("/");
         if (endIndex != -1) {
            elementId = elementId.substring(0, endIndex);
         }
         return elementId;
      }

      return null;
   }


   @Override
   protected void doEditDefaults(RenderRequest req, RenderResponse res) throws IOException, PortletException {
      HttpServletRequest servletRequest = getServletRequest(req);
      PortalRegistry pr = PortalRegistry.getPortalRegistry(servletRequest);

      Integer pageid = pr.getScreen().getPage().getId();
      String pagepath = SiteManagement.getPath(pageid, true);

      if (pagepath != null) {
         Set<String> positions = SiteManagement.getPagePositions(pageid.toString());
         ArrayList<String> orderedPositions = new ArrayList<String>(positions);
         Collections.sort(orderedPositions);
         setAttribute(req, "pagepositions", new ArrayList<String>(orderedPositions));
      }

      super.doEditDefaults(req, res);

   }

}
