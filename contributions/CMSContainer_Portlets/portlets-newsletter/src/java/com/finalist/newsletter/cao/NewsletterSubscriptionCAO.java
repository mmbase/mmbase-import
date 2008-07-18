package com.finalist.newsletter.cao;

import java.util.List;
import java.util.Set;
import java.util.Collection;

import org.mmbase.bridge.Node;

import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.domain.Term;
import com.finalist.newsletter.domain.Publication;

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

   void updateLastBounce(int subscriptionId);

   Node getSubscriptionNode(int newsletterId, int userId);

   public void pause(int subscriptionId);

   public Set<Node> getRecordByNewsletterAndName(int newsletterId,String termName);
   
   public Set<Node> getNewslettersByScriptionRecord(int authenticationId);
   
   public Set<Node> getTermsByScriptionRecord(int authenticationId);
   
}
