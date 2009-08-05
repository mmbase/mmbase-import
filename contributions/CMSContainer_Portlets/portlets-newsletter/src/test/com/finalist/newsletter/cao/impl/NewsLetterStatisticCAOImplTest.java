package com.finalist.newsletter.cao.impl;

import com.finalist.newsletter.BaseNewsletterTest;
import com.finalist.newsletter.cao.NewsLetterStatisticCAO;
import com.finalist.newsletter.domain.StatisticResult;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class NewsLetterStatisticCAOImplTest extends BaseNewsletterTest {

   NewsLetterStatisticCAO cao;

   protected void setUp() throws Exception {
      super.setUp();
      clearAllNode("newsletterdailylog");
   }

   public void testLogPubliction() {

      List list = cao.getAllRecords();

      assertEquals(2,list.size());
   }
   public void testGetAllRecords() {
		List<StatisticResult> records = cao.getAllRecords();
		assertEquals(8, records.size());
	}

	public void testGetRecordsByNewsletter() {
		List<StatisticResult> records = cao.getRecordsByNewsletter(1);
		assertEquals(6,records.get(0).getPost());
		assertEquals(2, records.size());
	}

	public void testGetAllRecordsByPeriod() {
		/*
		 * Long dateS1 = Date.parse("2008-3-31"); Date date1 = new Date(dateS1);
		 * Date date2 = new Date(Date.parse("2008-3-31"));
		 */

		try {
			DateFormat df = DateFormat.getDateInstance();
			List<StatisticResult> records = cao.getAllRecordsByPeriod(df
					.parse("2008-01-01"), df.parse("2008-01-03"));
			assertEquals(5, records.size());
		} catch (ParseException e) {
			fail("parser error");
			e.printStackTrace();
		}
	}

	public void testGetRecordsByNewsletterAndPeriod() {
		DateFormat df = DateFormat.getDateInstance();

		try {
			Date start = df.parse("2003-01-01");
			Date end = df.parse("2009-01-31");
			List<StatisticResult> records = cao.getRecordsByNewsletterAndPeriod(
					start, end, 2);
			assertEquals(2,records.get(0).getPost());
			assertEquals(2, records.size());

			Date end2 = df.parse("2007-01-08");
			List<StatisticResult> zeroRecords = cao
					.getRecordsByNewsletterAndPeriod(start, end2, 1);
			assertEquals(0, zeroRecords.size());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
