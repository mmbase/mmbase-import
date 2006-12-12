package com.finalist.cmsc.workflow;

import java.util.*;

import org.mmbase.bridge.*;
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
    * @param node node in workflow
    * @return workflow item
    */
   protected Node getWorkflowNode(Node node, String type) {
      NodeList list = getWorkflows(node);
      for (Iterator iter = list.iterator(); iter.hasNext();) {
        Node workflow = (Node) iter.next();
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
    * @param node node in workflow
    * @return true if the node has a related workflowitem
    */
   protected boolean hasWorkflow(Node node, String type) {
      if (!isWorkflowElement(node)) {
         return false;
      }
      NodeList list = getWorkflows(node, type);
      log.debug("Node " + node.getNumber() + " has workflow " + !list.isEmpty());
      return !list.isEmpty();
   }

   public abstract boolean isWorkflowElement(Node node);
   protected abstract List getUsersWithRights(Node channel, Role role);
   
   public abstract boolean isAllowedToPublish(Node node);
   public abstract boolean isAllowedToAccept(Node node) ;
   
   public abstract Node createFor(Node node, String remark);
   public abstract void finishWriting(Node node, String remark);
   public abstract void accept(Node node, String remark);
   public abstract void reject(Node node, String remark);
   public abstract void publish(Node node) throws WorkflowException;
   public abstract void publish(Node node, List<Integer> publishNumbers) throws WorkflowException;
   public abstract void complete(Node node);
   public abstract UserRole getUserRole(Node node);


   public void remove(Node node) {
       if (hasWorkflow(node, TYPE_ALL)) {
           NodeList list = getWorkflows(node);
           for (Iterator iter = list.iterator(); iter.hasNext();) {
             Node wf = (Node) iter.next();
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
           for (Iterator iter = list.iterator(); iter.hasNext();) {
               Node workflow = (Node) iter.next();
               if (!workflow.getStringValue(TYPE_FIELD).equals(type)) {
                   iter.remove();
               }
           }
       }
       return list;
   }
   

   /** Get status of the workflow of a node
    * @param node node in workflow
    * @return status of workflow. When there is no workflow then the draft status is returned
    */
   public String getStatus(Node node) {
      NodeList list =  getWorkflows(node);
      if (!list.isEmpty()) {
         return list.getNode(0).getStringValue(STATUS_FIELD);
      }
      return STATUS_DRAFT;
   }

   private boolean isStatusFinished(Node wfItem) {
       String status = wfItem.getStringValue(STATUS_FIELD);
       return STATUS_FINISHED.equals(status) || 
              STATUS_APPROVED.equals(status) || 
              STATUS_PUBLISHED.equals(status);
   }

   private boolean isStatusApproved(Node wfItem) {
       String status = wfItem.getStringValue(STATUS_FIELD);
       return STATUS_APPROVED.equals(status) || STATUS_PUBLISHED.equals(status);
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
    * Status change to 'FINISHED'. The workflow appears on all editor workflow screens
    */
   protected void finishWriting(Node wfItem, Node rightsNode, String remark) {
    /* Actions:
       * - Change status of related workflowitem to FINISHED
       * - Find all users with at least EDITOR rights on creation channel
       * - relate all users to the workflowitem
       */
       if (!isStatusFinished(wfItem)) {
          changeWorkflow(wfItem, STATUS_FINISHED, remark);
          List users = getUsersWithRights(rightsNode, Role.EDITOR);
          changeUserRelations(wfItem, users);
       }
   }

   /**
    * Status change to 'APPROVED'. The workflow appears on all chiefeditor workflow screens
    */
   protected void accept(Node wfItem, Node rightsNode, String remark) {
    /* Actions:
       * - Change status of related workflowitem to APPROVED
       * - Find all users with at least CHIEFEDITOR rights on channel
       * - relate all users to the workflowitem
       */
       // accept nodes first to avoid publish errors
       if (Workflow.isAcceptedStepEnabled()) {
          if (!isStatusApproved(wfItem)) {
              changeWorkflow(wfItem, STATUS_APPROVED, remark);
              List users = getUsersWithRights(rightsNode, Role.CHIEFEDITOR);
              changeUserRelations(wfItem, users);
          }
       }
       else {
           finishWriting(wfItem, rightsNode, remark);
       }
   }

    protected void changeWorkflow(Node wfItem, String status, String remark) {
        wfItem.setStringValue(STATUS_FIELD, status);
        if(remark != null) {
        	wfItem.setStringValue(REMARK_FIELD, remark);
        }
        wfItem.commit();
    }

    /**
     * Do rename the remark
     * @param wfItem
     * @param remark
     */
    public void rename(Node wfItem, String remark) {
    	wfItem.setStringValue(REMARK_FIELD, remark);
        wfItem.commit();
    }

    protected void changeUserRelations(Node wfItem, List users) {
        removeRelationsToUsers(wfItem);
        relateToUsers(wfItem, users);
    }
   
    protected void rejectWorkflow(Node wfItem, String remark) {
        if (isStatusPublished(wfItem)) {
           changeWorkflow(wfItem, STATUS_APPROVED, remark);
        }
        else {
            changeWorkflow(wfItem, STATUS_DRAFT, remark);
            removeRelationsToUsers(wfItem);
            NodeList list = wfItem.getRelatedNodes(SecurityUtil.USER, CREATORREL, DESTINATION);
            relateToUsers( wfItem, list);
        }
    }

    protected void publish(Node node, boolean skiptest, String type, List<Integer> publishNumbers) throws WorkflowException {
        if (isWorkflowElement(node) && hasWorkflow(node, type)) {
            Node wf = getWorkflowNode(node, type);
            if (wf != null) {
                if (!isStatusApproved(wf)) {
                    accept(node, null);
                }
                List<Node> errors = new ArrayList<Node>();
                log.debug("Got valid workflowtype: " + wf.getNodeManager().getName()
                        + ", checking relations");
                if (skiptest || isReadyToPublish(node, errors, publishNumbers)) {
                    changeWorkflow(wf, STATUS_PUBLISHED, "");
                    Publish.publish(node);
                }
                else {
                    throw new WorkflowException(
                            "Could not publish, because its relations still have a workflow",
                            errors);
                }
            }
            else {
                log.error("Node to publish is not a proper workflowtype! " + node.getNumber());
            }
        }
    }
    
    public List<Node> isReadyToPublish(Node node, List<Integer> publishNumbers) {
        List<Node> errors = new ArrayList<Node>();
        isReadyToPublish(node, errors, publishNumbers);
        return errors;
    }
    
    protected boolean isReadyToPublish(Node node, List<Node> errors, List<Integer> publishNumbers) {
        if(Publish.isPublishable(node)) {
           checkNode(node, errors, publishNumbers);
           if(errors.size() > 0) {
               if (log.isDebugEnabled()) {
              log.debug("Not ready to publish:");
              for(int i=0; i<errors.size(); i++) {
                 log.debug(":: " + errors.get(i));
              }
               }
              return false;
           }
           log.debug("Ready to publish!");
           return true;
        } else {
           log.error("isReadyToPublish() called with a non-publishable element: " + node.getNodeManager().getName());
           return false;
        }
    }

    protected void checkNode(Node node, List<Node> errors, List<Integer> publishNumbers) {
        // no errors
    }
    
    protected void complete(Node contentNode, String type) {
        NodeList workflows = getWorkflows(contentNode, type);
        for (Iterator iter = workflows.iterator(); iter.hasNext();) {
           Node workflow = (Node) iter.next();
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

   protected void relateToUsers(Node workflowItem, List users) {
      RelationManager manager = cloud.getRelationManager(WORKFLOW_MANAGER_NAME, SecurityUtil.USER, ASSIGNEDREL);

      for (Iterator iter = users.iterator(); iter.hasNext();) {
         workflowItem.createRelation((Node) iter.next(), manager).commit();
      }
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
       FieldValueConstraint constraint = query.createConstraint(query.getStepField(field),
             FieldCompareConstraint.EQUAL, status);
       return constraint;
   }

   public static Constraint getTypeConstraint(NodeQuery query, String type) {
       Field field = getManager(query.getCloud()).getField("type");
       FieldValueConstraint constraint = query.createConstraint(query.getStepField(field),
             FieldCompareConstraint.EQUAL, type);
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
        clone.addAggregatedField(step3, workflowManager.getField(STATUS_FIELD), AggregatedField.AGGREGATION_TYPE_GROUP_BY);
        clone.addAggregatedField(step3, workflowManager.getField(NUMBER_FIELD), AggregatedField.AGGREGATION_TYPE_COUNT);
        
        return clone;
    }


}