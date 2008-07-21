package com.finalist.newsletter;

import com.finalist.cmsc.services.community.ApplicationContextFactory;
import com.finalist.newsletter.services.NewsletterPublicationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.applications.crontab.CronEntry;
import org.mmbase.applications.crontab.CronJob;

public class PublicationSentCronJob implements CronJob {
   private static Log log = LogFactory.getLog(PublicationSentCronJob.class);


   public void init(CronEntry cronEntry) {
      log.info("Publication sent cronjob starting");
   }

   public void stop() {
      log.info("Publication sent cronjob stop");
   }

   public void run() {
      log.debug("Delivering all publications.");
      NewsletterPublicationService service = (NewsletterPublicationService) ApplicationContextFactory.getBean("publicationService");
      service.deliverAllPublication();
   }
}