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
    public boolean mayEdit(Page page) {
        if (super.mayEdit(page)) {
            Cloud cloud = getUserCloud();
            Node pageNode = cloud.getNode(page.getId());
            UserRole userrole = NavigationUtil.getRole(pageNode.getCloud(), pageNode, false);
            return Workflow.mayEdit(pageNode, userrole);
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
