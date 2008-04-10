package com.finalist.newsletter.services.impl;

import com.finalist.newsletter.services.NewsletterPublicationService;
import com.finalist.newsletter.publisher.NewsletterPublisher;
import com.finalist.newsletter.cao.NewsletterPublicationCAO;
import com.finalist.newsletter.cao.NewsletterSubscriptionCAO;
import com.finalist.newsletter.cao.NewsLetterStatisticCAO;
import com.finalist.newsletter.domain.Publication;
import static com.finalist.newsletter.domain.Publication.STATUS;
import com.finalist.cmsc.services.community.person.Person;

import java.util.Date;
import java.util.List;

public class NewsletterPublicationServiceImpl implements NewsletterPublicationService {

   private NewsletterPublisher publisher;
   private NewsletterPublicationCAO publicationCAO;
   private NewsletterSubscriptionCAO subscriptionCAO;
   private NewsLetterStatisticCAO statisticCAO;

   public void setMailSender(NewsletterPublisher publisher) {
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

   public void deliverAllPublication() {
      List<Publication> publications = publicationCAO.getIntimePublication(new Date(System.currentTimeMillis()));
      for(Publication publication:publications){
         List<Person> persons = subscriptionCAO.getSubscribers(publication.getNewsletterId());
         publisher.deliver(publication.getId(),persons);
         statisticCAO.logPubliction(publication.getId(),persons.size());
         publicationCAO.setStatus(publication, STATUS.DELIVERED);
      }


   }
}
