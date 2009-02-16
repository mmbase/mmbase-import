/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.workflow.forms;

import org.mmbase.bridge.*;

import com.finalist.cmsc.workflow.LinkWorkflow;
import com.finalist.cmsc.workflow.WorkflowManager;
import com.finalist.cmsc.repository.RepositoryUtil;

public class LinkWorkflowAction extends WorkflowAction {

   @Override
   protected String getWorkflowType() {
      return LinkWorkflow.TYPE_LINK;
   }


   @Override
   protected NodeQuery createDetailQuery(Cloud cloud, String orderby, boolean aord) {
      NodeManager manager = cloud.getNodeManager(RepositoryUtil.CONTENTCHANNEL);
      NodeQuery wfQuery = WorkflowManager.createDetailQuery(cloud, manager);

      wfQuery.addField(RepositoryUtil.CONTENTCHANNEL + ".number");
      wfQuery.addField(RepositoryUtil.CONTENTCHANNEL + "." + RepositoryUtil.TITLE_FIELD);

      if (orderby.equals(RepositoryUtil.TITLE_FIELD)) {
         addOrderBy(manager, wfQuery, RepositoryUtil.TITLE_FIELD, aord);
      }
      else if (orderby.equals("number")) {
         addOrderBy(manager, wfQuery, "number", aord);
      }
      else if (orderby.equals(WorkflowManager.REMARK_FIELD)) {
         addOrderBy(WorkflowManager.getManager(cloud), wfQuery, WorkflowManager.REMARK_FIELD, aord);
      }
      else {
         addOrderBy(WorkflowManager.getManager(cloud), wfQuery, WorkflowManager.LASTMODIFIEDDATE_FIELD, aord);
      }
      return wfQuery;
   }

}
