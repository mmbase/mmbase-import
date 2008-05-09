package com.finalist.newsletter.cao;

import java.util.Date;
import java.util.List;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import static com.finalist.newsletter.domain.Subscription.STATUS;

import com.finalist.newsletter.domain.StatisticResult;

public interface NewsLetterStatisticCAO {
	public List<StatisticResult> getAllRecords();

	public List<StatisticResult> getRecordsByNewsletter(int newsletter);

	public List<StatisticResult> getAllRecordsByPeriod(Date start, Date end);

	public List<StatisticResult> getRecordsByNewsletterAndPeriod(Date start,
			Date end, int newsletter);
	public void logPubliction(int userId,int id, STATUS status);
}
