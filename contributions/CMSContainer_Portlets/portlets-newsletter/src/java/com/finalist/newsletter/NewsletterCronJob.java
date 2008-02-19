package com.finalist.newsletter;

import java.util.ArrayList;
import java.util.List;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.applications.crontab.CronEntry;
import org.mmbase.applications.crontab.CronJob;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.services.publish.Publish;
import com.finalist.newsletter.util.NewsletterPublicationUtil;
import com.finalist.newsletter.util.NewsletterUtil;

public class NewsletterCronJob implements CronJob {

   private static Logger log = Logging.getLoggerInstance(NewsletterCronJob.class.getName());

   private List<Node> getNewslettersToPublish() {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      NodeManager manager = cloud.getNodeManager(NewsletterUtil.NEWSLETTER);
      NodeQuery query = manager.createQuery();
      NodeList newsletters = manager.getList(query);
      List<Node> newslettersToPublish = new ArrayList<Node>();
      for (int i = 0; i < newsletters.size(); i++) {
         Node newsletter = newsletters.getNode(i);
         if (Publish.isPublished(newsletter)) {
            long publishInterval = newsletter.getLongValue("publishinterval");
            if (publishInterval > 0) {
               int newsletterNumber = newsletter.getNumber();
               boolean isPaused = NewsletterUtil.isPaused(newsletterNumber);
               if (isPaused == false) {
                  newslettersToPublish.add(newsletter);
               }
            }
         }
      }
      return (newslettersToPublish);
   }

   public void init(CronEntry arg0) {
      log.info("Initializing Newsletter CronJob");

   }

   public void run() {
      List<Node> newslettersToPublish = getNewslettersToPublish();
      for (int newsletterIterator = 0; newsletterIterator < newslettersToPublish.size(); newsletterIterator++) {
         Node newsletterNode = newslettersToPublish.get(newsletterIterator);
         int newsletterNumber = newsletterNode.getNumber();
         log.info("Running Newsletter CronJob for newsletter " + newsletterNumber);
         Node publicationNode = NewsletterPublicationUtil.createPublication(newsletterNumber, true);
         Publish.publish(publicationNode);
      }
   }

   public void stop() {
      log.info("Stopping Newsletter CronJob");
   }
}