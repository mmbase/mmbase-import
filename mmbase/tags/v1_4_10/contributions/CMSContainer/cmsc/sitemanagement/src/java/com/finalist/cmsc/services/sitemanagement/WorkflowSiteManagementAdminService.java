/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.sitemanagement;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.beans.om.*;
import com.finalist.cmsc.navigation.NavigationUtil;

import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.services.workflow.Workflow;

public class WorkflowSiteManagementAdminService extends SiteManagementAdminServiceMMBaseImpl {

   @Override
   public boolean mayEdit(NavigationItem item) {
      if (super.mayEdit(item)) {
         Cloud cloud = getUserCloud();
         Node itemNode = cloud.getNode(item.getId());
         UserRole userrole = NavigationUtil.getRole(itemNode.getCloud(), itemNode, false);
         return Workflow.mayEdit(itemNode, userrole);
      }
      return false;
   }


   @Override
   protected void updatePage(Node page) {
      super.updatePage(page);
      if (!Workflow.hasWorkflow(page)) {
         Workflow.create(page, "");
      }
   }

}
