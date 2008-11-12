package com.finalist.newsletter.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.struts.MMBaseFormlessAction;

import com.finalist.newsletter.util.NewsletterUtil;

public class NewsletterPublish extends MMBaseFormlessAction{

   @Override
   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {
      int number = Integer.parseInt(getParameter(request, "number", true));
      Node newsletterNode = NewsletterUtil.getNewsletterById(number);
      request.setAttribute("isPublish", true);
      Publish.publish(newsletterNode);
      return mapping.findForward("success");
   }
}
