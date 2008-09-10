package com.finalist.newsletter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.applications.crontab.AbstractCronJob;
import org.mmbase.applications.crontab.CronJob;

import com.finalist.cmsc.services.community.ApplicationContextFactory;
import com.finalist.newsletter.services.NewsletterPublicationService;

public class PublicationSentCronJob extends AbstractCronJob implements CronJob {
   private static Log log = LogFactory.getLog(PublicationSentCronJob.class);


   @Override
   public void init() {
      log.info("Publication sent cronjob starting");
   }

   @Override
   public void stop() {
      log.info("Publication sent cronjob stop");
   }

   @Override
   public void run() {
      log.debug("Delivering all publications.");
      NewsletterPublicationService service = (NewsletterPublicationService) ApplicationContextFactory.getBean("publicationService");
      service.deliverAllPublication();
   }
}