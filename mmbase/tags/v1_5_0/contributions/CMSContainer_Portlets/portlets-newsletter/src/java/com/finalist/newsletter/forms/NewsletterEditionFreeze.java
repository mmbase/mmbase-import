package com.finalist.newsletter.forms;

import javax.servlet.http.HttpServletRequest;
import org.mmbase.bridge.Node;
import com.finalist.newsletter.domain.EditionStatus;
import com.finalist.newsletter.util.NewsletterPublicationUtil;

public class NewsletterEditionFreeze extends NewsletterEditionAction{

   @Override
   protected void doSave(HttpServletRequest request, Node edition) throws Exception {
      if(!EditionStatus.FROZEN.value().equals(edition.getValue("process_status"))) {
         NewsletterPublicationUtil.freezeEdition(edition);
      }
   }

   @Override
   protected String getAction() {
      return "freeze";
   }
}
