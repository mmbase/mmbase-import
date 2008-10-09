package com.finalist.cmsc.workflow;

import org.mmbase.bridge.Cloud;

import java.util.*;

import net.sf.mmapps.commons.bridge.RelationUtil;
import net.sf.mmapps.commons.util.StringUtil;

import org.mmbase.bridge.*;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.security.*;
import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.services.workflow.Workflow;
import com.finalist.cmsc.services.workflow.WorkflowException;

public abstract class WorkflowManager {

   /** MMbase logging system */
   private static Logger log = Logging.getLoggerInstance(WorkflowManager.class.getName());

   protected static final String SOURCE = "SOURCE";
   protected static final String DESTINATION = "DESTINATION";

   public static final String WORKFLOWREL = "workflowrel";

   public final static String WORKFLOW_MANAGER_NAME = "workflowitem";
   public final static String NUMBER_FIELD = "number";
   public final static String STATUS_FIELD = "status";
   public final static String TYPE_FIELD = "type";
   public final static String REMARK_FIELD = "remark";
   public static final String CREATIONDATE_FIELD = "creationdate";
   public static final String LASTMODIFIEDDATE_FIELD = "lastmodifieddate";
   public static final String CREATOR_FIELD = "creator";
   public static final String LASTMODIFIER_FIELD = "lastmodifier";
   public static final String STACKTRACE_FIELD = "stacktrace";

   public final static String CREATORREL = "creatorrel";
   public final static String ASSIGNEDREL = "assignedrel";

   public static final String STATUS_DRAFT = "draft";
   public static final String STATUS_FINISHED = "finished";
   public static final String STATUS_APPROVED = "approved";
   public static final String STATUS_PUBLISHED = "published";

   public static final String TYPE_ALL = "all";

   protected Cloud cloud;


   public WorkflowManager(Cloud cloud) {
      this.cloud = cloud;
   }


   public static NodeManager getManager(Cloud cloud) {
      return cloud.getNodeManager(WORKFLOW_MANAGER_NAME);
   }


   protected NodeManager getManager() {
      return cloud.getNodeManager(WORKFLOW_MANAGER_NAME);
   }


   /**
    * Retrieve the workflowitem related to a contentelement.
    * 
    * @param node
    *           node in workflow
    * @return workflow item
    */
   protected Node getWorkflowNode(Node node, String type) {
      NodeList list = getWorkflows(node);
      for (Iterator<Node> iter = list.iterator(); iter.hasNext();) {
         Node workflow = iter.next();
         if (!workflow.getStringValue(TYPE_FIELD).equals(type)) {
            iter.remove();
         }
      }
      if (!list.isEmpty()) {
         return list.getNode(0);
      }
      return null;
   }


   /**
    * Check if a contentnode has a workflow
    * 
    * @param node
    *           node in workflow
    * @return true if the node has a related workflowitem
    */
   protected boolean hasWorkflow(Node node, String type) {
      if (!isWorkflowElement(node, false)) {
         return false;
      }
      NodeList list = getWorkflows(node, type);
      log.debug("Node " + node.getNumber() + " has workflow " + !list.isEmpty());
      return !list.isEmpty();
   }


   public abstract boolean isWorkflowElement(Node node, boolean isWorkflowItem);


   protected abstract List<Node> getUsersWithRights(Node channel, Role role);


   /**
    * Is the user allowed to approve the node
    * 
    * @param node
    *           Node to check for
    * @return <code>true</code> when allowed
    */
   public boolean isAllowedToAccept(Node node) {
      return getUserRole(node).getRole().getId() >= Role.EDITOR.getId();
   }


   /**
    * Is the user allowed to publish the node
    * 
    * @param node
    *           Node to check for
    * @return <code>true</code> when allowed
    */
   public final boolean isAllowedToPublish(Node node) {
      return getUserRole(node).getRole().getId() >= Role.CHIEFEDITOR.getId();
   }


   public abstract Node createFor(Node node, String remark);


   public abstract void finishWriting(Node node, String remark);


   public abstract void accept(Node node, String remark);


   public abstract void reject(Node node, String remark);


   public abstract void publish(Node node) throws WorkflowException;


   public abstract void publish(Node node, List<Integer> publishNumbers) throws WorkflowException;


   public abstract void complete(Node node);


   public abstract UserRole getUserRole(Node node);


   /**
    * Retrieve the workflowitem related to a contentelement.
    * 
    * @param node
    *           node in workflow
    * @return workflow item
    */
   protected abstract Node getWorkflowNode(Node node);


