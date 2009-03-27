package com.finalist.newsletter.services;

import java.util.List;
import java.util.Set;

import com.finalist.cmsc.services.community.person.Person;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.Subscription;

public interface NewsletterSubscriptionServices {

   public boolean hasSubscription(int userId);

   public boolean noSubscriptionRecord(int userId, int newsletterId);

   public void selectTermInLetter(int userId, int newsletterId, int termId);

   public void unSelectTermInLetter(int userId, int newsletterId, int termId);

   public void modifyFormat(int userId, int newsletterId, String format);

   public void modifyStauts(int userId, int newsletterId, String status);

   public void addNewRecord(int userId, int newsletterId);

   public List<Subscription> getSubscriptionList(String[] newsletters, int userId);

   public List<Subscription> getNewSubscription(String[] newsletters);

   public void changeStatus(int userId, int newsletterId);

   public void pause(String subscriptionId, String duration, String durationunit);

   public void pause(String subscriptionId, String resumeDate);

   public void resume(String subscriptionId);

   public void terminateUserSubscription(String subscriptionId);

   public Subscription getSubscription(String sId);

   public List<Subscription> getActiveSubscription(int userId);

   public int countAllSubscriptions();

   public int countSubscriptionByNewsletter(int id);

   public List<Subscription> getAllSubscription();

   public List<Subscription> getSubscriptionsByNewsletterId(String newsletterId);

   public Set<Newsletter> getNewslettersBySubscription(int subscriberId, String title, boolean paging);

   public List<Person> getAllSubscribers(String name, String email);

   public List<Subscription> getSubscriptionBySubscriber(String subscriberId);

   public Subscription getSubscription(int sbId, int nId);
   
   public boolean isAbleSubscrip(int sbId, int nId);

   void unSubscribeAllInNewsletter(int integer);

   public void createSubscription(int userId, int newsletterId);

   public Set<Long> getAuthenticationIdsByTerms(int newsletterId, String terms);

   public Set<Long> getAuthenticationIdsByNewsletter(int newsletterId);

   public Set<Long> getAuthenticationIds();

   public Set<Integer> getRecordIdByNewsletterAndName(int newsletter, String term);

   public String getNewsletterNameList(int authenticationId);

   public String getTermsNameList(int authenticationId);

   public List<Subscription>  getSubscriptions(String[] allowedLetters, int userId) ;
   
}
