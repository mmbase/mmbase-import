package com.finalist.portlets.newsletter;

import java.io.IOException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.lang.StringUtils;

import com.finalist.cmsc.beans.om.ContentElement;
import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.portlets.AbstractContentPortlet;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.cmsc.util.ServerUtil;
import com.finalist.newsletter.util.NewsletterPublicationUtil;
import com.finalist.newsletter.util.NewsletterUtil;

public class NewsletterContentPortlet extends AbstractContentPortlet {

   public static final String DEFINITION = "newslettercontentportlet";
   public static final String NEWSLETTER_TERMS_PARAM = "termNumbers";
   protected static final String DISPLAYTYPE_PARAM = "displayType";

   protected static final String USE_PAGING = "usePaging";
   protected static final String OFFSET = "pager.offset";
   protected static final String SHOW_PAGES = "showPages";
   protected static final String ELEMENTS_PER_PAGE = "elementsPerPage";
   protected static final String PAGES_INDEX = "pagesIndex";
   protected static final String INDEX_POSITION = "position";

   protected static final String KEY_DEFAULTARTICLES = "defaultarticles";
   protected static final String ARTICLES_SORT_ORDERBY = "orderby";
   protected static final String ARTICLES_SORT_DIRECTION = "direction";
   protected static final String ARCHIVE_PAGE = "archivepage";
   protected static final String START_INDEX = "startindex";
   protected static final String TOTAL_ELEMENTS = "totalElements";
   protected static final String ELEMENTS = "elements";
   protected static final String TYPES = "types";
   protected static final String MAX_ELEMENTS = "maxElements";


   @Override
   protected void doEditDefaults(RenderRequest request, RenderResponse response) throws IOException, PortletException {
      super.doEditDefaults(request, response);
   }

   @Override
   protected void doView(RenderRequest request, RenderResponse response) throws PortletException, java.io.IOException {
      String currentPath = getUrlPath(request);
      NavigationItem result = SiteManagement.getNavigationItemFromPath(currentPath);
      if (result != null) {
         int itemNumber = result.getId();
         if (!NewsletterUtil.isNewsletter(itemNumber)) {
            if (NewsletterUtil.isNewsletterPublication(itemNumber)) {
               itemNumber = NewsletterPublicationUtil.getNewsletterByPublicationNumber(itemNumber).getNumber();
            }
         }
         addContentElements(request, itemNumber);
      } else {
         throw new RuntimeException("The page number could not be found");
      }
      super.doView(request, response);
   }

