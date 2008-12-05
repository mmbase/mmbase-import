/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.portlets;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.pluto.portalImpl.core.CmscPortletMode;

/**
 * @author Wouter Heijke
 */
public class JspPortlet extends CmscPortlet {

   private static final String VIEW = "view";

   /**
    * @see com.finalist.cmsc.portlets.CmscPortlet#processEditDefaults(javax.portlet.ActionRequest,
    *      javax.portlet.ActionResponse)
    */
   @Override
   public void processEditDefaults(ActionRequest request, ActionResponse response)
         throws PortletException, IOException {
      getLogger().debug("===>MenuPortlet.EDIT mode");
      PortletPreferences preferences = request.getPreferences();
      String portletId = preferences.getValue(PortalConstants.CMSC_OM_PORTLET_ID, null);

      String action = request.getParameter("action");
      if (action == null) {
         response.setPortletMode(CmscPortletMode.EDIT_DEFAULTS);
      }
      else
         if (action.equals("edit")) {
            if (portletId != null) {
               // get the values submitted with the form
               setPortletView(portletId, request.getParameter(VIEW));
            }
            else {
               getLogger().error("No portletId");
            }
         }
         else {
            getLogger().error("Unknown action: '" + action + "'");
         }
      super.processEditDefaults(request, response);
   }

   @Override
   protected void doEditDefaults(RenderRequest req, RenderResponse res) throws IOException,
         PortletException {
      addViewInfo(req);
      super.doEditDefaults(req, res);
   }

}
