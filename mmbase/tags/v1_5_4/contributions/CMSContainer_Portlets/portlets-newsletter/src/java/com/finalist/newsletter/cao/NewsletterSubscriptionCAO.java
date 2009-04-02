package com.finalist.newsletter.cao;

import java.util.List;
import java.util.Set;

import org.mmbase.bridge.Node;

import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.domain.Term;

public interface NewsletterSubscriptionCAO {

   public List<Node> querySubcriptionByUser(int userId);

   public void addSubscriptionRecord(Subscription subscription, int userId);

   public void modifySubscriptionStauts(Subscription subscription);

   public void modifySubscriptionFormat(Subscription subscription);

   public void addSubscriptionTerm(Subscription subscription, int termId);

   public void removeSubscriptionTerm(Subscription subscription, int termId);

   public List<Subscription> getSubscription(int newsletterId);

   public Subscription getSubscription(int newsletterId, int userId);

   public Set<Term> getTerms(int id);

   public Subscription getSubscriptionById(int id);

   public void createSubscription(int userId, int newsletterId);

   public void updateSubscription(Subscription subscription);

   public List<Subscription> getSubscriptionByUserIdAndStatus(int userId, Subscription.STATUS status);

   public List<Node> getAllSubscriptions();
   public List<Node>getSubscriptionsByTerms(int newsletterId, String terms);

   void updateLastBounce(int subscriptionId);

   Node getSubscriptionNode(int newsletterId, int userId);

   public void pause(int subscriptionId);

   public Set<Node> getRecordByNewsletterAndName(int newsletterId,String termName);

   public Set<Node> getNewslettersByScriptionRecord(int authenticationId);
   
   public List<Newsletter> getNewslettersByScription(int subscriberId, String title, boolean paging);

   public Set<Node> getTermsByScriptionRecord(int authenticationId);

   public int countSubscription(int newsletterId);

}
