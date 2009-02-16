package com.finalist.newsletter.forms;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;

import com.finalist.cmsc.paging.PagingUtils;
import com.finalist.cmsc.services.community.ApplicationContextFactory;
import com.finalist.cmsc.struts.MMBaseFormlessAction;
import com.finalist.newsletter.domain.Term;
import com.finalist.newsletter.services.NewsletterService;

public class NewsletterTermSearchAction extends MMBaseFormlessAction {

   /**
    * @param mapping
    * @param request
    * @param cloud
    * @return ActionForward refreshing newsletter term list
    * @throws Exception
    */
   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

      PagingUtils.initStatusHolder(request);

      int newsletterId = Integer.parseInt(request.getParameter("newsletterId"));
      String tmpName = request.getParameter("name");
      NewsletterService newsletterService = (NewsletterService) ApplicationContextFactory.getBean("newsletterServices");

      int resultCount = newsletterService.getNewsletterTermsByName(newsletterId, tmpName, false).size();
      List<Term> terms = newsletterService.getNewsletterTermsByName(newsletterId, tmpName, true);

      if (terms != null) {
         request.setAttribute("results", terms);
      }
      request.setAttribute("resultCount", resultCount);
      request.setAttribute("newsletterId", newsletterId);
      return mapping.findForward("success");
   }

}
