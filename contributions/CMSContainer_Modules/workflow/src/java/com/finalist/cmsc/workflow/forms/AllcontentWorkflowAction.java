/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.workflow.forms;


import javax.servlet.http.HttpServletRequest;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.CompositeConstraint;

import com.finalist.cmsc.repository.AssetElementUtil;
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.services.workflow.Workflow;
import com.finalist.cmsc.workflow.RepositoryWorkflow;
import com.finalist.cmsc.workflow.WorkflowManager;

public class AllcontentWorkflowAction extends WorkflowAction {

   @Override
   protected String getWorkflowType() {
      return RepositoryWorkflow.TYPE_ALLCONTENT;
   }


   @Override
   protected NodeQuery createDetailQuery(Cloud cloud, String orderby, boolean aord) {
      return null;
   }
   
   
   protected NodeQuery createContentDetailQuery(Cloud cloud, String orderby, boolean aord) {
      NodeManager manager = cloud.getNodeManager(ContentElementUtil.CONTENTELEMENT);
      NodeQuery wfQuery = WorkflowManager.createDetailQuery(cloud, manager);

      NodeManager channelManager = cloud.getNodeManager(RepositoryUtil.CONTENTCHANNEL);
      wfQuery.addRelationStep(channelManager, RepositoryUtil.CREATIONREL, null);

      wfQuery.addField(ContentElementUtil.CONTENTELEMENT + ".number");
      wfQuery.addField(ContentElementUtil.CONTENTELEMENT + "." + ContentElementUtil.TITLE_FIELD);
      wfQuery.addField(ContentElementUtil.CONTENTELEMENT + "." + ContentElementUtil.LASTMODIFIEDDATE_FIELD);
      wfQuery.addField(ContentElementUtil.CONTENTELEMENT + "." + ContentElementUtil.LASTMODIFIER_FIELD);
      wfQuery.addField(RepositoryUtil.CONTENTCHANNEL + "." + RepositoryUtil.NAME_FIELD);
      return wfQuery;
   }
   
   protected NodeQuery createAssetDetailQuery(Cloud cloud, String orderby, boolean aord) {
      NodeManager manager = cloud.getNodeManager(AssetElementUtil.ASSETELEMENT);
      NodeQuery wfQuery = WorkflowManager.createDetailQuery(cloud, manager);

      NodeManager channelManager = cloud.getNodeManager(RepositoryUtil.CONTENTCHANNEL);
      wfQuery.addRelationStep(channelManager, RepositoryUtil.CREATIONREL, null);

      wfQuery.addField(AssetElementUtil.ASSETELEMENT + ".number");
      wfQuery.addField(AssetElementUtil.ASSETELEMENT + "." + AssetElementUtil.TITLE_FIELD);
      wfQuery.addField(AssetElementUtil.ASSETELEMENT + "." + AssetElementUtil.LASTMODIFIEDDATE_FIELD);
      wfQuery.addField(AssetElementUtil.ASSETELEMENT + "." + AssetElementUtil.LASTMODIFIER_FIELD);
      wfQuery.addField(RepositoryUtil.CONTENTCHANNEL + "." + RepositoryUtil.NAME_FIELD);
      return wfQuery;
   }

   private NodeList getWorkflowList(Cloud cloud, String type, String status, String laststatus) {
      NodeQuery listQuery = WorkflowManager.createListQuery(cloud);
      Queries.addConstraint(listQuery, WorkflowManager.getStatusConstraint(listQuery, status));
      if (!Workflow.isAcceptedStepEnabled() && Workflow.STATUS_FINISHED.equals(status)) {
         SearchUtil.addConstraint(listQuery, WorkflowManager.getStatusConstraint(listQuery, Workflow.STATUS_APPROVED),
               CompositeConstraint.LOGICAL_OR);
      }
      Queries.addConstraint(listQuery, WorkflowManager.getTypeConstraint(listQuery, type));
      NodeQuery wfQuery;
      if("content".equals(type)){
      wfQuery = createContentDetailQuery(cloud, null, (laststatus == null) ? false : (laststatus.equals("true")));
      } else {
         wfQuery = createAssetDetailQuery(cloud, null, (laststatus == null) ? false : (laststatus.equals("true")));
      }
      NodeList ceWorkflowNumbers = cloud.getList(listQuery);
      if (!ceWorkflowNumbers.isEmpty()) {
         NodeQuery numbersQuery = createDetailsWithNumbersQuery(wfQuery, ceWorkflowNumbers);
         NodeList dataList = cloud.getList(numbersQuery);
         return dataList;
      }
      return null;
   }


   @Override
   protected void addAllcontentListToRequest(HttpServletRequest request, Cloud cloud, String status, String laststatus) {
      NodeList dataList = cloud.createNodeList();
      NodeList contentList = getWorkflowList(cloud, "content", status, laststatus);
      NodeList assetList = getWorkflowList(cloud, "asset", status, laststatus);
      if (contentList!=null&&!contentList.isEmpty()) {
         dataList.addAll(contentList);
      }
      if (assetList!=null&&!assetList.isEmpty()) {
         dataList.addAll(assetList);
      }
      request.setAttribute("results", dataList);
   }

}
