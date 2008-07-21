package com.finalist.newsletter;



import java.util.List;
import org.mmbase.applications.crontab.CronEntry;
import org.mmbase.applications.crontab.CronJob;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.services.community.ApplicationContextFactory;
import com.finalist.newsletter.domain.StatisticResult;
import com.finalist.newsletter.services.StatisticService;
public class NewsletterSumLogsCronJob implements CronJob {

	private static Logger log = Logging
			.getLoggerInstance(NewsletterSumLogsCronJob.class.getName());

	public void init(CronEntry cronEntry) {
		log.info("Initializing NewsletterLogRecorder");
	}

	public void run() {
		StatisticService service = (StatisticService) ApplicationContextFactory.getBean("statisticService");
		List<StatisticResult> listRecorder = service.getLogs();
		if (null != listRecorder) {
			insetStatRecord(service, listRecorder);
			listRecorder.clear();
		}
	}

	private int insetStatRecord(StatisticService service,List<StatisticResult> listRecorder) {
		if(null!=listRecorder){
			return service.pushSumedLogs(listRecorder);			
		}
		else{
		    return 0;	
		}
	}
	public void stop() {
		log.info("Stopping NewsletterLogRecorder");

	}
}
