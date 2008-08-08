/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.workflow;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.security.UserRole;

@SuppressWarnings("unused")
public class DummyWorkflowService extends WorkflowService {

   @Override
   public Node create(Node node, String remark) {
      // nothing
      return null;
   }


   @Override
   public Node create(Node node, String remark, List<Node> nodes) {
      // nothing
      return null;
   }


   @Override
   public void finish(Node node, String remark) {
      // nothing
   }


   @Override
   public void accept(Node node, String remark) {
      // nothing
   }


   @Override
   public void reject(Node node, String remark) {
      // nothing
   }


   @Override
   public void publish(Node node) throws WorkflowException {
      // nothing
   }


   @Override
   public void publish(Node node, List<Integer> publishNumbers) throws WorkflowException {
      // nothing
   }


   @Override
   public void complete(Node node) {
      // nothing
   }


   @Override
   public void remove(Node node) {
      // nothing
   }


   @Override
   public void remark(Node node, String remark) {
      // nothing
   }


   @Override
   public String getStatus(Node node) {
      // nothing
      return null;
   }


   @Override
   public boolean hasWorkflow(Node node) {
      // nothing
      return false;
   }


   @Override
   public boolean isWorkflowType(String type) {
      // nothing
      return false;
   }


   @Override
   public boolean isWorkflowElement(Node node) {
      return false;
   }


   @Override
   public boolean mayEdit(Node node) {
      return true;
   }


   @Override
   public boolean mayEdit(Node node, UserRole userrole) {
      return true;
   }


   @Override
   public boolean mayPublish(Node node) {
      return true;
   }


   @Override
   public boolean mayPublish(Node node, UserRole userrole) {
      return true;
   }


   @Override
   public List<Node> isReadyToPublish(Node node, List<Integer> publishNumbers) {
      return new ArrayList<Node>();
   }


   @Override
   public boolean isAllowedToPublish(Node node) {
      return true;
   }


   @Override
   public boolean isAccepted(Node node) {
      return true;
   }

   @Override
   public void addUserToWorkflow(Node node) {
      // nothing
   }

   @Override
   protected Log getLogger() {
      return LogFactory.getLog(DummyWorkflowService.class);
   }


   @Override
   public WorkflowStatusInfo getStatusInfo(Cloud cloud) {
      return null;
   }

}
