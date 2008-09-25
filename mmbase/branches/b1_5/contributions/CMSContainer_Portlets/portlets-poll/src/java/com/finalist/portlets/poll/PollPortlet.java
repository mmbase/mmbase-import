/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.portlets.poll;

import java.io.IOException;

import javax.portlet.*;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.navigation.ServerUtil;
import com.finalist.cmsc.portlets.ContentPortlet;
import com.finalist.cmsc.services.publish.Publish;
import com.finalist.pluto.portalImpl.core.CmscPortletMode;

public class PollPortlet extends ContentPortlet {

   @Override
   public void processView(ActionRequest request, ActionResponse response) throws PortletException {
      String action = request.getParameter(ACTION_PARAM);
      if (action == null) {
         response.setPortletMode(CmscPortletMode.EDIT_DEFAULTS);
      }
      else if (action.equals("edit")) {
         PortletPreferences preferences = request.getPreferences();
         String contentelement = preferences.getValue(CONTENTELEMENT, null);

         if (contentelement != null) {
            Cloud cloud = getCloudForAnonymousUpdate();

            String pollChoiceNumber = request.getParameter("pollChoiceNumber");
            if (pollChoiceNumber != null) {
               Node message = cloud.getNode(pollChoiceNumber);
               int counter = message.getIntValue("counter") + 1;
               message.setIntValue("counter", counter);
               message.commit();
               if (ServerUtil.isLive()) {
                  Node messageStaging = Publish.getRemoteNode(message);
                  messageStaging.setIntValue("counter", counter);
                  messageStaging.commit();
               }
            }
         }
         else {
            getLogger().error("No contentelement");
         }
         // switch to View mode
         response.setPortletMode(PortletMode.VIEW);
      }
      else {
         getLogger().error("Unknown action: '" + action + "'");
      }
   }


   @Override
   public void processEdit(ActionRequest request, ActionResponse response) throws PortletException, IOException {
      super.processEdit(request, response);

      String action = request.getParameter(ACTION_PARAM);
      if (action == null) {
         response.setPortletMode(PortletMode.EDIT);
      }
      else if (action.equals("delete")) {
         String deleteNumber = request.getParameter("deleteNumber");
         Cloud cloud = getCloud();
         Node element = cloud.getNode(deleteNumber);
         element.delete(true);
      }
   }

}
