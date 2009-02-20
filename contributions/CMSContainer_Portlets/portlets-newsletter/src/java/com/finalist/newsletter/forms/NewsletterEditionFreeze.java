package com.finalist.newsletter.forms;



import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.bridge.Node;

import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.services.workflow.Workflow;
import com.finalist.cmsc.services.workflow.WorkflowException;
import com.finalist.newsletter.domain.EditionStatus;
import com.finalist.newsletter.util.NewsletterPublicationUtil;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class NewsletterEditionFreeze extends NewsletterEditionAction{
   private static final String ERRORS = "errors";
   private static final String NEEDAJAX = "needajax";
   private static final Logger log = Logging.getLoggerInstance(NewsletterEditionFreeze.class.getName());
   @Override
   protected void doSave(HttpServletRequest request, Node edition) throws Exception {
      if (!EditionStatus.FROZEN.value().equals(edition.getValue("process_status"))) {
         if (Publish.isPublished(edition)) {
            NewsletterPublicationUtil.freezeEdition(edition);
         } else {
            try {
               Workflow.publish(edition);
            }
            catch (WorkflowException wfe) {
               List<Node> errors = wfe.getErrors();
               request.getSession().setAttribute(ERRORS, edition.getValue("title"));
               for (Node errorNode : errors) {
                  log.error(errorNode.getNodeManager().getName() + " " + errorNode.getNumber() + " ");
               }        
               if (Workflow.isAcceptedStepEnabled()) {
                  Workflow.accept(edition, "");
               }
               else {
                  Workflow.finish(edition, "");
               }
            }
            request.getSession().setAttribute(NEEDAJAX, edition.getNumber());
         }
      } 
   }

   @Override
   protected String getAction() {
      return "freeze";
   }
}
