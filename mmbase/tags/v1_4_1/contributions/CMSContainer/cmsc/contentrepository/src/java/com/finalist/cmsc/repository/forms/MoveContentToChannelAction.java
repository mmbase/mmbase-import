/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.repository.forms;

import java.net.URLEncoder;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.commons.bridge.RelationUtil;

import org.apache.struts.action.*;
import org.apache.struts.util.MessageResources;
import org.mmbase.bridge.*;

import com.finalist.cmsc.struts.MMBaseAction;
import com.finalist.cmsc.services.workflow.Workflow;

public class MoveContentToChannelAction extends MMBaseAction {

   private static final String PARAMETER_CHANNEL = "parentchannel";
   private static final String PARAMETER_NEW_CHANNEL = "newparentchannel";
   private static final String PARAMETER_NUMBER = "objectnumber";


   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      int number = Integer.parseInt(request.getParameter(PARAMETER_NUMBER));
      int channel = Integer.parseInt(request.getParameter(PARAMETER_CHANNEL));
      int newChannel = Integer.parseInt(request.getParameter(PARAMETER_NEW_CHANNEL));

      Locale locale = request.getLocale();
      MessageResources resources = getResources(request, "REPOSITORY");

      Node elementNode = cloud.getNode(number);
      Node channelNode = cloud.getNode(channel);
      Node newChannelNode = cloud.getNode(newChannel);

      RelationManager contentRelationManager = cloud.getRelationManager("contentrel");
      NodeList newContentList = contentRelationManager.getList("(snumber = " + newChannel + " and dnumber = " + number
            + ") or (snumber = " + number + " and dnumber = " + newChannel + ")", null, null);

      String message = null;
      // only if we are not already in the other channel
      if (newContentList.size() == 0) {

         RelationUtil.createCountedRelation(elementNode, newChannelNode, "contentrel", "pos");
         RelationUtil.createRelation(channelNode, elementNode, "deletionrel");
         NodeList contentList = contentRelationManager.getList("(snumber = " + channel + " and dnumber = " + number
               + ") or (snumber = " + number + " and dnumber = " + channel + ")", null, null);
         for (NodeIterator i = contentList.nodeIterator(); i.hasNext();) {
            i.nextNode().delete();
         }

         RelationManager creationRelationManager = cloud.getRelationManager("creationrel");
         NodeList creationList = creationRelationManager.getList("(snumber = " + channel + " and dnumber = " + number
               + ") or (snumber = " + number + " and dnumber = " + channel + ")", null, null);
         for (NodeIterator i = creationList.nodeIterator(); i.hasNext();) {
            i.nextNode().delete();
            RelationUtil.createRelation(newChannelNode, elementNode, "creationrel");
         }

         String remark = resources.getMessage(locale, "content.movetochannel.workflow.message", elementNode
               .getStringValue("title"), channelNode.getStringValue("name"), newChannelNode.getStringValue("name"));
         List<Node> nodes = new ArrayList<Node>();
         nodes.add(elementNode);
         Workflow.create(channelNode, remark, nodes);
         Workflow.create(newChannelNode, remark, nodes);

         message = resources.getMessage(locale, "content.movetochannel.success", newChannelNode.getStringValue("name"));
      }
      else {
         message = resources.getMessage(locale, "content.movetochannel.failed", newChannelNode.getStringValue("name"));
      }
      String path = mapping.findForward(SUCCESS).getPath() + "?" + PARAMETER_CHANNEL + "=" + channel
            + "&direction=down&message=" + URLEncoder.encode(message, "UTF-8");
      return new ActionForward(path);
   }

}
