package com.finalist.newsletter.forms;

import javax.servlet.http.HttpServletRequest;
import org.mmbase.bridge.Node;
import com.finalist.newsletter.domain.EditionStatus;
import com.finalist.newsletter.util.NewsletterPublicationUtil;

public class NewsletterEditionApprove extends NewsletterEditionAction{

   @Override
   protected void doSave(HttpServletRequest request, Node edition)
         throws Exception {
      if(!EditionStatus.APPROVED.value().equals(edition.getValue("process_status"))) {
         String appUser=request.getSession().getValue("cloud_mmbase").toString();
         String[] result = appUser.split("\\s");
         NewsletterPublicationUtil.approveEdition(edition,result[3]);
      }
   }

   @Override
   protected String getAction() {
      return "approve";
   }
  
}
