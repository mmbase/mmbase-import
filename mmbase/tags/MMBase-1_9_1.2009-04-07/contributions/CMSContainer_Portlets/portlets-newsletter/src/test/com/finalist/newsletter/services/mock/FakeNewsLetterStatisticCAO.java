package com.finalist.newsletter.services.mock;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mmbase.bridge.NodeList;

import com.finalist.newsletter.cao.NewsLetterStatisticCAO;
import com.finalist.newsletter.domain.StatisticResult;
import com.finalist.newsletter.domain.Subscription;

public class FakeNewsLetterStatisticCAO implements NewsLetterStatisticCAO{

	public List<StatisticResult> getAllRecords() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<StatisticResult> getAllRecordsByPeriod(Date start, Date end) {
		
		List<StatisticResult> list = new ArrayList<StatisticResult>();
		StatisticResult r1 = new StatisticResult();
		r1.setName("r1");
		r1.setPost(1);
		StatisticResult r2 = new StatisticResult();
		r1.setName("r2");
		r2.setPost(1);
		StatisticResult r3 = new StatisticResult();
		r1.setName("r3");
		r3.setPost(1);
		list.add(r1);
		list.add(r2);
		list.add(r3);
		
		return list;
	}

	public List<StatisticResult> getRecordsByNewsletter(int newsletter) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<StatisticResult> getRecordsByNewsletterAndPeriod(Date start, Date end,
			int newsletter) {
		// TODO Auto-generated method stub
		return null;
	}

   public void logPubliction(int userId, int newsletterId, StatisticResult.HANDLE handle) {

   }

   public int insertSumedLogs(List<StatisticResult> listRecorder) {
      return 0;
   }

   public List<StatisticResult> getLogs() {
      return null;
   }

   public void logPubliction(int userId, int id, Subscription.STATUS status) {

   }

   public void logPubliction(int id, int i) {
      //To change body of implemented methods use File | Settings | File Templates.
   }
}