   public Node createFor(Node node, String remark, List<Node> nodeList) {
      Node wfItem = getWorkflowNode(node);
      if (wfItem == null) {
         wfItem = createFor(node, remark);
      }

      if (!nodeList.isEmpty()) {
         Set<Integer> workflowNodeNumber = new HashSet<Integer>();
         NodeList workflowNodes = getAllWorkflowNodes(wfItem);
         for (Iterator<Node> iterator = workflowNodes.iterator(); iterator.hasNext();) {
            Node workflowNode = iterator.next();
            workflowNodeNumber.add(workflowNode.getNumber());
         }

         for (Node nodeItem : nodeList) {
            if (!workflowNodeNumber.contains(nodeItem.getNumber())) {
               RelationUtil.createRelation(wfItem, nodeItem, WORKFLOWREL);
            }
         }
      }
      return wfItem;
   }


   protected NodeList getAllWorkflowNodes(Node wfItem) {
      NodeList workflowNodes = wfItem.getRelatedNodes("object", WORKFLOWREL, DESTINATION);
      return workflowNodes;
   }


   public void remove(Node node) {
      if (hasWorkflow(node, TYPE_ALL)) {
         NodeList list = getWorkflows(node);
         for (Iterator<Node> iter = list.iterator(); iter.hasNext();) {
            Node wf = iter.next();
            if (wf.getStringValue(STATUS_FIELD).equals(STATUS_PUBLISHED)) {
               Publish.remove(node);
            }
            deleteWorkflow(wf);
         }
      }
   }


   protected NodeList getWorkflows(Node node) {
      return node.getRelatedNodes(WORKFLOW_MANAGER_NAME, WORKFLOWREL, SOURCE);
   }


   protected NodeList getWorkflows(Node node, String type) {
      NodeList list = getWorkflows(node);
      if (!TYPE_ALL.equals(type)) {
         for (Iterator<Node> iter = list.iterator(); iter.hasNext();) {
            Node workflow = iter.next();
            if (!workflow.getStringValue(TYPE_FIELD).equals(type)) {
               iter.remove();
            }
         }
      }
      return list;
   }


   /**
    * Get status of the workflow of a node
    * 
    * @param node
    *           node in workflow
    * @return status of workflow. When there is no workflow then the draft
    *         status is returned
    */
   public String getStatus(Node node) {
      NodeList list = getWorkflows(node);
      if (!list.isEmpty()) {
         return list.getNode(0).getStringValue(STATUS_FIELD);
      }
      return STATUS_DRAFT;
   }


   private boolean isStatusFinished(Node wfItem) {
      String status = wfItem.getStringValue(STATUS_FIELD);
      return STATUS_FINISHED.equals(status) || STATUS_APPROVED.equals(status) || STATUS_PUBLISHED.equals(status);
   }


   private boolean isStatusApproved(Node wfItem) {
      if (Workflow.isAcceptedStepEnabled()) {
         String status = wfItem.getStringValue(STATUS_FIELD);
         return STATUS_APPROVED.equals(status) || STATUS_PUBLISHED.equals(status);
      }
      return isStatusFinished(wfItem);
   }


   protected boolean isStatusPublished(Node wfItem) {
      return wfItem.getStringValue(STATUS_FIELD).equals(STATUS_PUBLISHED);
   }


   protected Node createFor(String type, String remark) {
      return createFor(type, remark, STATUS_DRAFT);
   }


   protected Node createFor(String type, String remark, String status) {
      NodeManager workflow = getManager();
      Node wfItem = workflow.createNode();
      wfItem.setStringValue(TYPE_FIELD, type);
      changeWorkflow(wfItem, status, remark);

      Node user = SecurityUtil.getUserNode(cloud);
      RelationManager assignedrel = cloud.getRelationManager(WORKFLOW_MANAGER_NAME, SecurityUtil.USER, ASSIGNEDREL);
      wfItem.createRelation(user, assignedrel).commit();

      RelationManager creatorrel = cloud.getRelationManager(WORKFLOW_MANAGER_NAME, SecurityUtil.USER, CREATORREL);
      wfItem.createRelation(user, creatorrel).commit();

      return wfItem;
   }


   /**
    * Status change to 'FINISHED'. The workflow appears on all editor workflow
    * screens
    */
   protected void finishWriting(Node wfItem, Node rightsNode, String remark) {
      /*
       * Actions: - Change status of related workflowitem to FINISHED - Find all
       * users with at least EDITOR rights on creation channel - relate all
       * users to the workflowitem
       */
      if (!isStatusFinished(wfItem)) {
         changeWorkflow(wfItem, STATUS_FINISHED, remark);
         Role role = Role.EDITOR;
         if (!Workflow.isAcceptedStepEnabled()) {
            role = Role.CHIEFEDITOR;
         }
         List<Node> users = getUsersWithRights(rightsNode, role);
         changeUserRelations(wfItem, users);
      }
   }


