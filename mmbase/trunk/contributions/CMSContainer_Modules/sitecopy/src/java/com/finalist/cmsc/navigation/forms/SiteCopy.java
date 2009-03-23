package com.finalist.cmsc.navigation.forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.mmapps.commons.bridge.CloneUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.FieldIterator;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.navigation.PortletUtil;
import com.finalist.cmsc.navigation.SiteUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.services.workflow.Workflow;
import com.finalist.cmsc.struts.MMBaseFormlessAction;

public class SiteCopy extends MMBaseFormlessAction{

   private static final Logger log = Logging.getLoggerInstance(SiteCopy.class.getName());

   @Override
   public ActionForward execute(ActionMapping mapping,
         HttpServletRequest request, Cloud cloud) throws Exception {
      String sourceNumber = getParameter(request, "parent");
      String destinationNumber = getParameter(request, "destination");
      String content = getParameter(request, "content");
      String targetChannel = getParameter(request, "targetchannel");
      
      Node sourceNode = null;
      Node destinationNode = null;
      if(cloud.hasNode(sourceNumber)) {
         sourceNode = cloud.getNode(sourceNumber);
      }
      if(cloud.hasNode(destinationNumber)) {
         destinationNode = cloud.getNode(destinationNumber);
      }
      List<Integer> pages = new ArrayList<Integer>();
      if(sourceNode != null && destinationNode != null && !sourceNumber.equals(destinationNumber)  && SiteUtil.isSite(sourceNode) && SiteUtil.isSite(destinationNode)) {
         RemoveRelations(destinationNode);
         PagesUtil.copyPageRelations(sourceNode,destinationNode);
         if(sourceNode.getNumber() != destinationNode.getNumber()) {
         NodeList children = NavigationUtil.getOrderedChildren(sourceNode);
            for (Iterator<Node> iter = children.iterator(); iter.hasNext();) {

               Node childPage = iter.next();
               iteratePages(childPage,pages);
               Node newPage = NavigationUtil.copyPage(childPage, destinationNode);
               addWorkflow(newPage);
            }
         }
         Map<Integer, Integer> copiedNodes = new HashMap<Integer, Integer>();
         StringBuilder output = new StringBuilder().append("Start");
         //copy content elements
         if (content != null && StringUtils.isNotBlank(targetChannel)) {
            NodeList newPages = NavigationUtil.getOrderedChildren(destinationNode);
            for (Iterator<Node> iter = newPages.iterator(); iter.hasNext();) {
               Node newPage = iter.next();
               //Node newPage = NavigationUtil.copyPage(childPage, destinationNode);
               copyContentElement(newPage,targetChannel, copiedNodes,output);
               //addWorkflow(newPage);
               output.append("<br/><br/>copiedNodes has #" + copiedNodes.size() + ":<br/>" + copiedNodes.toString());
              
               log.debug("##########   "+output.toString());
            }
         }
         
      }
      request.setAttribute("pages",pages.size());
      return mapping.findForward("success");
   }

   private void RemoveRelations(Node site) {
      site.deleteRelations();

   }
   private void addWorkflow(Node newPage) {
      addContentsToWorkflow(newPage);
      NodeList children = NavigationUtil.getOrderedChildren(newPage);
      for (Iterator<Node> iter = children.iterator(); iter.hasNext();) {
         Node childPage = iter.next();
         addWorkflow(childPage);
      }
   }
   
   private void addContentsToWorkflow(Node newElement) {
      if (Workflow.isWorkflowElement(newElement) && !Workflow.hasWorkflow(newElement)) {
         Workflow.create(newElement, null);
      } 
   }
   public static void iteratePages(Node page, List<Integer> pages) {
      pages.add(page.getNumber());
      NodeList children = NavigationUtil.getOrderedChildren(page);
      for (Iterator<Node> iter = children.iterator(); iter.hasNext();) {
         Node childPage = iter.next();
         iteratePages(childPage, pages);
      }
   } 
   private Node copyContentElement(Node newPage,String targetChannel,Map<Integer, Integer> copiedNodes,StringBuilder output){

      copyPortlets(newPage,targetChannel,copiedNodes,output);
      NodeList children = NavigationUtil.getOrderedChildren(newPage);
      for (Iterator<Node> iter = children.iterator(); iter.hasNext();) {
          Node childPage = iter.next();
          copyContentElement(childPage,targetChannel,copiedNodes,output);
      }
      return newPage;
   }
   
   public  void copyPortlets(Node newScreen,String targetChannel,Map<Integer, Integer> copiedNodes,StringBuilder output) {
      NodeList portlets = PortletUtil.getPortlets(newScreen);
      if (portlets != null) {
         for (int i = 0; i < portlets.size(); i++) {
            copyPortlet(portlets.getNode(i),targetChannel,copiedNodes,output);
         }
      }
   }
   
