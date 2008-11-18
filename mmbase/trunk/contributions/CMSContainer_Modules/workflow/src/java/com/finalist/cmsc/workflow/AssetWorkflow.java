/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.workflow;

import java.util.List;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.mmbase.RelationUtil;
import com.finalist.cmsc.repository.AssetElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.services.workflow.WorkflowException;

public class AssetWorkflow extends RepositoryWorkflow {

   /** MMbase logging system */
   private static final Logger log = Logging.getLoggerInstance(AssetWorkflow.class.getName());

   public static final String TYPE_ASSET = "asset";
   public static final String NODETYPE_IMAGES = "images";
   public static final String NODETYPE_ATTACHMENTS = "attachments";
   public static final String NODETYPE_URLS = "urls";


   public AssetWorkflow(Cloud cloud) {
      super(cloud);
   }


   @Override
   public Node createFor(Node asset, String remark) {
      synchronized (asset) {
         if (hasWorkflow(asset)) {
            return (Node) getWorkflows(asset).get(0);
         }
         else {
            Node wfItem = createFor(TYPE_ASSET, remark, asset.getNodeManager().getName());
            RelationUtil.createRelation(wfItem, asset, WORKFLOWREL);
            log.debug("Workflow " + wfItem.getNumber() + " created for asset " + asset.getNumber());
            return wfItem;
         }
      }
   }


   @Override
   public void finishWriting(Node node, String remark) {
      Node wfItem;
      Node asset;
      if (AssetElementUtil.isAssetElement(node)) {
         wfItem = getWorkflowNode(node, TYPE_ASSET);
         asset = node;
      }
      else {
         wfItem = node;
         asset = getAssetNode(node);
      }
      Node channel = RepositoryUtil.getCreationChannel(asset);
      super.finishWriting(wfItem, channel, remark);
   }


   /**
    * Status change to 'APPROVED'. The workflow appears on all chiefeditor
    * workflow screens
    */
   @Override
   public void accept(Node node, String remark) {
      Node wfItem;
      Node asset;
      if (AssetElementUtil.isAssetElement(node)) {
         wfItem = getWorkflowNode(node, TYPE_ASSET);
         asset = node;
      }
      else {
         wfItem = node;
         asset = getAssetNode(node);
      }
      Node channel = RepositoryUtil.getCreationChannel(asset);
      super.accept(wfItem, channel, remark);
   }


   /**
    * Status change to 'DRAFT'. The workflow appears on the writer workflow
    * screens
    */
   @Override
   public void reject(Node node, String remark) {
      if (AssetElementUtil.isAssetElement(node)) {
         if (hasWorkflow(node, TYPE_ASSET)) {
            Node wfItem = getWorkflowNode(node, TYPE_ASSET);
            if (isStatusPublished(wfItem)) {
               Publish.remove(node);
            }

            rejectWorkflow(wfItem, remark);
         }
      }
      else {
         if (isStatusPublished(node)) {
            Node asset = getAssetNode(node);
            Publish.remove(asset);
         }
         rejectWorkflow(node, remark);
      }
   }


   /**
    * Put content elements in publishqueue
    *
    * @param content
    */
   @Override
   public void publish(Node node) throws WorkflowException {
      publish(node, null);
   }


   @Override
   public void publish(Node node, List<Integer> publishNumbers) throws WorkflowException {
      Node asset;
      if (AssetElementUtil.isAssetElement(node)) {
         asset = node;
      }
      else {
         asset = getAssetNode(node);
      }
      publish(asset, TYPE_ASSET, publishNumbers);
   }


   @Override
   public void complete(Node assetNode) {
      complete(assetNode, TYPE_ASSET);
   }


   public boolean isWorkflowType(String type) {
      return AssetElementUtil.isAssetType(cloud.getNodeManager(type));
   }


   @Override
   public boolean isWorkflowElement(Node node, boolean isWorkflowItem) {
      if (isWorkflowItem) {
         return TYPE_ASSET.equals(node.getStringValue(TYPE_FIELD));
      }

      return (AssetElementUtil.isAssetElement(node) && RepositoryUtil.hasCreationChannel(node));
   }


   public boolean hasWorkflow(Node node) {
      return hasWorkflow(node, TYPE_ASSET);
   }


   @Override
   protected Node getWorkflowNode(Node node) {
      return getWorkflowNode(node, TYPE_ASSET);
   }


   @Override
   public UserRole getUserRole(Node node) {
      Node asset;
      Node creationNode;
      if (RepositoryUtil.isContentChannel(node) || RepositoryUtil.isCollectionChannel(node)) {
         creationNode = node;
      }
      else {
         if (AssetElementUtil.isAssetElement(node)) {
            asset = node;
         }
         else {
            asset = getAssetNode(node);
         }
         creationNode = RepositoryUtil.getCreationChannel(asset);
      }
      return RepositoryUtil.getRole(node.getCloud(), creationNode, false);
   }

   @Override
   public void addUserToWorkflow(Node node) {
      addUserToWorkflow(node, TYPE_ASSET);
   }

}
