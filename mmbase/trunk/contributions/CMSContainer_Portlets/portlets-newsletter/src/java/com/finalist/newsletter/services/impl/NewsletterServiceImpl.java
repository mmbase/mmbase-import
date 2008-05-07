package com.finalist.newsletter.services.impl;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

import com.finalist.newsletter.cao.NewsletterCAO;
import com.finalist.newsletter.cao.NewsletterSubscriptionCAO;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.services.NewsletterService;
import com.finalist.newsletter.services.NewsletterSubscriptionServices;
import org.mmbase.bridge.NodeManager;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NewsletterServiceImpl implements NewsletterService {

   private static Log log = LogFactory.getLog(NewsletterServiceImpl.class);

   NewsletterCAO newsletterCAO;
   NewsletterSubscriptionCAO subscriptionCAO;
   NewsletterSubscriptionServices subscriptionServices;

   public void setNewsletterCAO(NewsletterCAO newsletterCAO) {
      this.newsletterCAO = newsletterCAO;
   }

   public void setSubscriptionCAO(NewsletterSubscriptionCAO subscriptionCAO) {
      this.subscriptionCAO = subscriptionCAO;
   }

   public void setSubscriptionServices(NewsletterSubscriptionServices subscriptionServices) {
      this.subscriptionServices = subscriptionServices;
   }

   public List<Newsletter> getAllNewsletter() {

      return newsletterCAO.getNewsletterByConstraint(null, null, null);
   }

   public String getNewsletterName(String newsletterId) {
      String name = "";

      if (StringUtils.isNotBlank(newsletterId)) {
         name =  newsletterCAO.getNewsletterById(Integer.parseInt(newsletterId)).getTitle();
      }
      
      return name;
   }

   public int countAllNewsletters() {
      return getAllNewsletter().size();
   }

   public int countAllTerms() {
      return newsletterCAO.getALLTerm().size();
   }

   public List<Newsletter> getNewslettersByTitle(String title) {

      log.debug(String.format("Get newsletter by title %s", title));

      return newsletterCAO.getNewsletterByConstraint("title", "like", title);
   }

   public Newsletter getNewsletterBySubscription(int id) {
      int newsletterId = newsletterCAO.getNewsletterIdBySubscription(id);
      return newsletterCAO.getNewsletterById(newsletterId);
   }

   public List<Newsletter> getNewsletters(String subscriber, String title) {

      log.debug(String.format("Get Newsletters by subscriber %s and title %s", subscriber, title));

      boolean sc = StringUtils.isNotBlank(subscriber);
      boolean tc = StringUtils.isNotBlank(title);

      if (sc && tc) {
         return getAllNewsletterBySubscriberAndTitle(subscriber, title);
      }
      else if (sc && !tc) {
         return getAllNewsletterBySubscriber(subscriber);
      }
      else if (tc) {
         return getNewslettersByTitle(title);
      }
      else {
         return getAllNewsletter();
      }
   }

   private List<Newsletter> getAllNewsletterBySubscriber(String subscriber) {
      return null;
   }

   private List<Newsletter> getAllNewsletterBySubscriberAndTitle(String subscriber, String title) {
      return null;
   }

}
