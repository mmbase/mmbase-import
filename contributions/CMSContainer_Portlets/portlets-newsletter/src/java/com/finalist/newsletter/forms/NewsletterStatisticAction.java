package com.finalist.newsletter.forms;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.actions.MappingDispatchAction;

import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.StatisticResult;

import com.finalist.newsletter.services.NewsletterService;
import com.finalist.newsletter.services.impl.NewsletterStatisticServiceImpl;
import com.finalist.newsletter.services.impl.NewsletterServiceImpl;

public class NewsletterStatisticAction extends MappingDispatchAction {

   public ActionForward show(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

      NewsletterService newsletterService = new NewsletterServiceImpl();

      List<Newsletter> newsletters = newsletterService.getAllNewsletter();

      request.setAttribute("newsletters", newsletters);
      System.out.println("unspecified");


      return mapping.findForward("result");
   }

   public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
      NewsletterService newsletterService = new NewsletterServiceImpl();

      List<Newsletter> newsletters = newsletterService.getAllNewsletter();

      request.setAttribute("newsletters", newsletters);

      System.out.println("#####################");

      NewsletterStatisticServiceImpl service = new NewsletterStatisticServiceImpl();
//		service.setStatisticcao(new NewsletterServiceImpl());
      NewsLetterLogSearchForm searchForm = (NewsLetterLogSearchForm) form;

      StatisticResult result = service.statisticSummeryPeriod(searchForm
            .getStartDate(), searchForm.getEndDate());
      request.setAttribute("result", result);


      ActionErrors errors = new ActionErrors();
      errors.add("error1", new ActionMessage("error1"));
      saveErrors(request, errors);
      request.setAttribute("test", "test");

      return mapping.findForward("result");
   }
}
