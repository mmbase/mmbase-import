package com.finalist.newsletter.services.impl;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.services.community.person.Person;
import com.finalist.cmsc.services.community.person.PersonService;
import com.finalist.cmsc.util.DateUtil;
import com.finalist.newsletter.cao.*;
import com.finalist.newsletter.domain.*;
import com.finalist.newsletter.domain.StatisticResult.HANDLE;
import com.finalist.newsletter.domain.Subscription.STATUS;
import com.finalist.newsletter.services.*;
import com.finalist.newsletter.util.NewsletterSubscriptionUtil;

public class NewsletterSubscriptionServicesImpl implements NewsletterSubscriptionServices {

   private static Log log = LogFactory.getLog(NewsletterSubscriptionServicesImpl.class);
   NewsLetterStatisticCAO statisticCAO;
   NewsletterSubscriptionCAO subscriptionCAO;
   NewsletterCAO newsletterCAO;
   NewsletterService newsletterService;
   PersonService personService;

   public void setStatisticCAO(NewsLetterStatisticCAO statisticCAO) {
      this.statisticCAO = statisticCAO;
   }

   public void setNewsletterCAO(NewsletterCAO newsletterCAO) {
      this.newsletterCAO = newsletterCAO;
   }

   public void setNewsletterService(NewsletterService newsletterService) {
      this.newsletterService = newsletterService;
   }

   public void setPersonService(PersonService personService) {
      this.personService = personService;
   }

   public void setSubscriptionCAO(NewsletterSubscriptionCAO subscriptionCAO) {
      this.subscriptionCAO = subscriptionCAO;
   }

   public List<Subscription> getSubscriptionList(String[] allowedLetters, int userId) {

      List<Subscription> subscriptionList = new ArrayList<Subscription>();
      if (null != allowedLetters) {
         for (String allowedLetter : allowedLetters) {
            subscriptionList.add(getSubscription(allowedLetter, Integer.toString(userId)));
         }
      }
      return subscriptionList;
   }

