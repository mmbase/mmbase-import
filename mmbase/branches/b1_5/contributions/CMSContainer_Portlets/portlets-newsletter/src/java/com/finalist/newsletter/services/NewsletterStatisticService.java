package com.finalist.newsletter.services;

import java.util.List;

import com.finalist.newsletter.domain.StatisticResult;

public interface NewsletterStatisticService {
   public List<StatisticResult> statisticAll();

   public List<StatisticResult> statisticByNewsletter(int newsletterId);

   public List<StatisticResult> statisticAllByPeriod(String start, String end);

   public StatisticResult statisticByNewsletterPeriod(int newsletterId, String start, String end);

   public StatisticResult statisticSummery();

   public StatisticResult statisticSummeryPeriod(String start, String end) throws ServiceException;
}
