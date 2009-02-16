/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.workflow;

import java.util.*;

import net.sf.mmapps.commons.bridge.RelationUtil;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.security.*;
import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.services.workflow.WorkflowException;
import com.finalist.cmsc.services.workflow.Workflow;

public class LinkWorkflow extends RepositoryWorkflow {

   /** MMbase logging system */
   private static Logger log = Logging.getLoggerInstance(LinkWorkflow.class.getName());

   public static final String TYPE_LINK = "link";


   public LinkWorkflow(Cloud cloud) {
      super(cloud);
   }


   @Override
   public Node createFor(Node channel, String remark) {
      Node wfItem = getWorkflowNode(channel, TYPE_LINK);
      if (wfItem == null) {
         wfItem = createFor(TYPE_LINK, remark, Workflow.STATUS_FINISHED);
      }

      if (channel != null) {
         RelationUtil.createRelation(wfItem, channel, WORKFLOWREL);
      }

      List<Node> users = getUsersWithRights(channel, Role.EDITOR);
      changeUserRelations(wfItem, users);

      log.debug("Link Workflow " + wfItem.getNumber() + " created for contentchannel "
            + (channel != null ? channel.getNumber() : ""));
      return wfItem;
   }


   @SuppressWarnings("unused")
   public void finishWriting(Node content, String remark) {
      throw new UnsupportedOperationException("Linked workflows are always finished after linking");
   }


   /**
    * Status change to 'APPROVED'. The workflow appears on all chiefeditor
    * workflow screens
    */
   public void accept(Node node, String remark) {
      Node wfItem;
      Node channel;
      if (RepositoryUtil.isContentChannel(node)) {
         wfItem = getWorkflowNode(node, TYPE_LINK);
         channel = node;
      }
      else {
         if (ContentElementUtil.isContentElement(node)) {
            wfItem = getWorkflowNode(node, TYPE_LINK);
            channel = RepositoryUtil.getCreationChannel(node);
         }
         else {
            wfItem = node;
            channel = getLinkChannel(wfItem);
         }
      }

      super.accept(wfItem, channel, remark);
   }


   public void reject(Node node, String remark) {
      Node wfItem;
      if (RepositoryUtil.isContentChannel(node)) {
         wfItem = getWorkflowNode(node, TYPE_LINK);
      }
      else {
         if (ContentElementUtil.isContentElement(node)) {
            wfItem = getWorkflowNode(node, TYPE_LINK);
         }
         else {
            wfItem = node;
         }
      }
      changeWorkflowFailPublished(wfItem, STATUS_FINISHED, remark);
   }


   /**
    * Put content elements in publishqueue
    */
   public void publish(Node node) throws WorkflowException {
      publish(node, null);
   }


   public void publish(Node node, List<Integer> publishNumbers) throws WorkflowException {
      Node channel;
      if (RepositoryUtil.isContentChannel(node)) {
         channel = node;
      }
      else {
         channel = getLinkChannel(node);
      }
      publish(channel, TYPE_LINK, publishNumbers);
   }


   protected void publishInternal(Node wf, Node node) {
      NodeList nodes = getAllWorkflowNodes(wf);
      if (nodes.size() == 1) {
         if (nodes.getNode(0).getNumber() == node.getNumber()) {
            Publish.publish(node);
         }
      }
      else {
         for (Iterator<Node> iterator = nodes.iterator(); iterator.hasNext();) {
            Node nodeItem = iterator.next();
            if (nodeItem.getNumber() == node.getNumber()) {
               iterator.remove();
            }
         }
         Publish.publish(node, nodes);
      }
   }


   public void complete(Node contentNode) {
      complete(contentNode, TYPE_LINK);
   }


   public boolean hasWorkflow(Node node) {
      return hasWorkflow(node, TYPE_LINK);
   }


   @Override
   public boolean isWorkflowElement(Node node, boolean isWorkflowItem) {
      if (isWorkflowItem) {
         return TYPE_LINK.equals(node.getStringValue(TYPE_FIELD));
      }
      return RepositoryUtil.isContentChannel(node) || RepositoryUtil.isCollectionChannel(node);
   }


   @Override
   protected Node getWorkflowNode(Node node) {
      return getWorkflowNode(node, TYPE_LINK);
   }


   @Override
   public UserRole getUserRole(Node node) {
      Node channel;
      if (RepositoryUtil.isContentChannel(node)) {
         channel = node;
      }
      else {
         channel = getLinkChannel(node);
      }
      return RepositoryUtil.getRole(node.getCloud(), channel, false);
   }

}
