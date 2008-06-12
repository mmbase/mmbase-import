package com.finalist.cmsc.portlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.pluto.core.impl.PortletRequestImpl;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.beans.om.Page;
import com.finalist.cmsc.beans.om.Portlet;
import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;

public class RelatedContentPortlet extends AbstractContentPortlet {

   /**
    * This regex pattern is used to match the elementId from a contentURL.
    */
   private static final String CONTENTURL_ELEMENTID_PATTERN = "/content/([0-9]+)";

   @Override
   protected void doView(RenderRequest req, RenderResponse res) throws PortletException, IOException {
      String window = req.getPreferences().getValue(WINDOW, null);
      if (StringUtils.isNotEmpty(window)) {
         String elementId = getElementIdFromRequestParameters(req, window);
         if (StringUtils.isEmpty(elementId)) {
            elementId = getElementIdFromScreen(req, window);
            if (StringUtil.isEmpty(elementId)) {
               elementId = getElementIdFromContentURL(req);
               if (StringUtils.isEmpty(elementId)) {
                  elementId = getElementId(req, window);
               }
            }
         }

         if (StringUtils.isNotEmpty(elementId)) {
            setAttribute(req, ELEMENT_ID, elementId);
         }
      }
      super.doView(req, res);
   }

   @Override
   protected void doEditDefaults(RenderRequest req, RenderResponse res) throws IOException, PortletException {
      Integer pageid = getCurrentPageId(req);
      String pagepath = SiteManagement.getPath(pageid, true);

      if (pagepath != null) {
         Set<String> positions = SiteManagement.getPagePositions(pageid.toString());
         List<String> orderedPositions = new ArrayList<String>(positions);
         Collections.sort(orderedPositions);
         setAttribute(req, "pagepositions", new ArrayList<String>(orderedPositions));
      }
      super.doEditDefaults(req, res);
   }

   /**
    * This method can be overridden to provide your own mechanism for getting an
    * elementId. The default implementation returns <code>null</code>.
    *
    * @param req
    *           originating RenderRequest
    * @param window
    *           the passed 'window' parameter.
    * @return the elementId
    */
   protected String getElementId(RenderRequest request, String window) {
      return null;
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

   private String getElementIdFromScreen(RenderRequest req, String window) {
      Integer pageId = getCurrentPageId(req);
      NavigationItem item = SiteManagement.getNavigationItem(pageId);
      if (item instanceof Page) {
         Page page = (Page) item;
         int portletId = page.getPortlet(window);
         Portlet portlet = SiteManagement.getPortlet(portletId);
         if (portlet != null) {
            return portlet.getParameterValue(CONTENTELEMENT);
         }
      }
      return null;
   }

   private String getElementIdFromContentURL(RenderRequest req) {
      String requestURL = getServletRequest(req).getRequestURL().toString();
      Pattern pattern = Pattern.compile(CONTENTURL_ELEMENTID_PATTERN);
      Matcher matcher = pattern.matcher(requestURL);
      if (matcher.find() && matcher.groupCount() >= 1) {
         return matcher.group(1);
      }
      return null;
   }

   private Integer getCurrentPageId(RenderRequest req) {
      String pageId = (String) req.getAttribute(PortalConstants.CMSC_OM_PAGE_ID);
      return Integer.valueOf(pageId);
   }

   private HttpServletRequest getServletRequest(RenderRequest req) {
      return (HttpServletRequest) ((PortletRequestImpl) req).getRequest();
   }
}