   /**
    * Status change to 'APPROVED'. The workflow appears on all chiefeditor
    * workflow screens
    */
   protected void accept(Node wfItem, Node rightsNode, String remark) {
      /*
       * Actions: - Change status of related workflowitem to APPROVED - Find all
       * users with at least CHIEFEDITOR rights on channel - relate all users to
       * the workflowitem
       */
      // accept nodes first to avoid publish errors
      if (Workflow.isAcceptedStepEnabled()) {
         if (!isStatusApproved(wfItem)) {
            if (isAllowedToAccept(rightsNode)) {
               changeWorkflow(wfItem, STATUS_APPROVED, remark);
               List<Node> users = getUsersWithRights(rightsNode, Role.CHIEFEDITOR);
               changeUserRelations(wfItem, users);
            }
         }
      }
      else {
         finishWriting(wfItem, rightsNode, remark);
      }
   }


   protected void changeWorkflow(Node wfItem, String status, String remark) {
      wfItem.setStringValue(STATUS_FIELD, status);
      if (!StringUtil.isEmpty(remark)) {
         wfItem.setStringValue(REMARK_FIELD, remark);
      }
      wfItem.commit();
   }

   protected void changeWorkflowFailPublished(Node wfItem, String status, String stacktrace) {
      // Need to bypass the MMBase bridge to change workflow items.
      // The bridge causes the lastmodifier field to be changed, which is visible for the end user.
      MMObjectBuilder wfBuilder = MMBase.getMMBase().getBuilder(WORKFLOW_MANAGER_NAME);
      MMObjectNode mmNode = wfBuilder.getNode(wfItem.getNumber());

      mmNode.setValue(STATUS_FIELD, status);
      if (!StringUtil.isEmpty(stacktrace)) {
         mmNode.setValue(STACKTRACE_FIELD, stacktrace);
      }
      wfBuilder.commit(mmNode);
   }


   /**
    * Rename the remark
    * 
    * @param wfItem
    * @param remark
    */
   public void remark(Node wfItem, String remark) {
      wfItem.setStringValue(REMARK_FIELD, remark);
      wfItem.commit();
   }


   protected void changeUserRelations(Node wfItem, List<Node> users) {
      removeRelationsToUsers(wfItem);
      relateToUsers(wfItem, users);
   }


   protected void rejectWorkflow(Node wfItem, String remark) {
      if (isStatusPublished(wfItem)) {
         if (Workflow.isAcceptedStepEnabled()) {
        	 changeWorkflowFailPublished(wfItem, STATUS_APPROVED,remark);
         }
         else {
        	 changeWorkflowFailPublished(wfItem, STATUS_FINISHED,remark);
         }
      }
      else {
         changeWorkflow(wfItem, STATUS_DRAFT, remark);
         removeRelationsToUsers(wfItem);
         NodeList list = wfItem.getRelatedNodes(SecurityUtil.USER, CREATORREL, DESTINATION);
         relateToUsers(wfItem, list);
      }
   }


   protected void publish(Node node, String type, List<Integer> publishNumbers) throws WorkflowException {
      if (isWorkflowElement(node, false) && hasWorkflow(node, type)) {
         Node wf = getWorkflowNode(node, type);
         if (wf != null) {
            if (!isStatusApproved(wf)) {
               accept(node, null);
               wf = getWorkflowNode(node, type);
            }

            if (isStatusApproved(wf) && isAllowedToPublish(node)) {
               List<Node> errors = new ArrayList<Node>();
               log.debug("Got valid workflowtype: " + wf.getNodeManager().getName() + ", checking relations");
               if (isReadyToPublish(node, errors, publishNumbers)) {
                  changeWorkflow(wf, STATUS_PUBLISHED, "");
                  publishInternal(wf, node);
               }
               else {
                  throw new WorkflowException("Could not publish, because its relations still have a workflow", errors);
               }
            }
         }
         else {
            log.error("Node to publish is not a proper workflowtype! " + node.getNumber());
         }
      }
   }


   protected void publishInternal(Node wf, Node node) {
      Publish.publish(node);
   }


   public List<Node> isReadyToPublish(Node node, List<Integer> publishNumbers) {
      List<Node> errors = new ArrayList<Node>();
      isReadyToPublish(node, errors, publishNumbers);
      return errors;
   }


