/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.portlets;

import java.io.IOException;
import java.util.*;

import javax.portlet.*;

import com.finalist.cmsc.beans.om.PortletDefinition;
import com.finalist.cmsc.beans.om.View;
import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.cmsc.services.sitemanagement.SiteManagementAdmin;
import com.finalist.pluto.portalImpl.core.CmscPortletMode;

/**
 * @author Wouter Heijke
 */
public class PortletSelectPortlet extends CmscPortlet {

   private static final String PORTLET = "portlet";
   private static final String DEFINITION_NAME = "definitionname";
   private static final String VIEW = "view";


   /**
    * @see net.sf.mmapps.commons.portlets.CmscPortlet#processEditDefaults(javax.portlet.ActionRequest,
    *      javax.portlet.ActionResponse)
    */
   @Override
   public void processEditDefaults(ActionRequest request, ActionResponse response) throws PortletException {
      getLogger().debug("===>PortletSelectPortlet.EDIT mode");
      String screenId = (String) request.getAttribute(PortalConstants.CMSC_OM_PAGE_ID);
      String layoutId = (String) request.getAttribute(PortalConstants.CMSC_OM_PORTLET_LAYOUTID);

      // get selected portlet if any..
      String action = request.getParameter("action");

      logParameters(request);
      logPreference(request);

      if (action == null) {
         response.setPortletMode(CmscPortletMode.EDIT_DEFAULTS);
      }
      else if (action.equals("edit")) {
         String selectedPortlet = request.getParameter(PORTLET);
         if (selectedPortlet != null) {
            // add portlet to screen
            SiteManagementAdmin.setScreenPortlet(screenId, selectedPortlet, layoutId);
            response.setPortletMode(CmscPortletMode.VIEW);
         }
      }
      else if (action.equals("create")) {
         String definitionName = request.getParameter(DEFINITION_NAME);
         if (definitionName != null) {
            String viewId = request.getParameter(VIEW);
            String instanceName = screenId + "_" + layoutId;
            SiteManagementAdmin.createScreenPortlet(screenId, instanceName, definitionName, layoutId, viewId);
            response.setPortletMode(CmscPortletMode.EDIT_DEFAULTS);
         }
      }
      else {
         getLogger().error("Unknown action: '" + action + "'");
      }
   }


   /**
    * @see net.sf.mmapps.commons.portlets.CmscPortlet#doEditDefaults(javax.portlet.RenderRequest,
    *      javax.portlet.RenderResponse)
    */
   @Override
   protected void doEditDefaults(RenderRequest req, RenderResponse res) throws PortletException, IOException {
      String pageId = (String) req.getAttribute(PortalConstants.CMSC_OM_PAGE_ID);
      String layoutId = (String) req.getAttribute(PortalConstants.CMSC_OM_PORTLET_LAYOUTID);

      List<PortletDefinition> portlets = SiteManagement.getSingletonPortlets(pageId, layoutId);
      List<PortletDefinition> portletdefinitions = SiteManagement.getPortletDefintions(pageId, layoutId);

      Map<PortletDefinition, List<View>> defViews = new HashMap<PortletDefinition, List<View>>();
      for (PortletDefinition definition : portletdefinitions) {
         List<View> views = SiteManagement.getViews(definition);
         defViews.put(definition, views);
      }

      setAttribute(req, "portlets", portlets);
      setAttribute(req, "definitions", portletdefinitions);

      setAttribute(req, "views", defViews);

      super.doInclude("edit_defaults", null, req, res);
   }


   @Override
   protected void doView(RenderRequest req, RenderResponse res) throws PortletException, IOException {
   }
}
