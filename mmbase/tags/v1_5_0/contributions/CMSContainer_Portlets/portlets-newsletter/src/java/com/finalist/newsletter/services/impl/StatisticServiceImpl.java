package com.finalist.newsletter.services.impl;


import java.util.Date;
import java.util.List;

import com.finalist.cmsc.util.DateUtil;
import com.finalist.newsletter.cao.NewsLetterStatisticCAO;
import com.finalist.newsletter.cao.NewsletterCAO;
import com.finalist.newsletter.domain.StatisticResult;
import com.finalist.newsletter.domain.StatisticResult.HANDLE;
import com.finalist.newsletter.services.CommunityModuleAdapter;
import com.finalist.newsletter.services.ServiceException;
import com.finalist.newsletter.services.StatisticService;

public class StatisticServiceImpl implements StatisticService {

   NewsLetterStatisticCAO statisticCAO;

   NewsletterCAO newsletterCAO;

   public void setStatisticCAO(NewsLetterStatisticCAO statisticCAO) {

      this.statisticCAO = statisticCAO;
   }

   public void setNewsletterCAO(NewsletterCAO newsletterCAO) {

      this.newsletterCAO = newsletterCAO;
   }

   public List<StatisticResult> statisticAll() {

      List<StatisticResult> list = statisticCAO.getAllRecords();
      return list;
   }

   public List<StatisticResult> statisticAllByPeriod(String start, String end)
         throws ServiceException {

      Date startDate;
      Date endDate;
      startDate = DateUtil.parser(start);
      endDate = DateUtil.parser(end);
      List<StatisticResult> list = statisticCAO.getAllRecordsByPeriod(
            startDate, endDate);
      return list;
   }

   public List<StatisticResult> statisticByNewsletter(int newsletterId) {

      List<StatisticResult> list = statisticCAO.getRecordsByNewsletter(newsletterId);
      return list;
   }

   public StatisticResult statisticByNewsletterPeriod(int newsletterId,
         String start, String end) throws ServiceException {

      Date startDate;
      Date endDate;
      startDate = DateUtil.parser(start);
      endDate = DateUtil.parser(end);
      List<StatisticResult> list = statisticCAO.getRecordsByNewsletterAndPeriod(startDate, endDate,newsletterId);
      StatisticResult result = new StatisticResult();
      for (StatisticResult r : list) {
         result.setPost(result.getPost() + r.getPost());
         result.setSubscribe(result.getSubscribe() + r.getSubscribe());
         result.setUnsubscribe(result.getUnsubscribe() + r.getUnsubscribe());
         result.setRemoved(result.getRemoved() + r.getRemoved());
         result.setBounches(result.getBounches() + r.getBounches());
      }
      result.setName("newsletter.summary.bydate");
      return result;
   }

   public StatisticResult statisticSummery() {

      List<StatisticResult> list = statisticCAO.getAllRecords();
      StatisticResult result = new StatisticResult();
      for (StatisticResult r : list) {
         result.setPost(result.getPost() + r.getPost());
         result.setSubscribe(result.getSubscribe() + r.getSubscribe());
         result.setUnsubscribe(result.getUnsubscribe() + r.getUnsubscribe());
         result.setRemoved(result.getRemoved() + r.getRemoved());
         result.setBounches(result.getBounches() + r.getBounches());
      }
      result.setName("newsletter.summary.all");
      return result;
   }

   public StatisticResult statisticSummeryPeriod(String start, String end)
         throws ServiceException {

      Date startDate;
      Date endDate;
      startDate = DateUtil.parser(start);
      endDate = DateUtil.parser(end);
      List<StatisticResult> list = statisticCAO.getAllRecordsByPeriod(
            startDate, endDate);
      StatisticResult result = new StatisticResult();
      for (StatisticResult r : list) {
         result.setPost(result.getPost() + r.getPost());
         result.setSubscribe(result.getSubscribe() + r.getSubscribe());
         result.setUnsubscribe(result.getUnsubscribe() + r.getUnsubscribe());
         result.setRemoved(result.getRemoved() + r.getRemoved());
         result.setBounches(result.getBounches() + r.getBounches());
      }
      result.setName("newsletter.summary.all.bydate");
      return result;
   }

   public List<StatisticResult> StatisticDetailByNewsletterPeriod(
         int newsletterId, String start, String end) throws ServiceException {

      Date startDate;
      Date endDate;
      startDate = DateUtil.parser(start);
      endDate = DateUtil.parser(end);
      List<StatisticResult> list = statisticCAO.getRecordsByNewsletterAndPeriod(startDate, endDate,newsletterId);
      return list;
   }

   public StatisticResult StatisticSummaryByNewsletter(int newsletterId) {
      List<StatisticResult> list = statisticCAO.getRecordsByNewsletter(newsletterId);
      StatisticResult result = new StatisticResult();
      for (StatisticResult r : list) {
         result.setPost(result.getPost() + r.getPost());
         result.setSubscribe(result.getSubscribe() + r.getSubscribe());
         result.setUnsubscribe(result.getUnsubscribe() + r.getUnsubscribe());
         result.setRemoved(result.getRemoved() + r.getRemoved());
         result.setBounches(result.getBounches() + r.getBounches());
      }
      result.setName("newsletter.summary");
      return result;
   }

   public void logPubliction(int newsletterId, HANDLE handle) {
      int userId = CommunityModuleAdapter.getCurrentUserId();
      statisticCAO.logPubliction(userId, newsletterId, handle);
   }

   public List<StatisticResult> getLogs() {
      
      return statisticCAO.getLogs();
   }

   public int pushSumedLogs(List<StatisticResult> listRecorder) {
      
      return statisticCAO.insertSumedLogs(listRecorder);
   }
}
