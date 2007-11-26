package com.finalist.cmsc.portlets;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import net.sf.mmapps.commons.util.StringUtil;

import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.pluto.portalImpl.core.CmscPortletMode;

/**
 * Fulltext search portlet
 * 
 * @author Wouter Heijke
 * @version $Revision: 1.2 $
 */
public class SearchPortlet extends CmscPortlet {

   private static final String ACTION_PARAM = "action";

   private static final String OFFSET = "pager.offset";

   private static final String SHOW_PAGES = "showPages";

   private static final String ELEMENTS_PER_PAGE = "elementsPerPage";

   private static final String INDEX_POSITION = "position";

   private static final String MAX_ELEMENTS = "maxElements";

   private static final String VIEW = "view";

   private static final String WINDOW = "window";

   private static final String PAGE = "page";

   private static final String SEARCH_TEXT = "searchText";

   private static final String SEARCH_CATEGORY = "searchCategory";

   private static final String SEARCH_TARGET = "search.target";

   private static final String SEARCH_INDEX = "indexName";

   private static final String PAGES_INDEX = "pagesIndex";


   /**
    * @see net.sf.mmapps.commons.portlets.CmscPortlet#processEditDefaults(javax.portlet.ActionRequest,
    *      javax.portlet.ActionResponse)
    */
   @Override
   public void processEditDefaults(ActionRequest request, ActionResponse response) throws PortletException, IOException {
      getLogger().debug("===>SearchPortlet.EDIT_DEFAULTS mode");

      String action = request.getParameter(ACTION_PARAM);
      if (action == null) {
         response.setPortletMode(CmscPortletMode.EDIT_DEFAULTS);
      }
      else if (action.equals("edit")) {
         PortletPreferences preferences = request.getPreferences();
         String portletId = preferences.getValue(PortalConstants.CMSC_OM_PORTLET_ID, null);
         if (portletId != null) {
            // get the values submitted with the form
            setPortletParameter(portletId, ELEMENTS_PER_PAGE, request.getParameter(ELEMENTS_PER_PAGE));
            setPortletParameter(portletId, MAX_ELEMENTS, request.getParameter(MAX_ELEMENTS));
            setPortletParameter(portletId, SHOW_PAGES, request.getParameter(SHOW_PAGES));
            setPortletParameter(portletId, INDEX_POSITION, request.getParameter(INDEX_POSITION));
            setPortletParameter(portletId, SEARCH_INDEX, request.getParameter(SEARCH_INDEX));
            setPortletParameter(portletId, PAGES_INDEX, request.getParameter(PAGES_INDEX));

            setPortletView(portletId, request.getParameter(VIEW));

            setPortletNodeParameter(portletId, PAGE, request.getParameter(PAGE));
            setPortletParameter(portletId, WINDOW, request.getParameter(WINDOW));
         }
         else {
            getLogger().error("No portletId");
         }
         // switch to View mode
         response.setPortletMode(PortletMode.VIEW);
      }
      else {
         getLogger().error("Unknown action: '" + action + "'");
      }
   }


   @Override
   protected void doEdit(RenderRequest req, RenderResponse res) throws PortletException, IOException {
      addSearchParams(req);
      super.doEdit(req, res);
   }


   @Override
   protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
      String searchText = request.getParameter(SEARCH_TEXT);
      String searchCategory = request.getParameter(SEARCH_CATEGORY);
      if (searchText != null) {
         String searchTarget = request.getParameter(SEARCH_TARGET);
         setAttribute(request, SEARCH_TEXT, searchText);
      }
      if (searchCategory != null) {
         setAttribute(request, SEARCH_CATEGORY, searchCategory);
      }
      addSearchParams(request);
      // doSearch(request, response, searchWord, searchTarget);
      super.doView(request, response);
   }


   private void doSearch(RenderRequest request, RenderResponse response, String searchWord, String searchTarget) {
      // TODO put luceus-query here at some point to replace the luceus-taglib

   }


   private void addSearchParams(RenderRequest req) {

      PortletPreferences preferences = req.getPreferences();

      int offset = 0;
      String currentOffset = req.getParameter(OFFSET);
      if (!StringUtil.isEmpty(currentOffset)) {
         offset = Integer.parseInt(currentOffset);
      }
      setAttribute(req, "offset", offset);

      int maxElements = Integer.parseInt(preferences.getValue(MAX_ELEMENTS, "-1"));
      if (maxElements <= 0) {
         maxElements = Integer.MAX_VALUE;
      }
      int elementsPerPage = Integer.parseInt(preferences.getValue(ELEMENTS_PER_PAGE, "-1"));
      if (elementsPerPage <= 0) {
         elementsPerPage = Integer.MAX_VALUE;
      }
      elementsPerPage = Math.min(elementsPerPage, maxElements);
      setAttribute(req, ELEMENTS_PER_PAGE, elementsPerPage);

      String showPages = preferences.getValue(SHOW_PAGES, null);
      if (StringUtil.isEmpty(showPages)) {
         setAttribute(req, SHOW_PAGES, 10);
      }

      String indexPosition = preferences.getValue(INDEX_POSITION, null);
      if (StringUtil.isEmpty(indexPosition)) {
         setAttribute(req, INDEX_POSITION, "bottom");
      }

      String pagesIndex = preferences.getValue(PAGES_INDEX, null);
      if (StringUtil.isEmpty(pagesIndex)) {
         setAttribute(req, PAGES_INDEX, "center");
      }

      String indexName = preferences.getValue(SEARCH_INDEX, null);
      if (StringUtil.isEmpty(indexName)) {
         setAttribute(req, SEARCH_INDEX, "cmsc");
      }

   }

}
