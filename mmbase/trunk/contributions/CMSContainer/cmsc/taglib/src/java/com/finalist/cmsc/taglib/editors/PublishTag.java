package com.finalist.cmsc.taglib.editors;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.jsp.taglib.NodeReferrerTag;

import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.services.workflow.Workflow;
import com.finalist.cmsc.services.workflow.WorkflowException;

/**
 * Publish a nodes
 * 
 * @author Freek Punt
 * @author Nico Klasens
 */
@SuppressWarnings("serial")
public class PublishTag extends NodeReferrerTag {
   static Log log = LogFactory.getLog(PublishTag.class);

   /**
    * Node number
    */
   private int number;
   private boolean children;
   private String var;
   private boolean execute = true;


   @Override
   public int doStartTag() throws JspException {

      List<Node> toPublishNodes = new ArrayList<Node>();

      Node node = null;
      int nr = number;
      if (number <= 0) {
         node = getNode();
      }
      else {
         node = getCloudVar().getNode(nr);
      }

      Map<Node, List<Node>> errors = new HashMap<Node, List<Node>>();
      if (children) {
         findNodes(node, toPublishNodes);

         List<Integer> publishNumbers = new ArrayList<Integer>();
         for (Node toPublishNode : toPublishNodes) {
            publishNumbers.add(toPublishNode.getNumber());
         }

         for (Node toPublishNode : toPublishNodes) {
         	if (Workflow.isWorkflowType(toPublishNode.getNodeManager().getName())) { 
 	           List<Node> nodeErrors = Workflow.isReadyToPublish(toPublishNode, publishNumbers);
    	        if (!nodeErrors.isEmpty()) {
        	       errors.put(toPublishNode, nodeErrors);
            	}
         	}
         }
      }
      else {
         toPublishNodes.add(node);
         List<Integer> publishNumbers = new ArrayList<Integer>();

         publishNumbers.add(node.getNumber());

         if (RepositoryUtil.isContentChannel(node)) {
            NodeList content = RepositoryUtil.getLinkedElements(node);
            for (Iterator<Node> iter = content.iterator(); iter.hasNext();) {
               Node child = iter.next();
               publishNumbers.add(child.getNumber());
               toPublishNodes.add(child);
            }
            NodeList asset = RepositoryUtil.getCreatedAssetElements(node);
            for (Iterator<Node> iter = asset.iterator(); iter.hasNext();) {
               Node child = iter.next();
               publishNumbers.add(child.getNumber());
               toPublishNodes.add(child);
            }

            NodeList channels = RepositoryUtil.getChildren(node);
            for (Iterator<Node> iter = channels.iterator(); iter.hasNext();) {
               Node child = iter.next();
               publishNumbers.add(child.getNumber());
               toPublishNodes.add(child);
            }
         }

         if (Workflow.isWorkflowElement(node)) {
            List<Node> nodeErrors = Workflow.isReadyToPublish(node, publishNumbers);
            if (!nodeErrors.isEmpty()) {
               errors.put(node, nodeErrors);
            }
         }
      }

      if (var != null) {
         HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
         request.setAttribute(var, toPublishNodes.size());
      }

      if (execute) {
         if (errors.isEmpty()) {
            Thread runner = new MassPublishThread(toPublishNodes);
            runner.start();
         }
         else {
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            request.setAttribute("errors", errors);
         }
      }
      return EVAL_BODY_BUFFERED;
   }


   private void findNodes(Node node, List<Node> toPublishNodes) {
      toPublishNodes.add(node);

      if (PagesUtil.isPageType(node)) {
         NodeList children = NavigationUtil.getChildren(node);
         for (Iterator<Node> iter = children.iterator(); iter.hasNext();) {
            Node child = iter.next();
            findNodes(child, toPublishNodes);
         }
      }
      else if (RepositoryUtil.isContentChannel(node)) {
         NodeList content = RepositoryUtil.getLinkedElements(node);
         for (Iterator<Node> iter = content.iterator(); iter.hasNext();) {
            Node child = iter.next();
            toPublishNodes.add(child);
         }
         NodeList asset = RepositoryUtil.getCreatedAssetElements(node);
         for (Iterator<Node> iter = asset.iterator(); iter.hasNext();) {
            Node child = iter.next();
            toPublishNodes.add(child);
         }

         NodeList channels = node
               .getRelatedNodes(RepositoryUtil.CONTENTCHANNEL, RepositoryUtil.CHILDREL, "DESTINATION");
         for (Iterator<Node> iter = channels.iterator(); iter.hasNext();) {
            Node child = iter.next();
            findNodes(child, toPublishNodes);
         }

         NodeList collectionChannels = RepositoryUtil.getCollectionChannels(node);
         for (Iterator<Node> iter = collectionChannels.iterator(); iter.hasNext();) {
            Node child = iter.next();
            findNodes(child, toPublishNodes);
         }
      }
   }


   public void setNumber(int number) {
      this.number = number;
   }


   public void setChildren(boolean children) {
      this.children = children;
   }

   static class MassPublishThread extends Thread {

      private List<Node> toPublishNodes = new ArrayList<Node>();


      public MassPublishThread(List<Node> toPublishNodes) {
         super("Mass publish");
         setDaemon(true);
         this.toPublishNodes = toPublishNodes;
      }


      @Override
      public void run() {
         List<Integer> publishNumbers = new ArrayList<Integer>();
         for (Node toPublishNode : toPublishNodes) {
            publishNumbers.add(toPublishNode.getNumber());
         }

         for (Node toPublishNode : toPublishNodes) {
            publishNode(toPublishNode, publishNumbers);
         }
      }


      private void publishNode(Node node, List<Integer> publishNumbers) {
         try {
            log.debug("Publising node using taglib: " + node.getNumber());

            if (!Workflow.isWorkflowType(node.getNodeManager().getName())) {
               Publish.publish(node);
            }
            else {
               if (!Workflow.hasWorkflow(node)) {
                  Workflow.create(node, "");
               }
               if (!Workflow.getStatus(node).equals(Workflow.STATUS_PUBLISHED)) {
                  Workflow.publish(node, publishNumbers);
               }
            }
            log.debug("- published node using taglib: " + node.getNumber());
         }
         catch (WorkflowException wfe) {
            log.error("unable to publish node: " + node.getNumber(), wfe);
         }
      }
   }


   public void setExecute(boolean execute) {
      this.execute = execute;
   }


   public void setVar(String var) {
      this.var = var;
   }

}
