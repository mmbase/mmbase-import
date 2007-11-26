package com.finalist.cmsc.services.workflow;

import java.util.*;

import org.mmbase.bridge.Node;

/**
 * WorkFlowException.
 */
@SuppressWarnings("serial")
public class WorkflowException extends Exception {

   private List<Node> errors;


   public WorkflowException(String message, List<Node> errors) {
      super(message);
      this.errors = errors;
   }


   public WorkflowException(String message, List<Node> errors, Throwable cause) {
      super(message, cause);
      this.errors = errors;
   }


   public List<Node> getErrors() {
      return errors;
   }


   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(super.toString() + ": ");
      for (Node errorNode : errors) {
         sb.append(errorNode.getNodeManager().getName() + " " + errorNode.getNumber() + " ");
      }
      return sb.toString();
   }
}