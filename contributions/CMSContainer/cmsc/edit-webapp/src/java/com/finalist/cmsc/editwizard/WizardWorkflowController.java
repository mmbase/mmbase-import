/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.editwizard;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.applications.editwizard.Config;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.security.Rank;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.services.workflow.Workflow;
import com.finalist.cmsc.services.workflow.WorkflowException;

public class WizardWorkflowController extends WizardController {

   private static final String OFF = "off";

   private static final String WORKFLOWCOMMAND = "workflowcommand";
   private static final String WORKFLOWCOMMENT = "workflowcomment";
   private static final String ERRORS = "errors";

   private static final String CANCEL = "cancel";
   private static final String FINISH = "finish";
   private static final String ACCEPT = "accept";
   private static final String REJECT = "reject";
   private static final String PUBLISH = "publish";

   /**
    * MMbase logging system
    */
   private static final Logger log = Logging.getLoggerInstance(WizardWorkflowController.class.getName());


   /**
    * Additional actions to open the wizard
    *
    * @param request -
    *           http request
    * @param ewconfig -
    *           editwizard config
    * @param config -
    *           wizard config
    * @param cloud -
    *           cloud
    */
   @Override
   public void openWizard(HttpServletRequest request, Config ewconfig, Config.WizardConfig config, Cloud cloud,
         Map<String, String> params, UserRole userrole, String elementtype) {

      String objectnr = config.objectNumber;

      if (isMainWizard(ewconfig, config) && elementtype != null && !"".equals(elementtype)
            && Workflow.isWorkflowType(elementtype)) {

         params.put("WORKFLOW", TRUE);
         params.put("WORKFLOW-ACCEPTED-ENABLED", Boolean.toString(Workflow.isAcceptedStepEnabled()));

         String activity = "DRAFT";

         if (!NEW_OBJECT.equals(objectnr)) {
            Node node = cloud.getNode(objectnr);
            // The closeWizard() will create one if it is not present
            if (Workflow.hasWorkflow(node)) {
               String status = Workflow.getStatus(node);

               if (Workflow.STATUS_DRAFT.equals(status)) {
                  activity = "DRAFT";
               }
               if (Workflow.STATUS_FINISHED.equals(status)) {
                  activity = "FINISHED";
               }
               if (Workflow.STATUS_APPROVED.equals(status)) {
                  activity = "APPROVED";
               }
               if (Workflow.STATUS_PUBLISHED.equals(status)) {
                  activity = "PUBLISHED";
               }
            }

            if (!Workflow.mayEdit(node, userrole)) {
               params.put("READONLY", TRUE);
               if (Workflow.STATUS_PUBLISHED.equals(Workflow.getStatus(node))) {
                  params.put("READONLY-REASON", "PUBLISH");
               }
               else {
                  params.put("READONLY-REASON", "WORKFLOW");
               }
            }
         }
         else {
            if (PagesUtil.isPageType(cloud.getNodeManager(elementtype))) {
               // disable workflow for a new page
               params.put("WORKFLOW", OFF);
            }
         }
         log.debug("activity " + activity);
         params.put("ACTIVITY", activity);
      }
      else if (isMainWizard(ewconfig, config) && elementtype != null && !"".equals(elementtype)
            && !Workflow.isWorkflowType(elementtype)) {
         if(cloud.getUser().getRank() != Rank.ADMIN) {
            params.put("WORKFLOW", OFF);
         }
      }
      else {
         if (elementtype != null && !"".equals(elementtype) && Workflow.isWorkflowType(elementtype)) {
            params.put("WORKFLOW", FALSE);
         }
         else {
            params.put("WORKFLOW", OFF);
         }
      }
   }


   @Override
   public void closeWizard(HttpServletRequest request, Config ewconfig, Config.WizardConfig wizardConfig, Cloud cloud,
         Node editNode, String elementtype) {

      if (isMainWizard(ewconfig, wizardConfig) && editNode != null && Workflow.isWorkflowType(elementtype)) {

         String objectnr = wizardConfig.objectNumber;

         String workflowCommand = request.getParameter(WORKFLOWCOMMAND);
         String workflowcomment = request.getParameter(WORKFLOWCOMMENT);

         if (wizardConfig.wiz.committed()) {
            if (NEW_OBJECT.equals(objectnr)) {
               if (wizardConfig.wiz.committed()) {
                  if (!Workflow.hasWorkflow(editNode)) {
                     Workflow.create(editNode, workflowcomment);
                  }
                  else {
                     Workflow.addUserToWorkflow(editNode);
                  }
               }
            }
            else {
               if (!CANCEL.equals(workflowCommand)) {
                  if (!Workflow.hasWorkflow(editNode)) {
                     log.debug("object " + objectnr + " missing workflow. creating one. ");
                     Workflow.create(editNode, "");
                  }
                  else {
                     Workflow.addUserToWorkflow(editNode);
                  }
               }
            }

            // wizard command is commit
            if (FINISH.equals(workflowCommand)) {
               log.debug("finishing object " + objectnr);
               Workflow.finish(editNode, workflowcomment);
            }
            if (ACCEPT.equals(workflowCommand)) {
               log.debug("accepting object " + objectnr);
               Workflow.accept(editNode, workflowcomment);
            }
            if (PUBLISH.equals(workflowCommand)) {
               log.debug("publishing object " + objectnr);
               try {
                  Workflow.publish(editNode);
               }
               catch (WorkflowException wfe) {
                  List<Node> errors = wfe.getErrors();
                  request.setAttribute(ERRORS, errors);
                  log.error("Could not publish object");
                  for (Node errorNode : errors) {
                     log.error(errorNode.getNodeManager().getName() + " " + errorNode.getNumber() + " ");
                  }

                  if (Workflow.isAcceptedStepEnabled()) {
                     Workflow.accept(editNode, workflowcomment);
                  }
                  else {
                     Workflow.finish(editNode, workflowcomment);
                  }
               }
            }
         }
         else {

            // wizard command is cancel. This command cannot be called on a new
            // node
            // so there is always a workflow
            if (REJECT.equals(workflowCommand)) {
               log.debug("rejecting object " + objectnr);
               Workflow.reject(editNode, workflowcomment);
            }
         }
      }
      else {
         if (editNode != null && !Workflow.isWorkflowType(elementtype)) {

            String workflowCommand = request.getParameter(WORKFLOWCOMMAND);
            if(isMainWizard(ewconfig, wizardConfig) && cloud.getUser().getRank() == Rank.ADMIN) {
               if (PUBLISH.equals(workflowCommand)) {
                  // update only nodes in live clouds.
                  // PublishUtil.PublishOrUpdateNode(editNode);
                  Publish.publish(editNode); 
               }
            }

            if (!CANCEL.equals(workflowCommand)) {
               if (RepositoryUtil.isContentChannel(editNode) || RepositoryUtil.isCollectionChannel(editNode)) {
                  if (!Workflow.hasWorkflow(editNode)) {
                     Publish.publish(editNode);
                  }
               }
            }
         }
      }
   }
}
