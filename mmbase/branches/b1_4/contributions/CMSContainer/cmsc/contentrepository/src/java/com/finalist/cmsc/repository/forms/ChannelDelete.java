/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.repository.forms;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.RelationManager;

import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.services.workflow.Workflow;
import com.finalist.cmsc.struts.MMBaseFormlessAction;


public class ChannelDelete extends MMBaseFormlessAction {

    @Override
    public ActionForward execute(ActionMapping mapping,
            HttpServletRequest request, Cloud cloud) throws Exception {
        
      String objectnumber = getParameter(request, "number", true);
      String action = getParameter(request, "remove");
      Node channelNode = cloud.getNode(objectnumber);

      if (StringUtils.isBlank(action)) {
         if (RepositoryUtil.hasCreatedContent(channelNode)) {
            return mapping.findForward("channeldeletewarning");
         }
         else {
            return mapping.findForward("channeldelete");
         }
      }
      else {
         if ("cancel".equals(action)) {
            return mapping.findForward(SUCCESS);
         }
         if ("delete".equals(action)) {
            deleteAction(cloud, channelNode);
         }
         if ("move".equals(action)) {
            moveAction(cloud, channelNode);
         }
         return mapping.findForward(SUCCESS);
      }
   }

   private void moveAction(Cloud cloud, Node channelNode) {
      NodeList childChannels = RepositoryUtil.getChildren(channelNode);
      for (Iterator<Node> childIter = childChannels.iterator(); childIter.hasNext();) {
         Node childChannel = childIter.next();
         moveAction(cloud, childChannel);
      }
      
      // get relations of content elements to channels other then the creationchannel
      NodeList createdElements = RepositoryUtil.getCreatedElements(channelNode);

      for (Iterator<Node> iter = createdElements.iterator(); iter.hasNext();) {
         Node elementNode = iter.next();
         // get relations
         RelationManager contentRelationManager = cloud.getRelationManager("contentrel");
         NodeList relatedChannelsList = contentRelationManager.getList("(snumber != "
               + channelNode.getNumber() + " and dnumber = " + elementNode.getNumber()
               + ")", "number", "UP");

         // loop through channel relations
         Iterator<Node> iter2 = relatedChannelsList.iterator();

         if (iter2.hasNext()) {
            Node relationNode = iter2.next();
            Node newChannelNode = cloud.getNode(relationNode.getStringValue("snumber"));

            if (RepositoryUtil.isParent(channelNode, newChannelNode)) {
               moveElementToTrash(cloud, channelNode, elementNode);
            }
            else {
               moveElementToAnotherChannel(channelNode, elementNode, newChannelNode);
            }
         }
         else {
            moveElementToTrash(cloud, channelNode, elementNode);
         }
      }
      deleteChannel(channelNode);
   }

   private void deleteAction(Cloud cloud, Node channelNode) {
      NodeList childChannels = RepositoryUtil.getChildren(channelNode);
      for (Iterator<Node> childIter = childChannels.iterator(); childIter.hasNext();) {
         Node childChannel = childIter.next();
         deleteAction(cloud, childChannel);
      }
      
      NodeList createdElements = RepositoryUtil.getCreatedElements(channelNode);
      for (Iterator<Node> iter = createdElements.iterator(); iter.hasNext();) {
         Node objectNode = iter.next();
         moveElementToTrash(cloud, channelNode, objectNode);
      }
      deleteChannel(channelNode);
   }

   private void deleteChannel(Node channelNode) {
      Publish.remove(channelNode);
      Workflow.remove(channelNode);
      Publish.unpublish(channelNode);
      
      channelNode.delete(true);
   }

   private void moveElementToAnotherChannel(Node channelNode, Node elementNode, Node newChannelNode) {
      // move content element to the channel
      RepositoryUtil.removeContentFromChannel(elementNode, channelNode);
      RepositoryUtil.removeCreationRelForContent(elementNode);
      RepositoryUtil.addCreationChannel(elementNode, newChannelNode);

      // unpublish and remove from workflow
      Publish.remove(elementNode);
      Workflow.remove(elementNode);
      Publish.unpublish(elementNode);
   }

   private void moveElementToTrash(Cloud cloud, Node channelNode, Node elementNode) {
      // remove the element
      RepositoryUtil.removeContentFromChannel(elementNode, channelNode);
      RepositoryUtil.removeCreationRelForContent(elementNode);

      RepositoryUtil.removeContentFromAllChannels(elementNode);
      RepositoryUtil.addContentToChannel(elementNode, RepositoryUtil.getTrashNode(cloud));

      // unpublish and remove from workflow
      Publish.remove(elementNode);
      Workflow.remove(elementNode);
      Publish.unpublish(elementNode);
   }
    
}
