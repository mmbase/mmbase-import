package com.finalist.newsletter.forms;

import javax.servlet.http.HttpServletRequest;
import org.mmbase.bridge.Node;
import com.finalist.newsletter.domain.EditionStatus;
import com.finalist.newsletter.util.NewsletterPublicationUtil;

public class NewsletterEditionDefrost extends NewsletterEditionAction{

   @Override
   protected void doSave(HttpServletRequest request, Node edition)
         throws Exception {
      if(!EditionStatus.INITIAL.value().equals(edition.getValue("process_status"))) {
         NewsletterPublicationUtil.defrostEdition(edition);
      }
   }

   @Override
   protected String getAction() {
      return "defrost";
   }

  
}
