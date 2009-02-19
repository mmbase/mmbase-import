package com.finalist.newsletter.forms;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.bridge.Node;

import com.finalist.cmsc.services.publish.Publish;
import com.finalist.newsletter.domain.EditionStatus;
import com.finalist.newsletter.util.NewsletterPublicationUtil;

public class NewsletterEditionFreeze extends NewsletterEditionAction{
   private static final String ERRORS = "errors";
   @Override
   protected void doSave(HttpServletRequest request, Node edition) throws Exception {
      if (!EditionStatus.FROZEN.value().equals(edition.getValue("process_status")) && Publish.isPublished(edition)) {
         NewsletterPublicationUtil.freezeEdition(edition);
      } else {
         request.getSession().setAttribute(ERRORS, edition.getValue("title"));
      }
   }

   @Override
   protected String getAction() {
      return "freeze";
   }
}