   protected boolean isReadyToPublish(Node node, List<Node> errors, List<Integer> publishNumbers) {
      if (Publish.isPublishable(node)) {
         checkNode(node, errors, publishNumbers);
         if (errors.size() > 0) {
            if (log.isDebugEnabled()) {
               log.debug("Not ready to publish:");
               for (int i = 0; i < errors.size(); i++) {
                  log.debug(":: " + errors.get(i));
               }
            }
            return false;
         }
         log.debug("Ready to publish!");
         return true;
      }
      else {
         log.error("isReadyToPublish() called with a non-publishable element: " + node.getNodeManager().getName());
         return false;
      }
   }


   @SuppressWarnings("unused")
   protected void checkNode(Node node, List<Node> errors, List<Integer> publishNumbers) {
      // no errors
   }


   protected void complete(Node contentNode, String type) {
      NodeList workflows = getWorkflows(contentNode, type);
      for (Iterator<Node> iter = workflows.iterator(); iter.hasNext();) {
         Node workflow = iter.next();
         deleteWorkflow(workflow);
      }
   }


   private void deleteWorkflow(Node workflow) {
      removeRelationsToUsers(workflow);
      workflow.delete(true);
   }


   protected void removeRelationsToUsers(Node workflowItem) {
      workflowItem.deleteRelations(ASSIGNEDREL);
   }


   protected void relateToUsers(Node workflowItem, List<Node> users) {
      RelationManager manager = cloud.getRelationManager(WORKFLOW_MANAGER_NAME, SecurityUtil.USER, ASSIGNEDREL);

      for (Iterator<Node> iter = users.iterator(); iter.hasNext();) {
         workflowItem.createRelation(iter.next(), manager).commit();
      }
   }


   public static boolean isWorkflowItem(Node node) {
      return WORKFLOW_MANAGER_NAME.equals(node.getNodeManager().getName());
   }


   public static NodeQuery createListQuery(Cloud cloud) {
      Node userNode = SecurityUtil.getUserNode(cloud);

      NodeQuery query = cloud.createNodeQuery();
      Step step1 = query.addStep(userNode.getNodeManager());
      query.addNode(step1, userNode);

      RelationStep workflowitemStep = query.addRelationStep(getManager(cloud), ASSIGNEDREL, SOURCE);
      Step step3 = workflowitemStep.getNext();
      query.setNodeStep(step3); // makes it ready for use as NodeQuery
      return query;
   }


   public static NodeQuery createDetailQuery(Cloud cloud, NodeManager manager) {
      NodeQuery wfQuery = cloud.createNodeQuery();
      Step wfStep = wfQuery.addStep(getManager(cloud));
      wfQuery.addRelationStep(manager, WORKFLOWREL, DESTINATION);
      wfQuery.setNodeStep(wfStep); // makes it ready for use as NodeQuery
      return wfQuery;
   }


   public static FieldValueConstraint getStatusConstraint(NodeQuery query, String status) {
      Field field = getManager(query.getCloud()).getField(STATUS_FIELD);
      FieldValueConstraint constraint = query.createConstraint(query.getStepField(field), FieldCompareConstraint.EQUAL,
            status);
      return constraint;
   }


   public static Constraint getTypeConstraint(NodeQuery query, String type) {
      Field field = getManager(query.getCloud()).getField(TYPE_FIELD);
      FieldValueConstraint constraint = query.createConstraint(query.getStepField(field), FieldCompareConstraint.EQUAL,
            type);
      return constraint;
   }


   public static Query createStatusQuery(Cloud cloud) {
      Node userNode = SecurityUtil.getUserNode(cloud);

      NodeQuery query = cloud.createNodeQuery();
      Step step1 = query.addStep(userNode.getNodeManager());
      query.addNode(step1, userNode);

      NodeManager workflowManager = getManager(cloud);
      RelationStep workflowitemStep = query.addRelationStep(workflowManager, ASSIGNEDREL, SOURCE);
      Step step3 = workflowitemStep.getNext();
      query.setNodeStep(step3); // makes it ready for use as NodeQuery

      Query clone = query.aggregatingClone();

      clone.addAggregatedField(step3, workflowManager.getField(TYPE_FIELD), AggregatedField.AGGREGATION_TYPE_GROUP_BY);
      clone
            .addAggregatedField(step3, workflowManager.getField(STATUS_FIELD),
                  AggregatedField.AGGREGATION_TYPE_GROUP_BY);
      clone.addAggregatedField(step3, workflowManager.getField(NUMBER_FIELD), AggregatedField.AGGREGATION_TYPE_COUNT);

      return clone;
   }

}