   protected void addContentElements(RenderRequest req, int itemNumber) {
      String elementId = req.getParameter(ELEMENT_ID);
      if (StringUtils.isEmpty(elementId)) {
         PortletPreferences preferences = req.getPreferences();
         String portletId = preferences.getValue(PortalConstants.CMSC_OM_PORTLET_ID, null);
         List<String> contenttypes = SiteManagement.getContentTypes(portletId);

         int offset = 0;
         String currentOffset = req.getParameter(OFFSET);
         if (StringUtils.isNotEmpty(currentOffset)) {
            offset = Integer.parseInt(currentOffset);
         }
         int startIndex = Integer.parseInt(preferences.getValue(START_INDEX, "1")) - 1;
         if (startIndex > 0) {
            offset = offset + startIndex;
         }
         setAttribute(req, "offset", offset);

         String orderBy = preferences.getValue(ARTICLES_SORT_ORDERBY, null);
         String direction = preferences.getValue(ARTICLES_SORT_DIRECTION, null);
         String useLifecycle = preferences.getValue(USE_LIFECYCLE, null);

         int maxElements = Integer.parseInt(preferences.getValue(MAX_ELEMENTS, "-1"));
         if (maxElements <= 0) {
            maxElements = Integer.MAX_VALUE;
         }
         int elementsPerPage = Integer.parseInt(preferences.getValue(ELEMENTS_PER_PAGE, "-1"));
         if (elementsPerPage <= 0) {
            elementsPerPage = Integer.MAX_VALUE;
         }
         elementsPerPage = Math.min(elementsPerPage, maxElements);

         boolean useLifecycleBool = Boolean.valueOf(useLifecycle).booleanValue();
         if (useLifecycleBool && ServerUtil.isLive()) {
            // A live server will remove expired nodes.
            useLifecycleBool = false;
         }
         int totalItems = 0;
         List<ContentElement> elements = null;
         String termNumbers = req.getParameter(NEWSLETTER_TERMS_PARAM);
         if (StringUtils.isEmpty(termNumbers)) {
            totalItems = NewsletterUtil.countArticlesByNewsletter(itemNumber);
            elements = NewsletterUtil.getArticlesByNewsletter(itemNumber, offset, elementsPerPage, orderBy, direction);
         } else {
            totalItems = NewsletterUtil.countArticlesByNewsletter(itemNumber, termNumbers);
            elements = NewsletterUtil.getArticlesByNewsletter(itemNumber, termNumbers, offset, elementsPerPage, orderBy, direction);
         }

         if (startIndex > 0) {
            totalItems = totalItems - startIndex;
         }

         setAttribute(req, ELEMENTS, elements);
         if (contenttypes != null && !contenttypes.isEmpty()) {
            setAttribute(req, TYPES, contenttypes);
         }
         setAttribute(req, TOTAL_ELEMENTS, Math.min(maxElements, totalItems));
         setAttribute(req, ELEMENTS_PER_PAGE, elementsPerPage);

         String pagesIndex = preferences.getValue(PAGES_INDEX, null);
         if (StringUtils.isEmpty(pagesIndex)) {
            setAttribute(req, PAGES_INDEX, "center");
         }

         String showPages = preferences.getValue(SHOW_PAGES, null);
         if (StringUtils.isEmpty(showPages)) {
            setAttribute(req, SHOW_PAGES, 10);
         }

         boolean usePaging = Boolean.valueOf(preferences.getValue(USE_PAGING, "true"));
         if (usePaging) {
            usePaging = totalItems > elementsPerPage;
         }
         setAttribute(req, USE_PAGING, usePaging);

         String indexPosition = preferences.getValue(INDEX_POSITION, null);
         if (StringUtils.isEmpty(indexPosition)) {
            setAttribute(req, INDEX_POSITION, "bottom");
         }
      } else {
         setMetaData(req, elementId);
      }
   }

   public int getOffset(int currentPage, int pageSize) {
      return ((currentPage - 1) * pageSize) + 1;
   }

   @Override
   public void processEditDefaults(ActionRequest request, ActionResponse response) throws PortletException, IOException {
      PortletPreferences preferences = request.getPreferences();
      String portletId = preferences.getValue(PortalConstants.CMSC_OM_PORTLET_ID, null);

      String orderBy = request.getParameter(ARTICLES_SORT_ORDERBY);
      String direction = request.getParameter(ARTICLES_SORT_DIRECTION);
      setPortletParameter(portletId, ARTICLES_SORT_ORDERBY, orderBy);
      setPortletParameter(portletId, ARTICLES_SORT_DIRECTION, direction);

      setPortletParameter(portletId, USE_LIFECYCLE, request.getParameter(USE_LIFECYCLE));
      setPortletParameter(portletId, ELEMENTS_PER_PAGE, request.getParameter(ELEMENTS_PER_PAGE));
      setPortletParameter(portletId, SHOW_PAGES, request.getParameter(SHOW_PAGES));
      setPortletParameter(portletId, USE_PAGING, request.getParameter(USE_PAGING));
      setPortletParameter(portletId, PAGES_INDEX, request.getParameter(PAGES_INDEX));
      setPortletParameter(portletId, INDEX_POSITION, request.getParameter(INDEX_POSITION));
      setPortletParameter(portletId, MAX_ELEMENTS, request.getParameter(MAX_ELEMENTS));
      super.processEditDefaults(request, response);
   }
}