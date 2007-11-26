/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.workflow;

import java.util.List;

import net.sf.mmapps.commons.bridge.RelationUtil;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.security.*;
import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.services.workflow.WorkflowException;

public class ContentWorkflow extends RepositoryWorkflow {

   /** MMbase logging system */
   private static Logger log = Logging.getLoggerInstance(ContentWorkflow.class.getName());

   public static final String TYPE_CONTENT = "content";


   public ContentWorkflow(Cloud cloud) {
      super(cloud);
   }


   public Node createFor(Node content, String remark) {
      synchronized (content) {
         if (hasWorkflow(content)) {
            return (Node) getWorkflows(content).get(0);
         }
         else {
            Node wfItem = createFor(TYPE_CONTENT, remark);
            RelationUtil.createRelation(wfItem, content, WORKFLOWREL);
            log.debug("Workflow " + wfItem.getNumber() + " created for content " + content.getNumber());
            return wfItem;
         }
      }
   }


   public void finishWriting(Node node, String remark) {
      Node wfItem;
      Node content;
      if (ContentElementUtil.isContentElement(node)) {
         wfItem = getWorkflowNode(node, TYPE_CONTENT);
         content = node;
      }
      else {
         wfItem = node;
         content = getContentNode(node);
      }
      Node channel = RepositoryUtil.getCreationChannel(content);
      super.finishWriting(wfItem, channel, remark);
   }


   /**
    * Status change to 'APPROVED'. The workflow appears on all chiefeditor
    * workflow screens
    */
   public void accept(Node node, String remark) {
      Node wfItem;
      Node content;
      if (ContentElementUtil.isContentElement(node)) {
         wfItem = getWorkflowNode(node, TYPE_CONTENT);
         content = node;
      }
      else {
         wfItem = node;
         content = getContentNode(node);
      }
      Node channel = RepositoryUtil.getCreationChannel(content);
      super.accept(wfItem, channel, remark);
   }


   /**
    * Status change to 'DRAFT'. The workflow appears on the writer workflow
    * screens
    */
   public void reject(Node node, String remark) {
      if (ContentElementUtil.isContentElement(node)) {
         if (hasWorkflow(node, TYPE_CONTENT)) {
            Node wfItem = getWorkflowNode(node, TYPE_CONTENT);
            if (isStatusPublished(wfItem)) {
               Publish.remove(node);
            }

            rejectWorkflow(wfItem, remark);
         }
      }
      else {
         if (isStatusPublished(node)) {
            Node content = getContentNode(node);
            Publish.remove(content);
         }
         rejectWorkflow(node, remark);
      }
   }


   /**
    * Put content elements in publishqueue
    * 
    * @param content
    */
   public void publish(Node node) throws WorkflowException {
      publish(node, null);
   }


   public void publish(Node node, List<Integer> publishNumbers) throws WorkflowException {
      Node content;
      if (ContentElementUtil.isContentElement(node)) {
         content = node;
      }
      else {
         content = getContentNode(node);
      }
      publish(content, TYPE_CONTENT, publishNumbers);
   }


   public void complete(Node contentNode) {
      complete(contentNode, TYPE_CONTENT);
   }


   public boolean isWorkflowType(String type) {
      return ContentElementUtil.isContentType(cloud.getNodeManager(type));
   }


   public boolean isWorkflowElement(Node node, boolean isWorkflowItem) {
      if (isWorkflowItem) {
         return TYPE_CONTENT.equals(node.getStringValue(TYPE_FIELD));
      }

      return (ContentElementUtil.isContentElement(node) && RepositoryUtil.hasContentChannel(node));
   }


   public boolean hasWorkflow(Node node) {
      return hasWorkflow(node, TYPE_CONTENT);
   }


   @Override
   protected Node getWorkflowNode(Node node) {
      return getWorkflowNode(node, TYPE_CONTENT);
   }


   @Override
   public UserRole getUserRole(Node node) {
      Node content;
      Node creationNode;
      if (RepositoryUtil.isContentChannel(node) || RepositoryUtil.isCollectionChannel(node)) {
         creationNode = node;
      }
      else {
         if (ContentElementUtil.isContentElement(node)) {
            content = node;
         }
         else {
            content = getContentNode(node);
         }
         creationNode = RepositoryUtil.getCreationChannel(content);
      }
      return RepositoryUtil.getRole(node.getCloud(), creationNode, false);
   }

}
