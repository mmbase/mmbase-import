package com.finalist.newsletter.services;

import com.finalist.newsletter.domain.StatisticResult;
import com.finalist.newsletter.services.ServiceException;
import com.finalist.newsletter.services.mock.FakeNewsletterCAO;
import com.finalist.newsletter.services.mock.FakeNewsLetterStatisticCAO;
import com.finalist.newsletter.services.impl.StatisticServiceImpl;

import junit.framework.TestCase;

public class StatisticServiceTest extends TestCase{
	
	StatisticServiceImpl sService;
	
	public void setUp(){
		sService = new StatisticServiceImpl();
		sService.setStatisticcao(new FakeNewsLetterStatisticCAO());
		sService.setNewLettercao(new FakeNewsletterCAO());
	}
	
	public void testStatisticSummeryPeriod(){
		
		StatisticResult result = null;
		try {
			result = sService.statisticSummeryPeriod("2004-04-04", "2004-04-08");
		} catch (ServiceException e) {
			e.printStackTrace();
			fail("Excpetion raised");
		}
		assertEquals(3,result.getPost());
		assertEquals("newsletter.summary", result.getName());
	}
	
//	public List<StatisticResult> statisticAll();
//	public List<StatisticResult> statisticByNewsletter(int newsletterId);
//	public List<StatisticResult> statisticAllByPeriod(String start,String end);
//	public StatisticResult statisticByNewsletterPeriod(int newsletterId,String start,String end);
//	public StatisticResult statisticSummery();
//	public StatisticResult statisticSummeryPeriod(String start,String end); 
}
