package com.finalist.newsletter.services.impl;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Node;

import com.finalist.newsletter.cao.NewsletterCAO;
import com.finalist.newsletter.cao.NewsletterSubscriptionCAO;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.domain.Term;
import static com.finalist.newsletter.domain.Subscription.STATUS;
import com.finalist.newsletter.services.NewsletterSubscriptionServices;
import com.finalist.newsletter.util.DateUtil;

public class NewsletterSubscriptionServicesImpl implements NewsletterSubscriptionServices {

   NewsletterSubscriptionCAO cao;
   NewsletterCAO newsletterCAO;

   private static Log log = LogFactory.getLog(NewsletterSubscriptionServicesImpl.class);

   public void setCao(NewsletterSubscriptionCAO cao) {
      this.cao = cao;

   }

   public void setNewsletterCao(NewsletterCAO newsletterCAO) {
      this.newsletterCAO = newsletterCAO;
   }

   public List<Subscription> getSubscriptionList(String[] allowedLetters, int userId) {

      List<Subscription> subscriptionList = new ArrayList<Subscription>();
      int newsletterId;
      for (int i = 0; i < allowedLetters.length; i++) {
         newsletterId = Integer.parseInt(allowedLetters[i]);
         subscriptionList.add(getSubscription(newsletterId, userId));
      }
      return subscriptionList;
   }

   private Subscription getSubscription(int newsletterId, int userId) {

      Subscription subscription = cao.getSubscription(newsletterId, userId);
      Newsletter newsletter = newsletterCAO.getNewsletterById(newsletterId);

      if (subscription == null) {
         subscription = new Subscription();
         subscription.setTerms(newsletter.getTerms());

      }
      else {
         Set<Term> newsletterTerms = newsletter.getTerms();
         Set<Term> subscriptionTerms = subscription.getTerms();
         for (Term term : newsletterTerms) {
            if (subscriptionTerms.size() == 0) {
               subscriptionTerms.addAll(newsletterTerms);
            }
            else if (!subscriptionTerms.contains(term)) {
               subscriptionTerms.add(term);
            }
         }
      }
      subscription.setNewsletter(newsletter);
      return subscription;
   }

   public List<Subscription> getNewSubscription(String[] allowedLetters) {
      List<Subscription> list = new ArrayList<Subscription>();
      int nodenumber;
      for (int i = 0; i < allowedLetters.length; i++) {
         nodenumber = Integer.parseInt(allowedLetters[i]);
         Newsletter newsletter = newsletterCAO.getNewsletterById(nodenumber);
         Subscription subscription = new Subscription();
         subscription.setNewsletter(newsletter);
         Newsletter test = subscription.getNewsletter();
         subscription.setTerms(newsletter.getTerms());
         list.add(subscription);
      }
      return list;
   }

   public void changeStatus(int userId, int newsletterId) {
      log.debug(String.format("user % change subscribe status on %s", userId, newsletterId));

      Subscription subscription = cao.getSubscription(newsletterId, userId);

      if (STATUS.ACTIVE.equals(subscription.getStatus())) {
         subscription.setStatus(STATUS.INACTIVE);
      }
      else {
         subscription.setStatus(STATUS.ACTIVE);
      }
      cao.modifySubscriptionStauts(subscription);
   }

   public void pause(String subscriptionId, String duration, String durationunit) {
      Subscription subscription = cao.getSubscriptionById(Integer.parseInt(subscriptionId));

      subscription.setStatus(STATUS.PAUSED);
      if (null != duration) {
         Date date = DateUtil.calculateDateByDuration(DateUtil.getCurrent(), Integer.parseInt(duration), durationunit);
         subscription.setPausedTill(date);
      }

      cao.updateSubscription(subscription);
   }


   public void pause(String subscriptionId, String resumeDate) {

      log.debug(String.format("Pasue subscription %s till %s", subscriptionId, resumeDate));

      Subscription subscription = cao.getSubscriptionById(Integer.parseInt(subscriptionId));

      subscription.setStatus(STATUS.PAUSED);
      if (null != resumeDate) {
         Date date = DateUtil.parser(resumeDate);
         subscription.setPausedTill(date);
      }

      cao.updateSubscription(subscription);
   }


   public boolean hasSubscription(int userId) {

      List<Node> list = cao.querySubcriptionByUser(userId);
      if (0 == list.size()) {
         return false;
      }
      else {
         return true;
      }
   }

   public void selectTermInLetter(int userId, int newsletterId, int termId) {
      log.debug("Add term " + termId + " to user" + userId + "'s newsletter:" + newsletterId);
      Subscription subscription = cao.getSubscription(newsletterId, userId);
      Set<Term> termList = subscription.getTerms();
      Iterator it = termList.iterator();
      for (int i = 0; i < termList.size(); i++) {
         Term term = (Term) it.next();
         if (termId == term.getId()) {
            term.setSubscription(true);
         }
      }
      cao.addSubscriptionTerm(subscription, termId);

   }

   public void unSelectTermInLetter(int userId, int newsletterId, int termId) {
      log.debug("Remove term " + termId + " to user " + userId + "'s newsletter:" + newsletterId);
      Subscription subscription = cao.getSubscription(newsletterId, userId);
      Set<Term> termList = subscription.getTerms();
      Iterator it = termList.iterator();
      for (int i = 0; i < termList.size(); i++) {
         Term term = (Term) it.next();
         if (termId == term.getId()) {
            term.setSubscription(false);
         }
      }
      cao.removeSubscriptionTerm(subscription, termId);
   }

   public void modifyStauts(int userId, int newsletterId, String status) {
      log.debug("user " + userId + " change subscription status of newsletter " + newsletterId + " to " + status);
      Subscription subscription = cao.getSubscription(newsletterId, userId);

      if (null == subscription) {
         cao.createSubscription(userId, newsletterId);
      }
      else {
         subscription.setStatus(STATUS.valueOf(status));
         cao.modifySubscriptionStauts(subscription);
      }
   }

   public void modifyFormat(int userId, int newsletterId, String format) {
      log.debug("User " + userId + " modify format of newsletter " + newsletterId + "to " + format);
      Subscription subscription = cao.getSubscription(newsletterId, userId);
      subscription.setMimeType(format);
      cao.modifySubscriptionFormat(subscription);
   }

   public boolean noSubscriptionRecord(int userId, int newsletterId) {
      Subscription subscription = cao.getSubscription(newsletterId, userId);
      return subscription == null;
   }

   public void addNewRecord(int userId, int newsletterId) {
      Subscription subscription = new Subscription();
      Newsletter newsletter = new Newsletter();
      newsletter.setId(newsletterId);
      subscription.setNewsletter(newsletter);
      subscription.setMimeType("text/html");
      subscription.setStatus(STATUS.ACTIVE);
      cao.addSubscriptionRecord(subscription, userId);
   }

   public void resume(String subscriptionId) {
      Subscription subscription = cao.getSubscriptionById(Integer.parseInt(subscriptionId));
      subscription.setStatus(STATUS.ACTIVE);
      cao.updateSubscription(subscription);
   }

   public void terminateUserSubscription(String subscriptionId) {
      Subscription subscription = cao.getSubscriptionById(Integer.parseInt(subscriptionId));
      subscription.setStatus(STATUS.INACTIVE);
      cao.updateSubscription(subscription);
   }

   public Subscription getSubscription(String sId) {
      int id = Integer.parseInt(sId);
      return cao.getSubscriptionById(id);
   }

   public List<Subscription> getActiveSubscription(int userId) {
      return cao.getSubscriptionByUserIdAndStatus(userId,STATUS.ACTIVE); 
   }
}
