/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.workflow;

import java.util.List;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.services.ServiceManager;

public class Workflow {

   public static final String STATUS_DRAFT = "draft";
   public static final String STATUS_FINISHED = "finished";
   public static final String STATUS_APPROVED = "approved";
   public static final String STATUS_PUBLISHED = "published";

   private final static WorkflowService cService = (WorkflowService) ServiceManager.getService(WorkflowService.class);


   public static Node create(Node node, String remark) {
      return cService.create(node, remark);
   }


   public static Node create(Node node, String remark, List<Node> nodeList) {
      return cService.create(node, remark, nodeList);
   }


   public static void finish(Node node, String remark) {
      cService.finish(node, remark);
   }


   public static void accept(Node node, String remark) {
      cService.accept(node, remark);
   }


   public static void reject(Node node, String remark) {
      cService.reject(node, remark);
   }


   public static void publish(Node node) throws WorkflowException {
      cService.publish(node);
   }


   public static void publish(Node node, List<Integer> publishNumbers) throws WorkflowException {
      cService.publish(node, publishNumbers);
   }


   public static void complete(Node node) {
      cService.complete(node);
   }


   public static boolean isWorkflowType(String type) {
      return cService.isWorkflowType(type);
   }


   public static boolean hasWorkflow(Node node) {
      return cService.hasWorkflow(node);
   }


   public static String getStatus(Node node) {
      return cService.getStatus(node);
   }


   public static boolean mayEdit(Node node) {
      return cService.mayEdit(node);
   }


   public static boolean mayEdit(Node node, UserRole userrole) {
      return cService.mayEdit(node, userrole);
   }


   public static boolean mayPublish(Node node) {
      return cService.mayPublish(node);
   }


   public static boolean mayPublish(Node node, UserRole userrole) {
      return cService.mayPublish(node, userrole);
   }


   public static boolean isWorkflowElement(Node node) {
      return cService.isWorkflowElement(node);
   }


   public static boolean isAcceptedStepEnabled() {
      return cService.isAcceptedStepEnabled();
   }


   public static List<Node> isReadyToPublish(Node node, List<Integer> publishNumbers) {
      return cService.isReadyToPublish(node, publishNumbers);
   }


   public static void remove(Node node) {
      cService.remove(node);
   }


   public static void remark(Node node, String remark) {
      cService.remark(node, remark);
   }


   public static WorkflowStatusInfo getStatusInfo(Cloud cloud) {
      return cService.getStatusInfo(cloud);
   }


   public static boolean isAllowedToPublish(Node node) {
      return cService.isAllowedToPublish(node);
   }
}