   public  void copyPortlet(Node sourcePortlet,String targetChannel,Map<Integer, Integer> copiedNodes,StringBuilder output) { 
      NodeList nodeParameterList = PortletUtil.getNodeParameters(sourcePortlet);
      if (nodeParameterList != null) {
         Node targetChannelNode =  sourcePortlet.getCloud().getNode(targetChannel);
         for (int i = 0; i < nodeParameterList.size(); i++) {
            Node nodeParameter = nodeParameterList.getNode(i);
            if("contentchannel".equals(nodeParameter.getStringValue(PortletUtil.KEY_FIELD))) {
               int number = nodeParameter.getIntValue(PortletUtil.VALUE_FIELD); 

               if (number > 0) {
                  Node channelNode = sourcePortlet.getCloud().getNode(number);
                  Node newChannel = null;
                  if (copiedNodes.get(number) == null) {
                     newChannel = CloneUtil.cloneNode(channelNode);
                     copiedNodes.put(number, newChannel.getNumber());
                     RepositoryUtil.appendChild(targetChannelNode, newChannel);
                     nodeParameter.setIntValue(PortletUtil.VALUE_FIELD, newChannel.getNumber());
                     NodeList l = RepositoryUtil.getLinkedElements(channelNode, null, null, null, false, null, -1, -1, -1, -1, -1);
                     for (int j = 0; j < l.size(); j++) {
//                        Node newContentElement = CloneUtil.cloneNode(l.getNode(j));
                        Node newContentElement = cloneNode(l.getNode(j));
                        RepositoryUtil.addCreationChannel(newContentElement, newChannel);
                        RepositoryUtil.addContentToChannel(newContentElement, newChannel);
                        addContentsToWorkflow(newContentElement);
                     }
                  }
                  else {
                     nodeParameter.setIntValue(PortletUtil.VALUE_FIELD, copiedNodes.get(number));
                  }
                  nodeParameter.commit();
               }
            }   
            else if ("contentelement".equals(nodeParameter.getStringValue(PortletUtil.KEY_FIELD))) {
               
               int number = nodeParameter.getIntValue(PortletUtil.VALUE_FIELD);
               if (copiedNodes.get(number) == null) {
                  Node contentNode = sourcePortlet.getCloud().getNode(number);
//                  Node newContentElement = CloneUtil.cloneNode(contentNode);
                  Node newContentElement = cloneNode(contentNode);
                  RepositoryUtil.addContentToChannel(newContentElement, targetChannelNode);
                  nodeParameter.setIntValue(PortletUtil.VALUE_FIELD, newContentElement.getNumber());
                  addContentsToWorkflow(newContentElement);
               }
               else {
                  nodeParameter.setIntValue(PortletUtil.VALUE_FIELD, copiedNodes.get(number)); 
               }
               nodeParameter.commit();
            }
         }
      }
   }
   public  Node cloneNode(Node localNode) {
      if (isRelation(localNode)) {
         return CloneUtil.cloneRelation(localNode);
      }
      else {
        NodeManager localNodeManager = localNode.getNodeManager();
        NodeManager nodeManager = localNode.getCloud().getNodeManager(localNodeManager.getName());
        Node newNode = nodeManager.createNode();

        FieldIterator fields = localNodeManager.getFields().fieldIterator();
        while (fields.hasNext()) {
           Field field = fields.nextField();
           String fieldName = field.getName();
           
           if (field.getState() == Field.STATE_PERSISTENT) {
               if (!(fieldName.equals("owner") || fieldName.equals("number") ||
                     fieldName.equals("otype") ||
                     (fieldName.indexOf("_") == 0))) {
                  cloneNodeField(localNode, newNode, field);
               }
           }
        }
        newNode.commit();

        return newNode;
      }
   }
   
   /**
    * cloneNodeField copies node fields from one node to an other
    * 
    * @param sourceNode
    *           the source node
    * @param destinationNode
    *           destination node
    * @param field
    *           the field to clone
    */
   public  void cloneNodeField(Node sourceNode, Node destinationNode, Field field) {
      String fieldName = field.getName();

      if (destinationNode.getNodeManager().hasField(fieldName) == true) {
         Field sourceField = sourceNode.getNodeManager().getField(fieldName);
         if (sourceField.getState() != Field.STATE_SYSTEM && !sourceField.isVirtual()) {
          destinationNode.setValueWithoutProcess(fieldName, 
                sourceNode.getValueWithoutProcess(fieldName));
         }
      }
   }
   public  boolean isRelation(Node node) {
      FieldIterator fi = node.getNodeManager().getFields().fieldIterator();
      int count = 0;

      while (fi.hasNext()) {
         String name = fi.nextField().getName();

         if (name.equals("rnumber") || name.equals("snumber") ||
               name.equals("dnumber")) {
            count++;
         }
      }

      if (count == 3) {
         return true;
      }

      return false;
   }
}
