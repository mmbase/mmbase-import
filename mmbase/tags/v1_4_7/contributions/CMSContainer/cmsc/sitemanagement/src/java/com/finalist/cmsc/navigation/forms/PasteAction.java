/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.navigation.forms;

import java.util.Iterator;

import org.mmbase.bridge.*;

import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.services.workflow.Workflow;
import com.finalist.cmsc.struts.TreePasteAction;

public class PasteAction extends TreePasteAction {

   @Override
   protected void copy(Node sourcePage, Node destPage) {
      Node newPage = NavigationUtil.copyPage(sourcePage, destPage);
      addWorkflow(newPage);
   }


   private void addWorkflow(Node newPage) {
      if (!Workflow.hasWorkflow(newPage)) {
         Workflow.create(newPage, null);
      }

      NodeList children = NavigationUtil.getOrderedChildren(newPage);
      for (Iterator<Node> iter = children.iterator(); iter.hasNext();) {
         Node childPage = iter.next();
         addWorkflow(childPage);
      }
   }


   @Override
   protected void move(Node sourcePage, Node destPage) {
      NavigationUtil.movePage(sourcePage, destPage);
      if (Workflow.isWorkflowType(sourcePage.getNodeManager().getName())) { 
	      if (!Workflow.hasWorkflow(sourcePage)) {
	         Workflow.create(sourcePage, null);
	      }
      }
      SecurityUtil.clearUserRoles(sourcePage.getCloud());
   }

}
