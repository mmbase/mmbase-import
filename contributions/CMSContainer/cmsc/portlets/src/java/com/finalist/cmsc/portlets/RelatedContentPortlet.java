package com.finalist.cmsc.portlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.portlet.ActionRequest;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.pluto.core.impl.PortletRequestImpl;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.beans.om.Page;
import com.finalist.cmsc.beans.om.Portlet;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;

public class RelatedContentPortlet extends AbstractContentPortlet {

   protected static final String RELATED_PAGE = "relatedPage";
   protected static final String RELATED_WINDOW = "relatedWindow";

   /**
    * This regex pattern is used to match the elementId from a contentURL.
    */
   private static final String CONTENTURL_ELEMENTID_PATTERN = "/content/([0-9]+)";

   @Override
   protected void saveParameters(ActionRequest request, String portletId) {
      setPortletParameter(portletId, RELATED_PAGE, request.getParameter(RELATED_PAGE));
      setPortletParameter(portletId, RELATED_WINDOW, request.getParameter(RELATED_WINDOW));
   }

   @Override
   protected void doView(RenderRequest req, RenderResponse res) throws PortletException, IOException {
      String relatedPage = req.getPreferences().getValue(RELATED_PAGE, null);
      String relatedWindow = req.getPreferences().getValue(RELATED_WINDOW, null);
      String elementId = getRelatedElementId(req, relatedPage, relatedWindow);

      if (StringUtils.isNotEmpty(elementId)) {
         setAttribute(req, ELEMENT_ID, elementId);
      }
      super.doView(req, res);
   }

   @Override
   protected void doEditDefaults(RenderRequest req, RenderResponse res) throws IOException, PortletException {
      String relatedPage = req.getPreferences().getValue(RELATED_PAGE, null);
      if (StringUtils.isNotEmpty(relatedPage)) {
         Integer pageid = Integer.valueOf(relatedPage);
         String pagepath = SiteManagement.getPath(pageid, true);
         if (pagepath != null) {
            Set<String> positions = SiteManagement.getPagePositions(relatedPage);
            List<String> orderedPositions = new ArrayList<String>(positions);
            Collections.sort(orderedPositions);
            setAttribute(req, "relatedpagepositions", new ArrayList<String>(orderedPositions));
         }
      }
      super.doEditDefaults(req, res);
   }

   /**
    * Retrieves the related elementId as a String. The default implementation
    * tries to retrieve the element in the following order:
    *
    * <ol>
    * <li>From contentelement node parameter of the specified portlet</li>
    * <li>From a given contentURL (assumes that the elementId we want is
    * exactly that elementId)</li>
    * </ol>
    *
    * The first one to return a non <code>null</code> value will be returned.
    *
    * @param request
    *           the render request
    *@param relatedPage
    *           the related page (might be <code>null</code>)
    * @param relatedWindow
    *           the related window (might be <code>null</code>)
    * @return elementId if an elementId could be found, <code>null</code>
    *         otherwise.
    */
   protected String getRelatedElementId(RenderRequest request, String relatedPage, String relatedWindow) {
      String elementId = null;
      if (StringUtils.isNotEmpty(relatedPage) && StringUtils.isNotEmpty(relatedWindow)) {
         elementId = getElementIdFromScreen(request, relatedPage, relatedWindow);
         if (StringUtils.isEmpty(elementId)) {
            elementId = getElementIdFromContentURL(request);
         }
      }

      return elementId;
   }

   private String getElementIdFromScreen(RenderRequest req, String relatedPage, String relatedWindow) {
      Integer pageId = Integer.valueOf(relatedPage);
      NavigationItem item = SiteManagement.getNavigationItem(pageId);
      if (item instanceof Page) {
         Page page = (Page) item;
         int portletId = page.getPortlet(relatedWindow);
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

   private HttpServletRequest getServletRequest(RenderRequest req) {
      return (HttpServletRequest) ((PortletRequestImpl) req).getRequest();
   }
}