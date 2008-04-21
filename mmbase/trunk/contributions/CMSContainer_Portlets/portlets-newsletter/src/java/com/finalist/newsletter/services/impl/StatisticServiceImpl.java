package com.finalist.newsletter.services.impl;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mmbase.bridge.Node;

import com.finalist.newsletter.cao.NewsLetterStatisticCAO;
import com.finalist.newsletter.cao.NewsletterCAO;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.StatisticResult;
import com.finalist.newsletter.services.ServiceException;
import com.finalist.newsletter.services.StatisticService;
import com.finalist.newsletter.util.DateUtil;

public class StatisticServiceImpl implements StatisticService {

	NewsLetterStatisticCAO statisticcao;

	NewsletterCAO newLettercao;

	public void setStatisticcao (NewsLetterStatisticCAO statisticcao){

		this.statisticcao = statisticcao;
	}

	public void setNewLettercao (NewsletterCAO newLettercao){

		this.newLettercao = newLettercao;
	}

	public List<StatisticResult> statisticAll (){

		List<StatisticResult> list = statisticcao.getAllRecords();

		return list;
	}

	public List<StatisticResult> statisticAllByPeriod (String start, String end)
			throws ServiceException{

		Date startDate;
		Date endDate;
		try {
			startDate = DateUtil.parser(start);
			endDate = DateUtil.parser(end);
		} catch (ParseException e) {
			throw new ServiceException("Date parser failed", e);
		}
		List<StatisticResult> list = statisticcao.getAllRecordsByPeriod(
				startDate, endDate);
		return list;
	}

	public List<StatisticResult> statisticByNewsletter (int newsletterId){

		List<StatisticResult> list = statisticcao
				.getRecordsByNewsletter(newsletterId);
		return list;
	}

	public StatisticResult statisticByNewsletterPeriod (int newsletterId,
			String start, String end) throws ServiceException{

		Date startDate;
		Date endDate;
		try {
			startDate = DateUtil.parser(start);
			endDate = DateUtil.parser(end);
		} catch (ParseException e) {
			throw new ServiceException("Date parser failed", e);
		}
		List<StatisticResult> list = statisticcao
				.getRecordsByNewsletterAndPeriod(startDate, endDate, newsletterId);
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

	public StatisticResult statisticSummery (){

		List<StatisticResult> list = statisticcao.getAllRecords();
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

	public StatisticResult statisticSummeryPeriod (String start, String end)
			throws ServiceException{

		Date startDate;
		Date endDate;
		try {
			startDate = DateUtil.parser(start);
			endDate = DateUtil.parser(end);
		} catch (ParseException e) {
			throw new ServiceException("Date parser failed", e);
		}
		List<StatisticResult> list = statisticcao.getAllRecordsByPeriod(
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

	public List<StatisticResult> StatisticDetailByNewsletterPeriod (
			int newsletterId, String start, String end) throws ServiceException{

		Date startDate;
		Date endDate;
		try {
			startDate = DateUtil.parser(start);
			endDate = DateUtil.parser(end);
		} catch (ParseException e) {
			throw new ServiceException("Date parser failed", e);
		}
		List<StatisticResult> list = statisticcao
				.getRecordsByNewsletterAndPeriod(startDate, endDate, newsletterId);
		return list;
	}

	public StatisticResult StatisticSummaryByNewsletter (int newsletterId){

		List<StatisticResult> list = statisticcao
				.getRecordsByNewsletter(newsletterId);
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

}
