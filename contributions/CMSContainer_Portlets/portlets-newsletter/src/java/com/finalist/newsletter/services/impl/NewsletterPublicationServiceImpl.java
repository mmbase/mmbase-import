package com.finalist.newsletter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.services.community.person.Person;
import com.finalist.cmsc.services.community.person.RegisterStatus;
import com.finalist.newsletter.NewsletterSendFailException;
import com.finalist.newsletter.cao.NewsletterPublicationCAO;
import com.finalist.newsletter.cao.NewsletterSubscriptionCAO;
import com.finalist.newsletter.domain.Publication;
import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.domain.Term;
import com.finalist.newsletter.domain.Publication.STATUS;
import com.finalist.newsletter.publisher.NewsletterPublisher;
import com.finalist.newsletter.services.CommunityModuleAdapter;
import com.finalist.newsletter.services.NewsletterPublicationService;
import com.finalist.newsletter.util.NewsletterPublicationUtil;

public class NewsletterPublicationServiceImpl implements NewsletterPublicationService {

   private static Logger log = Logging.getLoggerInstance(NewsletterPublicationServiceImpl.class.getName());

   private NewsletterPublisher publisher;
   private NewsletterPublicationCAO publicationCAO;
   private NewsletterSubscriptionCAO subscriptionCAO;
   // CAO setters

   public void setPublisher(NewsletterPublisher publisher) {
      this.publisher = publisher;
   }

   public void setPublicationCAO(NewsletterPublicationCAO publicationCAO) {
      this.publicationCAO = publicationCAO;
   }

   public void setSubscriptionCAO(NewsletterSubscriptionCAO subscriptionCAO) {
      this.subscriptionCAO = subscriptionCAO;
   }

   // service method.
   public STATUS getStatus(int publicationId) {
      return publicationCAO.getPublication(publicationId).getStatus();
   }

   public void setStatus(int publicationId, STATUS status) {
      publicationCAO.setStatus(publicationId, status);
   }

   /**
    * deliver all READY publications in the system
    */
   public void deliverAllPublications() {
      log.debug("starting deliver all publications in READY status");

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
   public Map<String, List<String>> deliver(int publicationId) {

      int newsletterId = publicationCAO.getNewsletterId(publicationId);
      List<String> sendSuccess = new ArrayList<String>();
      List<String> sendFails = new ArrayList<String>();
      Map<String, List<String>> sendResults = new HashMap<String, List<String>>();
      List<Subscription> subscriptions = subscriptionCAO.getSubscription(newsletterId);
      log.debug("deliver publication " + publicationId + " which has " + subscriptions.size() + " subscriptions");
      NewsletterPublicationUtil.setBeingSend(publicationId);
      Publication publication = publicationCAO.getPublication(publicationId);
      for (Subscription subscription : subscriptions) {
         Set<Term> terms = subscriptionCAO.getTerms(subscription.getId());
         Person subscriber = CommunityModuleAdapter.getUserById(subscription.getSubscriberId());
         if(subscriber == null || RegisterStatus.BLOCKED.getName().equalsIgnoreCase(subscriber.getActive())) {
            continue;
         }
         subscription.setEmail(subscriber.getEmail());
         subscription.setTerms(terms);
         try {
            publisher.deliver(publication, subscription);
            sendSuccess.add(subscription.getSubscriberId());
         } catch (NewsletterSendFailException e) {
            sendFails.add(subscription.getSubscriberId());
            log.error(e.getMessage());
         }
      }
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      Node edition = cloud.getNode(publicationId);  
      if(!sendSuccess.isEmpty()){
         edition.setDateValue("sendtime", new java.util.Date());
      }
      NewsletterPublicationUtil.setIsSent(edition);
      sendResults.put(SEND_SUCCESS, sendSuccess);
      sendResults.put(SEND_FAIL, sendFails);

      publicationCAO.setStatus(publicationId, STATUS.DELIVERED);
//      publicationCAO.renamePublicationTitle(publicationId);
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

   public Set<Publication> getPublicationByNewsletter(int newsletterId) {
      Set<Publication> result = new HashSet<Publication>();

      for (Publication publication : publicationCAO.getPublicationsByNewsletter(newsletterId, null)) {
         result.add(publication);
      }
      return result;
   }

   public List<Publication> searchPublication(int newsletterId, String title, String subject, Date startTime, Date endTime, boolean paging) {
      return publicationCAO.getPublicationsByNewsletterAndPeriod(newsletterId, title, subject, startTime, endTime, paging);
   }

   public List<Publication> searchPublication(String title, String subject,
                                              String description, String intro, boolean paging) {
      return publicationCAO.getPublications(title, subject, description, intro, paging);
   }

}