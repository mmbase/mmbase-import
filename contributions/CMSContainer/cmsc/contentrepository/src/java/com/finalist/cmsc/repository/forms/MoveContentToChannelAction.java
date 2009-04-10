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

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.*;
import org.apache.struts.util.MessageResources;
import org.mmbase.bridge.*;

import com.finalist.cmsc.mmbase.RelationUtil;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.services.workflow.Workflow;
import com.finalist.cmsc.struts.MMBaseAction;

public class MoveContentToChannelAction extends MMBaseAction {

   private static final String PARAMETER_CHANNEL = "parentchannel";
   private static final String PARAMETER_NEW_CHANNEL = "newparentchannel";
   private static final String PARAMETER_NUMBER = "objectnumber";
   private static final String PARAMETER_PAGING_ODERBY = "orderby";
   private static final String PARAMETER_PAGING_OFFSET = "offset";
   private static final String PARAMETER_PAGING_DIRECTION = "direction";


   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {


      String objectNumber =  request.getParameter(PARAMETER_NUMBER);
      String orderBy =  request.getParameter(PARAMETER_PAGING_ODERBY);
      String offset =  request.getParameter(PARAMETER_PAGING_OFFSET);
      String direction =  request.getParameter(PARAMETER_PAGING_DIRECTION);
      int channel = Integer.parseInt(request.getParameter(PARAMETER_CHANNEL));
      int newChannel = Integer.parseInt(request.getParameter(PARAMETER_NEW_CHANNEL));

      String message = "";
      String[] numbers = objectNumber.split(",");
      Node userNode = SecurityUtil.getUserNode(cloud);
      String language = userNode.getStringValue("language");
      Locale locale;
      if(StringUtils.isEmpty(language)){
         locale = request.getLocale();
      }else{
         locale = new Locale(language);
      }
      MessageResources resources = getResources(request, "REPOSITORY");
      int successCount=0;
      int failureCount=0;

      Node channelNode = cloud.getNode(channel);
      Node newChannelNode = cloud.getNode(newChannel);

      for (String number2 : numbers) {
         int number = Integer.parseInt(number2);
         Node elementNode = cloud.getNode(number);

         RelationManager contentRelationManager = cloud.getRelationManager("contentrel");
         NodeList newContentList = contentRelationManager.getList("(snumber = " + newChannel + " and dnumber = " + number
               + ") or (snumber = " + number + " and dnumber = " + newChannel + ")", null, null);


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
            successCount++;
         }
         else {
            failureCount++;
         }
      }
      if(successCount>0) {
         if(successCount==1){
            message += resources.getMessage(locale, "content.movetochannel.success", newChannelNode.getStringValue("name"));
         }else{
            message += resources.getMessage(locale, "content.massmovetochannel.success", successCount, newChannelNode.getStringValue("name"));
         }
      }
      if(failureCount>0) {
         if(successCount>0){
            message += "\\n";
         }
         if(failureCount==1){
            message += resources.getMessage(locale, "content.movetochannel.failed", newChannelNode.getStringValue("name"));
         }else{
            message += resources.getMessage(locale, "content.massmovetochannel.failed", failureCount, newChannelNode.getStringValue("name"));
         }
      }
      String path = mapping.findForward(SUCCESS).getPath() + "?" + PARAMETER_CHANNEL + "=" + channel;

      if(StringUtils.isNotEmpty(offset)) {
         path += "&offset="+offset;
      }
      if(StringUtils.isNotEmpty(orderBy)) {
         path += "&orderby="+orderBy;
      }
      if(StringUtils.isNotEmpty(direction)) {
         path += "&direction="+direction;
      }
      path += "&message=" + URLEncoder.encode(message, "UTF-8");
      ActionForward actionForward = new ActionForward(path);
      actionForward.setRedirect(true);
      return actionForward;
   }

}
