package com.finalist.newsletter.services.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Node;

import com.finalist.newsletter.cao.NewsLetterStatisticCAO;
import com.finalist.newsletter.cao.NewsletterCAO;
import com.finalist.newsletter.cao.NewsletterPublicationCAO;
import com.finalist.newsletter.cao.NewsletterSubscriptionCAO;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.StatisticResult;
import com.finalist.newsletter.domain.Term;
import com.finalist.newsletter.services.NewsletterService;

public class NewsletterServiceImpl implements NewsletterService {

   private static Log log = LogFactory.getLog(NewsletterServiceImpl.class);

   NewsletterCAO newsletterCAO;
   NewsletterSubscriptionCAO subscriptionCAO;
   NewsletterPublicationCAO publicationCAO;
   NewsLetterStatisticCAO statisticCAO;

   public void setNewsletterCAO(NewsletterCAO newsletterCAO) {
      this.newsletterCAO = newsletterCAO;
   }

   public void setSubscriptionCAO(NewsletterSubscriptionCAO subscriptionCAO) {
      this.subscriptionCAO = subscriptionCAO;
   }

   public void setPublicationCAO(NewsletterPublicationCAO publicationCAO) {
      this.publicationCAO = publicationCAO;
   }

   public void setStatisticCAO(NewsLetterStatisticCAO statisticCAO) {
      this.statisticCAO = statisticCAO;
   }

   public List<Newsletter> getAllNewsletter(boolean paging) {
      return newsletterCAO.getNewsletterByConstraint(null, null, null, paging);
   }

   public String getNewsletterName(int newsletterId) {
      String name = "";

      if (newsletterId > 0) {
         name = newsletterCAO.getNewsletterById(newsletterId).getTitle();
      }

      return name;
   }

   public int countAllTerms() {
      return newsletterCAO.getALLTerm().size();
   }

   public List<Newsletter> getNewslettersByTitle(String title, boolean paging) {

      log.debug(String.format("Get newsletter by title %s", title));

      return newsletterCAO.getNewsletterByConstraint("title", "like", title, paging);
   }

   public Newsletter getNewsletterBySubscription(int id) {
      int newsletterId = newsletterCAO.getNewsletterIdBySubscription(id);
      if (newsletterId < 1) {
         return null;
      }
      return newsletterCAO.getNewsletterById(newsletterId);
   }

   public List<Newsletter> getNewsletters(String subscriber, String title, boolean paging) {

      log.debug(String.format("Get Newsletters by subscriber %s and title %s", subscriber, title));

      boolean sc = StringUtils.isNotBlank(subscriber);
      boolean tc = StringUtils.isNotBlank(title);

      if (sc && tc) {
         return getAllNewsletterBySubscriberAndTitle(subscriber, title, paging);
      } else if (sc && !tc) {
         return getAllNewsletterBySubscriber(subscriber, paging);
      } else if (tc) {
         return getNewslettersByTitle(title, paging);
      } else {
         return getAllNewsletter(paging);
      }
   }

   public void processBouncesOfPublication(String publicationId, String userId) {
      // todo test.
      int pId = Integer.parseInt(publicationId);
      int uId = Integer.parseInt(userId);
      int newsletterId = publicationCAO.getNewsletterId(pId);
      Node newsletterNode = newsletterCAO.getNewsletterNodeById(newsletterId);
      Node subscriptionNode = subscriptionCAO.getSubscriptionNode(newsletterId, uId);
      int bouncesCount = subscriptionNode.getIntValue("count_bounces");
      int maxAllowedBonce = newsletterNode.getIntValue("max_bounces");

      if (bouncesCount > maxAllowedBonce) {
         subscriptionCAO.pause(subscriptionNode.getNumber());
      }
      statisticCAO.logPubliction(uId, newsletterId, StatisticResult.HANDLE.BOUNCE);
      subscriptionCAO.updateLastBounce(subscriptionNode.getNumber());
   }

   private List<Newsletter> getAllNewsletterBySubscriber(String subscriber, boolean paging) {
      return null;
   }

   private List<Newsletter> getAllNewsletterBySubscriberAndTitle(String subscriber, String title, boolean paging) {
      return null;
   }

   public List<Term> getNewsletterTermsByName(int newsletterId, String name, boolean paging) {
      List<Term> terms = newsletterCAO.getNewsletterTermsByName(newsletterId, name, paging);
      return terms;
   }

   public void processBouncesOfPublication(String publicationId, String userId, String bounceContent) {
      newsletterCAO.processBouncesOfPublication(publicationId, userId, bounceContent);
   }
}
