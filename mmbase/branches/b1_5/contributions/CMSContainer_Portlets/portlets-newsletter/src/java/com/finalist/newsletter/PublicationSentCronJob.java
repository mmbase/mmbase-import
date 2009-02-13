package com.finalist.newsletter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.applications.crontab.AbstractCronJob;

import com.finalist.cmsc.services.community.ApplicationContextFactory;
import com.finalist.cmsc.util.ServerUtil;
import com.finalist.newsletter.services.NewsletterPublicationService;

public class PublicationSentCronJob extends AbstractCronJob {
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
      if(ServerUtil.isSingle() || ServerUtil.isStaging()) {
         log.debug("Delivering all publications.");
         NewsletterPublicationService service = (NewsletterPublicationService) ApplicationContextFactory.getBean("publicationService");
         service.deliverAllPublications();
      }
      
   }
}