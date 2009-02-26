/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.workflow;

import java.util.List;

import org.mmbase.bridge.*;

import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.security.Role;

public abstract class RepositoryWorkflow extends WorkflowManager {
   
   public static final String TYPE_ALLCONTENT = "allcontent";

   public RepositoryWorkflow(Cloud cloud) {
      super(cloud);
   }


   protected Node getContentNode(Node wfItem) {
      NodeList list = wfItem.getRelatedNodes(RepositoryUtil.CONTENTELEMENT, WORKFLOWREL, DESTINATION);
      if (!list.isEmpty()) {
         return list.getNode(0);
      }
      return null;
   }


   protected Node getAssetNode(Node wfItem) {
      NodeList list = wfItem.getRelatedNodes(RepositoryUtil.ASSETELEMENT, WORKFLOWREL, DESTINATION);
      if (!list.isEmpty()) {
         return list.getNode(0);
      }
      return null;
   }


   protected Node getLinkChannel(Node wfItem) {
      NodeList channels = wfItem.getRelatedNodes(RepositoryUtil.CONTENTCHANNEL, WORKFLOWREL, DESTINATION);
      if (!channels.isEmpty()) {
         return channels.getNode(0);
      }
      return null;
   }


   public List<Node> getUsersWithRights(Node channel, Role role) {
      return RepositoryUtil.getUsersWithRights(channel, role);
   }

}
