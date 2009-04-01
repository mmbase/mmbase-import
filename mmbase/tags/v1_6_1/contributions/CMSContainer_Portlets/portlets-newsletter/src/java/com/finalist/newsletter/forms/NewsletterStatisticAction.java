package com.finalist.newsletter.forms;

import com.finalist.cmsc.paging.PagingStatusHolder;
import com.finalist.cmsc.paging.PagingUtils;
import com.finalist.cmsc.services.community.ApplicationContextFactory;
import com.finalist.cmsc.util.DateUtil;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.StatisticResult;
import com.finalist.newsletter.services.*;


import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.*;
import org.apache.struts.actions.MappingDispatchAction;

/**
 * Show all newsletters when first come.
 * Search some newsletters according to these search conditions.
 *
 * @author Eva
 */
public class NewsletterStatisticAction extends MappingDispatchAction {

   public NewsletterStatisticAction() {

   }

   public ActionForward show(ActionMapping mapping, ActionForm form,
                             HttpServletRequest request, HttpServletResponse response)
            throws Exception {
      PagingUtils.initStatusHolder(request);
      NewsletterService newsletterService = (NewsletterService) ApplicationContextFactory.getBean("newsletterServices");
      List<Newsletter> newsletters = newsletterService.getAllNewsletter(false);

      addBlankNewsletter(newsletters);

      request.setAttribute("newsletters", newsletters);
      StatisticService service = (StatisticService) ApplicationContextFactory.getBean("statisticService");
      StatisticResult result = new StatisticResult();
      result = service.statisticSummery();
      request.setAttribute("result", result);
      return mapping.findForward("result");
   }

   public ActionForward search(ActionMapping mapping, ActionForm form,
                               HttpServletRequest request, HttpServletResponse response)
            throws Exception {

      PagingUtils.initStatusHolder(request);
      PagingStatusHolder holder = PagingUtils.getStatusHolder();
      
      NewsletterService newsletterService = (NewsletterService) ApplicationContextFactory.getBean("newsletterServices");
      StatisticService service = (StatisticService) ApplicationContextFactory.getBean("statisticService");
      List<Newsletter> newsletters = newsletterService.getAllNewsletter(false);
      addBlankNewsletter(newsletters);
      request.setAttribute("newsletters", newsletters);
      NewsLetterLogSearchForm searchForm = (NewsLetterLogSearchForm) form;
      StatisticResult result = new StatisticResult();
      request.setAttribute("searchForm", searchForm);

      boolean isAll = Integer.parseInt(searchForm.getNewsletters()) == 0;
      boolean isDetail = searchForm.getDetailOrSum().equals("2");
      boolean hasDate = searchForm.getEndDate() != ""
               && searchForm.getStartDate() != "";
      String startDate = searchForm.getStartDate();
      String endDate = searchForm.getEndDate();
      int newsletterId = Integer.parseInt(searchForm.getNewsletters());

      if (isAll && hasDate && isDetail) {
         List<StatisticResult> records = service.statisticAllByPeriod(
                  startDate, endDate);
         transferShowingFromDB(records, newsletterService);
         request.setAttribute("records", addPagingCondition(request, records, holder));
      } else if (isAll && !hasDate && isDetail) {
         List<StatisticResult> records = service.statisticAll();
         transferShowingFromDB(records, newsletterService);
         request.setAttribute("records", addPagingCondition(request, records, holder));
      } else if (!isAll && !hasDate && isDetail) {
         List<StatisticResult> records = service
                  .statisticByNewsletter(newsletterId);
         transferShowingFromDB(records, newsletterService);
         request.setAttribute("records", addPagingCondition(request, records, holder));
      } else if (!isAll && hasDate && !isDetail) {
         result = service.statisticByNewsletterPeriod(newsletterId,
                  startDate, endDate);
         request.setAttribute("newsletterName", transToNewsName(request, newsletterService, newsletterId));
         request.setAttribute("result", result);
      } else if (!isAll && !hasDate && !isDetail) {
         result = service.statisticSummaryByNewsletter(newsletterId);
         request.setAttribute("result", result);
         request.setAttribute("newsletterName", transToNewsName(request, newsletterService, newsletterId));
      } else if (isAll && !hasDate && !isDetail) {
         result = service.statisticSummery();
         request.setAttribute("result", result);
      } else if (isAll && hasDate && !isDetail) {
         result = service.statisticSummeryPeriod(startDate, endDate);
         request.setAttribute("result", result);
      } else if (!isAll && hasDate && isDetail) {
         List<StatisticResult> records = service
                  .statisticDetailByNewsletterPeriod(newsletterId, startDate,
                           endDate);
         transferShowingFromDB(records, newsletterService);
         request.setAttribute("records", addPagingCondition(request, records, holder));
      }
      return mapping.findForward("result");
   }

   private String transToNewsName(HttpServletRequest request,
                                  NewsletterService newsletterService, int newsletterId) {
      return newsletterService.getNewsletterName(newsletterId);

   }

   private void addBlankNewsletter(List<Newsletter> newsletters) {

      Newsletter newsletter = new Newsletter();
      newsletter.setTitle("ALL");
      newsletter.setId(0);
      newsletters.add(0, newsletter);
   }

   private void transferShowingFromDB(List<StatisticResult> records,
                                      NewsletterService newsletterService) {

      for (StatisticResult s : records) {
         s.setShowingdate(DateUtil.parser(s.getLogdate()));
         s.setName(newsletterService.getNewsletterName(s.getNewsletterId()));
      }

   }

   private List<StatisticResult> addPagingCondition(HttpServletRequest request, List<StatisticResult> records, PagingStatusHolder holder) {

      List<StatisticResult> recordsForShow = new ArrayList<StatisticResult>();
      int totalCount = records.size();
      request.setAttribute("totalCount", totalCount);
      for (int i = holder.getOffset(); i < holder.getPageSize() + holder.getOffset() && i < totalCount; i++) {
         recordsForShow.add(records.get(i));
      }
      return recordsForShow;
   }
}