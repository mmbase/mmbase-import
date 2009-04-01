package com.finalist.newsletter.forms;



import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.services.workflow.Workflow;
import com.finalist.cmsc.services.workflow.WorkflowException;
import com.finalist.cmsc.struts.MMBaseFormlessAction;
import com.finalist.cmsc.util.HttpUtil;
import com.finalist.newsletter.domain.EditionStatus;
import com.finalist.newsletter.util.NewsletterPublicationUtil;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class NewsletterEditionFreezeAjax extends MMBaseFormlessAction{

   
   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {
      int number = Integer.parseInt(getParameter(request, "number", true));
      PrintWriter out = HttpUtil.getWriterForXml(response);
      Node edition = cloud.getNode(number);
      if (!EditionStatus.FROZEN.value().equals(edition.getValue("process_status"))) {
         if (Publish.isPublished(edition)) {
            NewsletterPublicationUtil.freezeEdition(edition);
            out.print("0");
         } else {
            out.print("1");
         }
      }
      return null;
   }

   @Override
   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {
     
      return null;
   }
  
}
