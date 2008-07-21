package com.finalist.newsletter.services.impl;

import java.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.services.community.person.Person;
import com.finalist.newsletter.NewsletterSendFailException;
import com.finalist.newsletter.cao.*;
import com.finalist.newsletter.domain.*;
import com.finalist.newsletter.domain.Publication.STATUS;
import com.finalist.newsletter.publisher.NewsletterPublisher;
import com.finalist.newsletter.services.CommunityModuleAdapter;
import com.finalist.newsletter.services.NewsletterPublicationService;

public class NewsletterPublicationServiceImpl implements NewsletterPublicationService {

   private static Logger log = Logging.getLoggerInstance(NewsletterPublicationServiceImpl.class.getName());

   private NewsletterPublisher publisher;
   private NewsletterPublicationCAO publicationCAO;
   private NewsletterSubscriptionCAO subscriptionCAO;
   private NewsLetterStatisticCAO statisticCAO;

   //CAO setters

   public void setPublisher(NewsletterPublisher publisher) {
      this.publisher = publisher;
   }

   public void setPublicationCAO(NewsletterPublicationCAO publicationCAO) {
      this.publicationCAO = publicationCAO;
   }

   public void setSubscriptionCAO(NewsletterSubscriptionCAO subscriptionCAO) {
      this.subscriptionCAO = subscriptionCAO;
   }

   public void setStatisticCAO(NewsLetterStatisticCAO statisticCAO) {
      this.statisticCAO = statisticCAO;
   }

   //service method.
   public STATUS getStatus(int publicationId) {
      return publicationCAO.getPublication(publicationId).getStatus();
   }

   public void setStatus(int publicationId, STATUS status) {
      publicationCAO.setStatus(publicationId, status);
   }

   /**
    * deliver all READY publications in the system
    */
   public void deliverAllPublication() {
      log.info("starting deliver all publications in READY status");

      List<Integer> publications = publicationCAO.getIntimePublicationIds();

      log.debug(publications.size() + " publications found");

      for (int publicationId : publications) {
         deliver(publicationId);
      }
   }

   /**
    * deliver specific publication.
    *
    * @param publicationId The id of the publication to be sent out
    */
   public Map<String,List<String>> deliver(int publicationId) {

      int newsletterId = publicationCAO.getNewsletterId(publicationId);
      List<String> sendSuccess = new ArrayList<String>();
      List<String> sendFails = new ArrayList<String>() ;
      Map<String,List<String>> sendResults = new HashMap<String,List<String>>();
      List<Subscription> subscriptions = subscriptionCAO.getSubscription(newsletterId);
      log.debug("deliver publication " + publicationId + " which has " + subscriptions.size() + " subscriptions");

      Publication publication = publicationCAO.getPublication(publicationId);

      for (Subscription subscription : subscriptions) {
         Set<Term> terms = subscriptionCAO.getTerms(subscription.getId());
         Person subscripber = CommunityModuleAdapter.getUserById(subscription.getSubscriberId());
         subscription.setEmail(subscripber.getEmail());
         subscription.setTerms(terms);
         try {
            publisher.deliver(publication, subscription);
            sendSuccess.add(subscription.getSubscriberId());
         }
         catch (NewsletterSendFailException e) {
            sendFails.add(subscription.getSubscriberId());
            log.error(e.getMessage());
         }
      }
      sendResults.put(SEND_SUCCESS, sendSuccess);
      sendResults.put(SEND_FAIL, sendFails);

      publicationCAO.setStatus(publicationId, STATUS.DELIVERED);
      publicationCAO.renamePublicationTitle(publicationId);
      return sendResults;
   }

   public int countAllPublications() {
      return publicationCAO.getAllPublications().size();
   }

   public int countPublicationByNewsletter(int id) {
      return publicationCAO.getPublicationsByNewsletter(id, null).size();
   }

   public int countSentPublications(int id) {
      return publicationCAO.getPublicationsByNewsletter(id, STATUS.DELIVERED).size();
   }

   public void deliver(int publicationId, String email, String mimeType) {
      Publication publication = publicationCAO.getPublication(publicationId);
      Subscription subscription = new Subscription();
      subscription.setEmail(email);
      subscription.setTerms(new HashSet<Term>());
      subscription.setMimeType(mimeType);
      publisher.deliver(publication, subscription);
   }

   public Set<Publication> getPublicationByNewsletter(int newsletterId){
	   Set<Publication> result = new HashSet<Publication>();

	   for (Publication publication : publicationCAO.getPublicationsByNewsletter(newsletterId, null)) {
		   result.add(publication);
	   }
	   return result;
   }

   public Set<Publication> searchPublication(int id , String title, String subject, Date startDate, Date endDate, int pagesize, int offset){
	   Set<Publication> result = new HashSet<Publication>();
	   result = publicationCAO.getPublicationsByNewsletterAndPeriod(id ,title, subject, startDate, endDate, pagesize, offset);
	   return result;
   }

   public int searchPublicationCountForEdit(int id, String title, String subject, Date startDate, Date endDate){
	   int tmpResultCount = publicationCAO.getPublicationCountForEdit(id, title, subject, startDate, endDate);
	   return tmpResultCount;
   }
}
