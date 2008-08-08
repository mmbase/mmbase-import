/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.workflow;

import java.util.List;

import net.sf.mmapps.commons.bridge.CloudUtil;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.*;

import com.finalist.cmsc.security.Role;
import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.workflow.*;

public class WorkflowServiceMMBaseImpl extends WorkflowService {

   @Override
   public Node create(Node node, String remark) {
      return getManager(node).createFor(node, remark);
   }


   @Override
   public Node create(Node node, String remark, List<Node> nodeList) {
      return getManager(node).createFor(node, remark, nodeList);
   }


   @Override
   public void finish(Node node, String remark) {
      getManager(node).finishWriting(node, remark);
   }


   @Override
   public void accept(Node node, String remark) {
      if (isAcceptedStepEnabled()) {
         getManager(node).accept(node, remark);
      }
      else {
         getManager(node).finishWriting(node, remark);
      }
   }


   @Override
   public void reject(Node node, String remark) {
      getManager(node).reject(node, remark);
   }


   @Override
   public void publish(Node node) throws WorkflowException {
      getManager(node).publish(node);
   }


   @Override
   public void publish(Node node, List<Integer> publishNumbers) throws WorkflowException {
      getManager(node).publish(node, publishNumbers);
   }


   @Override
   public void complete(Node node) {
      getManager(node).complete(node);
   }


   @Override
   public void remove(Node node) {
      getManager(node).remove(node);
   }


   @Override
   public void remark(Node node, String remark) {
      getManager(node).remark(node, remark);
   }


   private WorkflowManager getManager(Node node) {
      boolean isWorkflowItem = WorkflowManager.isWorkflowItem(node);

      WorkflowManager manager = getContentWorkflow(node.getCloud());
      if (manager.isWorkflowElement(node, isWorkflowItem)) {
         return manager;
      }
      manager = getPageWorkflow(node.getCloud());
      if (manager.isWorkflowElement(node, isWorkflowItem)) {
         return manager;
      }
      manager = getLinkWorkflow(node.getCloud());
      if (manager.isWorkflowElement(node, isWorkflowItem)) {
         return manager;
      }
      throw new IllegalArgumentException("Node was not a workflow element " + node);
   }


   private ContentWorkflow getContentWorkflow(Cloud cloud) {
      return new ContentWorkflow(cloud);
   }


   private LinkWorkflow getLinkWorkflow(Cloud cloud) {
      return new LinkWorkflow(cloud);
   }


   private PageWorkflow getPageWorkflow(Cloud cloud) {
      return new PageWorkflow(cloud);
   }


   @Override
   public String getStatus(Node node) {
      return getManager(node).getStatus(node);
   }


   @Override
   public boolean hasWorkflow(Node node) {
      return getContentWorkflow(node.getCloud()).hasWorkflow(node)
            || getPageWorkflow(node.getCloud()).hasWorkflow(node) || getLinkWorkflow(node.getCloud()).hasWorkflow(node);
   }


   @Override
   public boolean isWorkflowType(String type) {
      Cloud cloud = getUserCloud();
      return getContentWorkflow(cloud).isWorkflowType(type) || getPageWorkflow(cloud).isWorkflowType(type);
   }


   @Override
   public boolean isWorkflowElement(Node node) {
      Cloud cloud = node.getCloud();
      return getContentWorkflow(cloud).isWorkflowElement(node, false)
            || getPageWorkflow(cloud).isWorkflowElement(node, false)
            || getLinkWorkflow(cloud).isWorkflowElement(node, false);
   }


   private Cloud getUserCloud() {
      Cloud cloud = CloudUtil.getCloudFromThread();
      if (cloud == null) {
         cloud = CloudProviderFactory.getCloudProvider().getAdminCloud();
      }
      return cloud;
   }


   @Override
   public boolean mayEdit(Node node) {
      UserRole userrole = getManager(node).getUserRole(node);
      return mayEdit(node, userrole);
   }


   @Override
   public boolean mayEdit(Node node, UserRole userrole) {
      String status = getStatus(node);

      boolean deny = WorkflowManager.STATUS_PUBLISHED.equals(status)
            || (WorkflowManager.STATUS_APPROVED.equals(status) && (userrole.getRole() == Role.EDITOR || userrole
                  .getRole() == Role.WRITER));

      if (!deny && !Workflow.isAcceptedStepEnabled()) {
         deny = userrole.getRole() == Role.EDITOR && WorkflowManager.STATUS_FINISHED.equals(status);
      }

      return !deny;
   }


   @Override
   public boolean mayPublish(Node node) {
      UserRole userrole = getManager(node).getUserRole(node);
      return mayPublish(node, userrole);
   }


   @Override
   public boolean mayPublish(Node node, UserRole userrole) {
      String status = getStatus(node);

      boolean deny = WorkflowManager.STATUS_DRAFT.equals(status)
            || !(userrole.getRole() == Role.CHIEFEDITOR || userrole.getRole() == Role.WEBMASTER);

      return !deny;
   }


   @Override
   public List<Node> isReadyToPublish(Node node, List<Integer> publishNumbers) {
      return getManager(node).isReadyToPublish(node, publishNumbers);
   }


   @Override
   public boolean isAllowedToPublish(Node node) {
      return getManager(node).isAllowedToPublish(node);
   }


   @Override
   public boolean isAccepted(Node node) {
      if (hasWorkflow(node)) {
         String status = getStatus(node);
         if (isAcceptedStepEnabled()) {
            return WorkflowManager.STATUS_PUBLISHED.equals(status)
               || WorkflowManager.STATUS_APPROVED.equals(status);
         }
         else {
            return WorkflowManager.STATUS_PUBLISHED.equals(status);
         }
      }
      return true;
   }

   @Override
   public void addUserToWorkflow(Node node) {
      if (hasWorkflow(node)) {
         WorkflowManager manager = getManager(node);
         String status = manager.getStatus(node);
         if (WorkflowManager.STATUS_DRAFT.equals(status)) {
            manager.addUserToWorkflow(node);
         }
      }

   }


   @Override
   protected Log getLogger() {
      return LogFactory.getLog(WorkflowServiceMMBaseImpl.class);
   }


   @Override
   public WorkflowStatusInfo getStatusInfo(Cloud cloud) {
      Query statusQuery = WorkflowManager.createStatusQuery(cloud);
      NodeList statusList = cloud.getList(statusQuery);

      return new WorkflowStatusInfo(statusList);
   }

}
