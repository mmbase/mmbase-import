package com.finalist.newsletter.services.impl;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.finalist.newsletter.cao.NewsLetterStatisticCAO;
import com.finalist.newsletter.cao.NewsletterCAO;
import com.finalist.newsletter.domain.StatisticResult;
import com.finalist.newsletter.services.ServiceException;
import com.finalist.newsletter.services.NewsletterStatisticService;
import com.finalist.newsletter.util.DateUtil;

public class NewsletterStatisticServiceImpl implements NewsletterStatisticService {
   NewsLetterStatisticCAO statisticcao;
   NewsletterCAO newLettercao;


   public void setStatisticcao(NewsLetterStatisticCAO statisticcao) {
      this.statisticcao = statisticcao;
   }

   public void setNewLettercao(NewsletterCAO newLettercao) {
      this.newLettercao = newLettercao;
   }

   public List<StatisticResult> statisticAll() {
      // TODO Auto-generated method stub
      return null;
   }

   public List<StatisticResult> statisticAllByPeriod(String start, String end) {
      // TODO Auto-generated method stub
      return null;
   }

   public List<StatisticResult> statisticByNewsletter(int newsletterId) {
      // TODO Auto-generated method stub
      return null;
   }

   public StatisticResult statisticByNewsletterPeriod(int newsletterId, String start, String end) {
      // TODO Auto-generated method stub
      return null;
   }

   public StatisticResult statisticSummery() {
      // TODO Auto-generated method stub
      return null;
   }

   public StatisticResult statisticSummeryPeriod(String start, String end) throws ServiceException {

      Date startDate;
      Date endDate;
      try {
         startDate = DateUtil.parser(start);
         endDate = DateUtil.parser(end);
      } catch (ParseException e) {
         throw new ServiceException("Date parser failed", e);
      }

//		List<StatisticResult> list = statisticcao.getAllRecordsByPeriod(startDate, endDate);
//		StatisticResult result = new StatisticResult();
//
//		for(StatisticResult r : list){
//			result.setPost(result.getPost()+r.getPost());
//		}
//
//		result.setName("newsletter.summary");
      return null;
   }


}
