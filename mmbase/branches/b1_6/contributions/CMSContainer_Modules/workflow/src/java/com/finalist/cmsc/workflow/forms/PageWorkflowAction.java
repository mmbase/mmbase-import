/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.workflow.forms;


import javax.servlet.http.HttpServletRequest;

import org.mmbase.bridge.*;

import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.workflow.*;

public class PageWorkflowAction extends WorkflowAction {

   @Override
   protected String getWorkflowType() {
      return PageWorkflow.TYPE_PAGE;
   }

   @Override
   protected NodeQuery createDetailQuery(Cloud cloud, String orderby, boolean aord) {
      NodeManager manager = cloud.getNodeManager(PagesUtil.PAGE);
      NodeQuery wfQuery = WorkflowManager.createDetailQuery(cloud, manager);

      wfQuery.addField(PagesUtil.PAGE + ".number");
      wfQuery.addField(PagesUtil.PAGE + "." + PagesUtil.TITLE_FIELD);
      wfQuery.addField(PagesUtil.PAGE + "." + PagesUtil.LASTMODIFIEDDATE_FIELD);
      wfQuery.addField(PagesUtil.PAGE + "." + PagesUtil.LASTMODIFIER_FIELD);

      if (orderby.equals(PagesUtil.TITLE_FIELD)) {
         addOrderBy(manager, wfQuery, PagesUtil.TITLE_FIELD, aord);
      }
      else if (orderby.equals(PagesUtil.LASTMODIFIER_FIELD)) {
         addOrderBy(manager, wfQuery, PagesUtil.LASTMODIFIER_FIELD, aord);
      }
      else if (orderby.equals(PagesUtil.LASTMODIFIEDDATE_FIELD)) {
         addOrderBy(manager, wfQuery, PagesUtil.LASTMODIFIEDDATE_FIELD, aord);
      }
      else if (orderby.equals("number")) {
         addOrderBy(manager, wfQuery, "number", aord);
      }
      else if (orderby.equals(WorkflowManager.REMARK_FIELD)) {
         addOrderBy(WorkflowManager.getManager(cloud), wfQuery, WorkflowManager.REMARK_FIELD, aord);
      }
      else {
         addOrderBy(manager, wfQuery, WorkflowManager.LASTMODIFIEDDATE_FIELD, aord);
      }
      return wfQuery;
   }

   @Override
   protected void addAllcontentListToRequest(HttpServletRequest request, Cloud cloud,String orderby, String status, String laststatus) {
   }
}
