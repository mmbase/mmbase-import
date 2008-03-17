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
import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.services.workflow.Workflow;
import com.finalist.cmsc.services.workflow.WorkflowException;
import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.navigation.PagesUtil;

public class WizardWorkflowController extends WizardController {

   /**
    * MMbase logging system
    */
   private static Logger log = Logging.getLoggerInstance(WizardWorkflowController.class.getName());


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
         Map<String, String> params, UserRole userrole, String contenttype) {

      String objectnr = config.objectNumber;

      if (isMainWizard(ewconfig, config) && contenttype != null && !"".equals(contenttype)
            && Workflow.isWorkflowType(contenttype)) {

         params.put("WORKFLOW", "true");
         params.put("WORKFLOW-ACCEPTED-ENABLED", Boolean.toString(Workflow.isAcceptedStepEnabled()));

         String activity = "DRAFT";

         if (!"new".equals(objectnr)) {
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
               params.put("READONLY", "true");
               if (Workflow.STATUS_PUBLISHED.equals(Workflow.getStatus(node))) {
                  params.put("READONLY-REASON", "PUBLISH");
               }
               else {
                  params.put("READONLY-REASON", "WORKFLOW");
               }
            }
         }
         else {
            if (PagesUtil.isPageType(cloud.getNodeManager(contenttype))) {
               // disable workflow for a new page
               params.put("WORKFLOW", "off");
            }
         }
         log.debug("activity " + activity);
         params.put("ACTIVITY", activity);
      }
      else {
         if (contenttype != null && !"".equals(contenttype) && Workflow.isWorkflowType(contenttype)) {
            params.put("WORKFLOW", "false");
         }
         else {
            params.put("WORKFLOW", "off");
         }
      }
   }


   @Override
   public void closeWizard(HttpServletRequest request, Config ewconfig, Config.WizardConfig wizardConfig, Cloud cloud,
         Node editNode, String contenttype) {

      if (isMainWizard(ewconfig, wizardConfig) && editNode != null && Workflow.isWorkflowType(contenttype)) {

         String objectnr = wizardConfig.objectNumber;

         String workflowCommand = request.getParameter("workflowcommand");
         String workflowcomment = request.getParameter("workflowcomment");

         if (wizardConfig.wiz.committed()) {
            if ("new".equals(objectnr)) {
               if (wizardConfig.wiz.committed() && !Workflow.hasWorkflow(editNode)) {
                  Workflow.create(editNode, workflowcomment);
               }
            }
            else {
               if (!Workflow.hasWorkflow(editNode) && !"cancel".equals(workflowCommand)) {
                  log.debug("object " + objectnr + " missing workflow. creating one. ");
                  Workflow.create(editNode, "");
               }
            }

            // wizard command is commit
            if ("finish".equals(workflowCommand)) {
               log.debug("finishing object " + objectnr);
               Workflow.finish(editNode, workflowcomment);
            }
            if ("accept".equals(workflowCommand)) {
               log.debug("accepting object " + objectnr);
               Workflow.accept(editNode, workflowcomment);
            }
            if ("publish".equals(workflowCommand)) {
               log.debug("publishing object " + objectnr);
               try {
                  Workflow.publish(editNode);
               }
               catch (WorkflowException wfe) {
                  List<Node> errors = wfe.getErrors();
                  request.setAttribute("errors", errors);
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
            if ("reject".equals(workflowCommand)) {
               log.debug("rejecting object " + objectnr);
               Workflow.reject(editNode, workflowcomment);
            }
         }
      }
      else {
         if (editNode != null && !Workflow.isWorkflowType(contenttype)) {

            String workflowCommand = request.getParameter("workflowcommand");
            if ("publish".equals(workflowCommand)) {
               // update only nodes in live clouds.
               // PublishUtil.PublishOrUpdateNode(editNode);
            }

            if (!"cancel".equals(workflowCommand)) {
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