   private Subscription getSubscription(String newsletterId, String userId) {
      int nId = Integer.parseInt(newsletterId);
      int uId = Integer.parseInt(userId);

      Subscription subscription = subscriptionCAO.getSubscription(nId, uId);
      Newsletter newsletter = newsletterCAO.getNewsletterById(nId);

      if (subscription == null) {
         subscription = new Subscription();
         subscription.setTerms(newsletter.getTerms());

      } else {
         Set<Term> newsletterTerms = newsletter.getTerms();
         Set<Term> subscriptionTerms = subscription.getTerms();
         for (Term term : newsletterTerms) {
            if (subscriptionTerms.size() == 0) {
               subscriptionTerms.addAll(newsletterTerms);
            } else if (!subscriptionTerms.contains(term)) {
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
      for (String allowedLetter : allowedLetters) {
         nodenumber = Integer.parseInt(allowedLetter);
         Newsletter newsletter = newsletterCAO.getNewsletterById(nodenumber);
         Subscription subscription = new Subscription();
         subscription.setNewsletter(newsletter);
         subscription.setTerms(newsletter.getTerms());
         list.add(subscription);
      }
      return list;
   }

   public void changeStatus(int userId, int newsletterId) {
      log.debug(String.format("user % change subscribe status on %s", userId, newsletterId));
      Subscription subscription = subscriptionCAO.getSubscription(newsletterId, userId);

      if (STATUS.ACTIVE.equals(subscription.getStatus())) {
         subscription.setStatus(STATUS.INACTIVE);
      } else {
         subscription.setStatus(STATUS.ACTIVE);
      }
      subscriptionCAO.modifySubscriptionStauts(subscription);
      if (STATUS.ACTIVE.equals(subscription.getStatus())) {
         statisticCAO.logPubliction(userId, newsletterId, HANDLE.ACTIVE);
      } else {
         statisticCAO.logPubliction(userId, newsletterId, HANDLE.INACTIVE);
      }
   }

   public void pause(String subscriptionId, String duration, String durationunit) {
      Subscription subscription = subscriptionCAO.getSubscriptionById(Integer.parseInt(subscriptionId));
      subscription.setStatus(STATUS.PAUSED);
      if (null != duration) {
         Date date = DateUtil.calculateDateByDuration(DateUtil.getCurrent(), Integer.parseInt(duration), durationunit);
         subscription.setPausedTill(date);
      }

      subscriptionCAO.updateSubscription(subscription);
   }

   public void pause(String subscriptionId, String resumeDate) {
      log.debug(String.format("Pasue subscription %s till %s", subscriptionId, resumeDate));

      Subscription subscription = subscriptionCAO.getSubscriptionById(Integer.parseInt(subscriptionId));

      subscription.setStatus(STATUS.PAUSED);
      if (null != resumeDate) {
         Date date = DateUtil.parser(resumeDate);
         subscription.setPausedTill(date);
      }

      subscriptionCAO.updateSubscription(subscription);
   }

   public boolean hasSubscription(int userId) {

      List<Node> list = subscriptionCAO.querySubcriptionByUser(userId);
      if (0 == list.size()) {
         return false;
      } else {
         return true;
      }
   }

   public void selectTermInLetter(int userId, int newsletterId, int termId) {
      log.debug("Add term " + termId + " to user" + userId + "'s newsletter:" + newsletterId);
      Subscription subscription = subscriptionCAO.getSubscription(newsletterId, userId);
      Set<Term> termList = subscription.getTerms();
      Iterator<Term> it = termList.iterator();
      for (int i = 0; i < termList.size(); i++) {
         Term term = it.next();
         if (termId == term.getId()) {
            term.setSubscription(true);
         }
      }
      subscriptionCAO.addSubscriptionTerm(subscription, termId);

   }

   public void unSelectTermInLetter(int userId, int newsletterId, int termId) {
      log.debug("Remove term " + termId + " to user " + userId + "'s newsletter:" + newsletterId);
      Subscription subscription = subscriptionCAO.getSubscription(newsletterId, userId);
      Set<Term> termList = subscription.getTerms();
      Iterator<Term> it = termList.iterator();
      for (int i = 0; i < termList.size(); i++) {
         Term term = it.next();
         if (termId == term.getId()) {
            term.setSubscription(false);
         }
      }
      subscriptionCAO.removeSubscriptionTerm(subscription, termId);
   }

   public void modifyStauts(int userId, int newsletterId, String status) {
      log.debug("user " + userId + " change subscription status of newsletter " + newsletterId + " to " + status);
      Subscription subscription = subscriptionCAO.getSubscription(newsletterId, userId);

      if (null == subscription) {
         subscriptionCAO.createSubscription(userId, newsletterId);
      } else {
         subscription.setStatus(STATUS.valueOf(status));
         subscriptionCAO.modifySubscriptionStauts(subscription);
      }
   }

   public void modifyFormat(int userId, int newsletterId, String format) {
      log.debug("User " + userId + " modify format of newsletter " + newsletterId + "to " + format);
      Subscription subscription = subscriptionCAO.getSubscription(newsletterId, userId);
      subscription.setMimeType(format);
      subscriptionCAO.modifySubscriptionFormat(subscription);
   }

   public boolean noSubscriptionRecord(int userId, int newsletterId) {
      Subscription subscription = subscriptionCAO.getSubscription(newsletterId, userId);
      return subscription == null;
   }

   public void addNewRecord(int userId, int newsletterId) {
      Subscription subscription = new Subscription();
      Newsletter newsletter = new Newsletter();
      newsletter.setId(newsletterId);
      subscription.setNewsletter(newsletter);
      subscription.setMimeType("text/html");
      subscription.setStatus(STATUS.ACTIVE);
      subscriptionCAO.addSubscriptionRecord(subscription, userId);
      statisticCAO.logPubliction(userId, newsletterId, HANDLE.ACTIVE);
   }

   public void resume(String subscriptionId) {
      Subscription subscription = subscriptionCAO.getSubscriptionById(Integer.parseInt(subscriptionId));
      subscription.setStatus(STATUS.ACTIVE);
      subscriptionCAO.updateSubscription(subscription);
   }

   public void terminateUserSubscription(String subscriptionId) {
      Subscription subscription = subscriptionCAO.getSubscriptionById(Integer.parseInt(subscriptionId));
      subscription.setStatus(STATUS.INACTIVE);
      subscriptionCAO.updateSubscription(subscription);
      int newsletterId = newsletterCAO.getNewsletterIdBySubscription(Integer.parseInt(subscriptionId));
      int userId = CommunityModuleAdapter.getCurrentUserId();
      statisticCAO.logPubliction(userId, newsletterId, HANDLE.INACTIVE);
   }

   public Subscription getSubscription(String sId) {
      int id = Integer.parseInt(sId);
      return subscriptionCAO.getSubscriptionById(id);
   }

   public List<Subscription> getActiveSubscription(int userId) {
      return subscriptionCAO.getSubscriptionByUserIdAndStatus(userId, STATUS.ACTIVE);
   }

   public int countAllSubscriptions() {
      return getAllSubscription().size();
   }

   public int countSubscriptionByNewsletter(int id) {
      return subscriptionCAO.countSubscription(id);
   }

   public List<Subscription> getAllSubscription() {
      List<Node> subscriptionNodes = subscriptionCAO.getAllSubscriptions();

      List<Subscription> subscriptions = new ArrayList<Subscription>();

      for (Node node : subscriptionNodes) {
         Subscription subscription = NewsletterSubscriptionUtil.convertFromNode(node);
         subscription.setNewsletter(newsletterService.getNewsletterBySubscription(subscription.getId()));
         subscriptions.add(subscription);
      }

      return subscriptions;
   }

   public List<Subscription> getSubscriptionsByNewsletterId(String newsletterId) {
      log.debug("Get all subscriptions of newsletter " + newsletterId);
      return subscriptionCAO.getSubscription(Integer.parseInt(newsletterId));
   }

   public Set<Newsletter> getNewslettersBySubscription(int subscriberId, String title, boolean paging){
      log.debug("Get Subscriptions of subscriberId" + subscriberId);
      List<Newsletter> newsletters = subscriptionCAO.getNewslettersByScription(subscriberId, title, paging);
      Set<Newsletter> results = new HashSet<Newsletter>();
      for(Newsletter newsletter: newsletters){
         String status = subscriptionCAO.getSubscription(newsletter.getId(), subscriberId).getStatus().toString();
         newsletter.setStatus(status);
         results.add(newsletter);
      }
      return results;
   }

   private Set<String> getPersonIdSet(String name, String email) {
      Set<String> personIds = new HashSet<String>();

      Person person = new Person();
      if (StringUtils.isNotBlank(name)) {
         person.setLastName(name);
      }

      if (StringUtils.isNotBlank(email)) {
         person.setEmail(email);
      }

      List<Person> persons = personService.getLikePersons(person);
      for (Person p : persons) {
         personIds.add(p.getId().toString());
      }

      return personIds;
   }

   public List<Person> getAllSubscribers(String name, String email) {
      List<Node> subscriptions = subscriptionCAO.getAllSubscriptions();
      Set<Integer> authenticationIds = new HashSet<Integer>();
      for (Node subscription : subscriptions) {
         int authId = subscription.getIntValue("subscriber");
            authenticationIds.add(authId);
      }
      if (authenticationIds.size() > 0) {
         List<Person> persons = personService.getPersonsByAuthenticationIds(authenticationIds, name, email);
         return persons;
      } else {
         return null;
      }
   }

   public List<Subscription> getSubscriptionBySubscriber(String subscriberId) {
      return subscriptionCAO.getSubscriptionByUserIdAndStatus(Integer.parseInt(subscriberId), null);
   }

   public Subscription getSubscription(int userId, int newsletterId) {
      if (isAbleSubscrip(userId,newsletterId)) {
         return subscriptionCAO.getSubscription(newsletterId, userId);
      }else{
         return null;
      }
   }

   public void unSubscribeAllInNewsletter(int newsletterId) {
      List<Subscription> subscriptions = subscriptionCAO.getSubscription(newsletterId);
      for (Subscription subscription : subscriptions) {
         subscription.setStatus(STATUS.INACTIVE);
         subscriptionCAO.modifySubscriptionStauts(subscription);
      }
   }

   public void createSubscription(int userId, int newsletterId) {
      subscriptionCAO.createSubscription(userId, newsletterId);
   }

   public Set<Integer> getRecordIdByNewsletterAndName(int newsletterId, String termName) {
      Set<Node> subscriptions = subscriptionCAO.getRecordByNewsletterAndName(newsletterId, termName);
      Set<Integer> authenticationIds = new HashSet<Integer>();
      for (Node subscription : subscriptions) {
         authenticationIds.add(subscription.getIntValue("subscriber"));
      }
      return authenticationIds;
   }

   public String getNewsletterNameList(int authenticationId) {
      Set<Node> newsletterList = subscriptionCAO.getNewslettersByScriptionRecord(authenticationId);
      String tmpTitle = "";
      for (Node newsletterNode : newsletterList) {
         if (StringUtils.isNotBlank(newsletterNode.getStringValue("title"))) {
            tmpTitle += newsletterNode.getStringValue("title") + ", ";
         }
      }
      if (tmpTitle.length() > 0) {
         return tmpTitle.substring(0, tmpTitle.length() - 2);
      } else {
         return tmpTitle;
      }

   }

   public String getTermsNameList(int authenticationId) {
      Set<Node> termList = subscriptionCAO.getTermsByScriptionRecord(authenticationId);
      String tmpNames = "";
      for (Node termNode : termList) {
         if (StringUtils.isNotBlank(termNode.getStringValue("name"))) {
            tmpNames += termNode.getStringValue("name") + ", ";
         }
      }
      if (tmpNames.length() > 0) {
         return tmpNames.substring(0, tmpNames.length() - 2);
      } else {
         return tmpNames;
      }

   }

   public Set<Long> getAuthenticationIdsByTerms(int newsletterId, String terms) {
      List<Node> subscriptions = subscriptionCAO.getSubscriptionsByTerms(newsletterId, terms);

      Set<Long> subscirberIds = new HashSet<Long>();
      for (Node subscription : subscriptions) {
         subscirberIds.add(new Long(subscription.getIntValue("subscriber")));
      }
      return subscirberIds;
   }

   public Set<Long> getAuthenticationIdsByNewsletter(int newsletterId) {
      List<Subscription> subscriptions = subscriptionCAO.getSubscription(newsletterId);
      Set<Long> subscriberIds = new HashSet<Long>();
      for(Subscription subscription: subscriptions){
         subscriberIds.add(new Long(Integer.parseInt(subscription.getSubscriberId())));
      }
      return subscriberIds;
   }

   public Set<Long> getAuthenticationIds() {
      List<Node> subscriptions = subscriptionCAO.getAllSubscriptions();
      Set<Long> subscriberIds = new HashSet<Long>();
      for(Node subscription: subscriptions){
         subscriberIds.add(new Long(subscription.getIntValue("subscriber")));
      }
      return subscriberIds;
   }

   public boolean isAbleSubscrip(int userId, int newsletterId) {
      
      if (null == personService.getPersonByUserId(String.valueOf(userId))
            || null == newsletterService.getNewsletterName(newsletterId)) {
         return false;
      } else {
         return true;
      }
   }
   

}
