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
import org.apache.commons.logging.Log;

import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.services.Service;
import com.finalist.cmsc.services.Properties;

public abstract class WorkflowService extends Service {
   protected boolean acceptedStepEnabled;


   public abstract Node create(Node node, String remark);


   public abstract Node create(Node node, String remark, List<Node> nodeList);


   public abstract void finish(Node node, String remark);


   public abstract void accept(Node node, String remark);


   public abstract void reject(Node node, String remark);


   public abstract void publish(Node node) throws WorkflowException;


   public abstract void publish(Node node, List<Integer> publishNumbers) throws WorkflowException;


   public abstract void complete(Node node);


   public abstract boolean isWorkflowType(String type);


   public abstract boolean hasWorkflow(Node node);


   public abstract boolean isWorkflowElement(Node node);


   public abstract String getStatus(Node node);


   public abstract boolean mayEdit(Node node);


   public abstract boolean mayEdit(Node node, UserRole userrole);


   public abstract boolean mayPublish(Node node);


   public abstract boolean mayPublish(Node node, UserRole userrole);


   public abstract List<Node> isReadyToPublish(Node node, List<Integer> publishNumbers);


   public abstract void remove(Node node);


   public abstract void remark(Node node, String remark);


   public abstract WorkflowStatusInfo getStatusInfo(Cloud cloud);


   public abstract boolean isAllowedToPublish(Node node);


   @Override
   protected void init(Properties properties) throws Exception {

      acceptedStepEnabled = properties.getBoolean("workflow.use.state.accepted", true);
      if (acceptedStepEnabled) {
         getLogger().info("Step 'accepted' enabled.");
      }
      else {
         getLogger().info("Step 'accepted' disabled.");
      }

   }


   public boolean isAcceptedStepEnabled() {
      return acceptedStepEnabled;
   }


   public void setAcceptedStepEnabled(boolean acceptedStepEnabled) {
      this.acceptedStepEnabled = acceptedStepEnabled;
   }


   protected abstract Log getLogger();

}
