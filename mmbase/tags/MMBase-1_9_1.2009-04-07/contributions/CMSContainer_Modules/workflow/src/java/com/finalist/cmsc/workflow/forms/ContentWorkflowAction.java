/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.workflow.forms;


import javax.servlet.http.HttpServletRequest;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;

import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.workflow.ContentWorkflow;
import com.finalist.cmsc.workflow.WorkflowManager;

public class ContentWorkflowAction extends WorkflowAction {

   @Override
   protected String getWorkflowType() {
      return ContentWorkflow.TYPE_CONTENT;
   }


   @Override
   protected NodeQuery createDetailQuery(Cloud cloud, String orderby, boolean aord) {
      NodeManager manager = cloud.getNodeManager(ContentElementUtil.CONTENTELEMENT);
      NodeQuery wfQuery = WorkflowManager.createDetailQuery(cloud, manager);

      NodeManager channelManager = cloud.getNodeManager(RepositoryUtil.CONTENTCHANNEL);
      wfQuery.addRelationStep(channelManager, RepositoryUtil.CREATIONREL, null);

      wfQuery.addField(ContentElementUtil.CONTENTELEMENT + ".number");
      wfQuery.addField(ContentElementUtil.CONTENTELEMENT + "." + ContentElementUtil.TITLE_FIELD);
      wfQuery.addField(ContentElementUtil.CONTENTELEMENT + "." + ContentElementUtil.LASTMODIFIEDDATE_FIELD);
      wfQuery.addField(ContentElementUtil.CONTENTELEMENT + "." + ContentElementUtil.LASTMODIFIER_FIELD);
      wfQuery.addField(RepositoryUtil.CONTENTCHANNEL + "." + RepositoryUtil.NAME_FIELD);
      if (orderby.equals(ContentElementUtil.TITLE_FIELD)) {
         addOrderBy(manager, wfQuery, ContentElementUtil.TITLE_FIELD, aord);
      }
      else if (orderby.equals(ContentElementUtil.LASTMODIFIER_FIELD)) {
         addOrderBy(manager, wfQuery, ContentElementUtil.LASTMODIFIER_FIELD, aord);
      }
      else if (orderby.equals(ContentElementUtil.LASTMODIFIEDDATE_FIELD)) {
         addOrderBy(manager, wfQuery, ContentElementUtil.LASTMODIFIEDDATE_FIELD, aord);
      }
      else if (orderby.equals(RepositoryUtil.CONTENTCHANNEL)) {
         addOrderBy(channelManager, wfQuery, RepositoryUtil.NAME_FIELD, aord);
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
