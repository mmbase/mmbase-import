/*
 * 
 * This software is OSI Certified Open Source Software. OSI Certified is a
 * certification mark of the Open Source Initiative.
 * 
 * The license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package com.finalist.cmsc.workflow.forms;

import java.util.ArrayList;
import java.util.List;

import org.mmbase.bridge.Node;

import com.finalist.cmsc.services.workflow.Workflow;
import com.finalist.cmsc.services.workflow.WorkflowException;

public class WorkflowUtil {

   public static final String ACTION_FINISH = "finish";

   public static final String ACTION_ACCEPT = "accept";

   public static final String ACTION_REJECT = "reject";

   public static final String ACTION_PUBLISH = "publish";

   public static final String ACTION_RENAME = "rename";

   
   public static List<Node> performWorkflowAction(String action, List<Node> nodes, String remark) {
      List<Node> errors = new ArrayList<Node>();

      if (ACTION_FINISH.equals(action)) {
         for (Node node : nodes) {
            Workflow.finish(node, remark);
         }
      }
      if (ACTION_ACCEPT.equals(action)) {
         for (Node node : nodes) {
            Workflow.accept(node, remark);
         }
      }
      if (ACTION_REJECT.equals(action)) {
         for (Node node : nodes) {
            // Node in status published might be completed already before
            // request reaches this point.
            if (node.getCloud().hasNode(node.getNumber())) {
               Workflow.reject(node, remark);
            }
         }
      }
      if (ACTION_RENAME.equals(action)) {
         for (Node node : nodes) {
            Workflow.remark(node, remark);
         }
      }
      if (ACTION_PUBLISH.equals(action)) {
         List<Integer> publishNumbers = new ArrayList<Integer>();
         for (Node publishNode : nodes) {
            if (Workflow.isAllowedToPublish(publishNode)) {
               publishNumbers.add((publishNode).getNumber());
            }
         }

         for (Node publishNode : nodes) {
            try {
               if (publishNumbers.contains(publishNode.getNumber())) {
                  Workflow.publish(publishNode, publishNumbers);
               } else {
                  Workflow.accept(publishNode, "");
               }
            } catch (WorkflowException wfe) {
               errors.addAll(wfe.getErrors());
            }
         }
      }
      return errors;
   }

}
