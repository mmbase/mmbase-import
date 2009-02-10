/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.workflow.forms;


import java.util.Comparator;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
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
   protected void addAllcontentListToRequest(HttpServletRequest request, Cloud cloud,String orderby, String status, String laststatus) {
      NodeList dataList = cloud.createNodeList();
      NodeList contentList = getWorkflowList(cloud, "content", status, laststatus);
      NodeList assetList = getWorkflowList(cloud, "asset", status, laststatus);
      if (contentList!=null&&!contentList.isEmpty()) {
         dataList.addAll(contentList);
      }
      if (assetList!=null&&!assetList.isEmpty()) {
         dataList.addAll(assetList);//dataList.sort(comparator)
      }
      if(dataList!=null && !dataList.isEmpty()){
      if ("contenttype".equals(orderby)) {
         orderby = "title";
      }
      dataList.sort(new ContentComparator(orderby,laststatus));
      }

      request.setAttribute("results", dataList);
   }
   class ContentComparator implements Comparator<Node> {
      
      private String field;
      private String destination ;
      public ContentComparator(String field,String destination) {
         this.field = field;
         this.destination = destination;
      }

      public int compare(Node o1, Node o2) {
         Object f1, f2;
         int result = 0;
         String field1_prefix = "",field2_prefix = "";
         if ("type".equals(field) || "remark".equals(field)) {
            field1_prefix = "workflowitem.";
            field2_prefix = "workflowitem.";
         }
         else if ("title".equals(field) || "publishdate".equals(field) || "lastmodifieddate".equals(field) || "lastmodifier".equals(field) || "number".equals(field)) {
            if ("content".equals(o1.getStringValue("workflowitem.type")))
       
               field1_prefix = ContentElementUtil.CONTENTELEMENT+".";
            else {
               field1_prefix = AssetElementUtil.ASSETELEMENT+".";
            }
            if ("content".equals(o2.getStringValue("workflowitem.type")))
               field2_prefix = ContentElementUtil.CONTENTELEMENT+".";
            else {
               field2_prefix = AssetElementUtil.ASSETELEMENT+".";
            }
         }
         else if ("contentchannel".equals(field)) {
            field1_prefix = RepositoryUtil.CONTENTCHANNEL + ".";
            field2_prefix = RepositoryUtil.CONTENTCHANNEL + ".";
            field = "name";
         }

         f1 = o1.getObjectValue(field1_prefix+field);
         f2 = o2.getObjectValue(field2_prefix+field);

         if (f1 == null || f2 == null) {
            return result;
         }

         if (f1 instanceof Comparable) {
            try {
                result=((Comparable)f1).compareTo(f2);
            } catch (ClassCastException e) {

            }
        } 
         else if (!f1.equals(f2)) {
            if (f1 instanceof Boolean) {
                result=((Boolean)f1).booleanValue() ? 1 : -1;
            }
        }
         if (destination != null && "false".equalsIgnoreCase(destination)) {
            result = -result;
         }
        return result; 
      }
      
   }
}
