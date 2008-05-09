package com.finalist.newsletter.services.impl;

import com.finalist.cmsc.services.community.person.Person;
import com.finalist.newsletter.cao.NewsLetterStatisticCAO;
import com.finalist.newsletter.cao.NewsletterCAO;
import com.finalist.newsletter.cao.NewsletterPublicationCAO;
import com.finalist.newsletter.cao.NewsletterSubscriptionCAO;
import com.finalist.newsletter.domain.Publication;
import com.finalist.newsletter.domain.Publication.STATUS;
import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.domain.Term;
import com.finalist.newsletter.publisher.NewsletterPublisher;
import com.finalist.newsletter.services.CommunityModuleAdapter;
import com.finalist.newsletter.services.NewsletterPublicationService;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import java.util.List;
import java.util.Set;

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
   public void deliver(int publicationId) {

      int newsletterId = publicationCAO.getNewsletterId(publicationId);
      
      List<Subscription> subscriptions = subscriptionCAO.getSubscription(newsletterId);
      log.debug("deliver publication " + publicationId + " which has " + subscriptions.size() + " subscriptions");

      Publication publication = publicationCAO.getPublication(publicationId);

      for (Subscription subscription : subscriptions) {
         Set<Term> terms = subscriptionCAO.getTerms(subscription.getId());
         Person subscripber = CommunityModuleAdapter.getUserById(subscription.getSubscriberId());
         subscription.setEmail(subscripber.getEmail());
         subscription.setTerms(terms);
         publisher.deliver(publication, subscription);
      }

     
      publicationCAO.setStatus(publicationId, STATUS.DELIVERED);
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
      subscription.setTerms(publicationCAO.getTermsByPublication(publicationId));
      subscription.setMimeType(mimeType);
      publisher.deliver(publication, subscription);
   }
}
