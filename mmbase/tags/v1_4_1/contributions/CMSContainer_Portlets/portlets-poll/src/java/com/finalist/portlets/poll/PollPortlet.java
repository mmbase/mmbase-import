/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.portlets.poll;

import java.io.IOException;

import javax.portlet.*;

import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.*;
import org.mmbase.remotepublishing.PublishManager;
import com.finalist.cmsc.navigation.ServerUtil;
import com.finalist.cmsc.portlets.ContentPortlet;
import com.finalist.pluto.portalImpl.core.CmscPortletMode;

public class PollPortlet extends ContentPortlet {

   protected static final String ACTION_PARAM = "action";
   protected static final String CONTENTELEMENT = "contentelement";


   public void processView(ActionRequest request, ActionResponse response) throws PortletException, IOException {
      String action = request.getParameter(ACTION_PARAM);
      if (action == null) {
         response.setPortletMode(CmscPortletMode.EDIT_DEFAULTS);
      }
      else if (action.equals("edit")) {
         PortletPreferences preferences = request.getPreferences();
         String contentelement = preferences.getValue(CONTENTELEMENT, null);

         if (contentelement != null) {
            CloudProvider cloudProvider = CloudProviderFactory.getCloudProvider();
            Cloud cloud = cloudProvider.getCloud();

            String pollChoiceNumber = request.getParameter("pollChoiceNumber");
            if (pollChoiceNumber != null) {
               Node message = cloud.getNode(pollChoiceNumber);
               int counter = message.getIntValue("counter") + 1;
               message.setIntValue("counter", counter);
               message.commit();
               if (ServerUtil.isLive()) {
                  Node messageStaging = PublishManager.getSourceNode(message);
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


   public void processEdit(ActionRequest request, ActionResponse response) throws PortletException, IOException {
      super.processEdit(request, response);

      String action = request.getParameter(ACTION_PARAM);
      if (action == null) {
         response.setPortletMode(PortletMode.EDIT);
      }
      else if (action.equals("delete")) {
         String deleteNumber = request.getParameter("deleteNumber");
         CloudProvider cloudProvider = CloudProviderFactory.getCloudProvider();
         Cloud cloud = cloudProvider.getCloud();
         Node element = cloud.getNode(deleteNumber);
         element.delete(true);
      }
   }

}
