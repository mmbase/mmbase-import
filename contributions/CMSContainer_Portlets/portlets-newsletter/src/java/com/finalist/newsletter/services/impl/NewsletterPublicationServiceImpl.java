package com.finalist.newsletter.services.impl;

import com.finalist.cmsc.services.community.person.Person;
import com.finalist.newsletter.cao.NewsLetterStatisticCAO;
import com.finalist.newsletter.cao.NewsletterPublicationCAO;
import com.finalist.newsletter.cao.NewsletterSubscriptionCAO;
import com.finalist.newsletter.domain.Publication;
import com.finalist.newsletter.domain.Publication.STATUS;
import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.publisher.NewsletterPublisher;
import com.finalist.newsletter.services.NewsletterPublicationService;

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
      List<Publication> publications = publicationCAO.getIntimePublication();
      for(Publication publication:publications){
         deliver(publication);
      }
   }

   private void deliver(Publication publication) {
      List<Subscription> subscriptions = subscriptionCAO.getSubscription(publication.getNewsletter().getId());
      publisher.deliver(publication,subscriptions);
      statisticCAO.logPubliction(publication.getId(),subscriptions.size());
      publicationCAO.setStatus(publication, STATUS.DELIVERED);
   }

   public void deliver(int number,String email,String mimeType) {

      Publication publication = publicationCAO.getPublication(number);
      Subscription subscription = new Subscription();

      subscription.setMimeType(mimeType);
      System.out.println("------------------------"+publication.getNewsletter());
      subscription.setNewsletter(publication.getNewsletter());

      Person person = new Person();
      person.setEmail(email);
      subscription.setSubscriber(person);

      publisher.deliver(publication, subscription);
   }
   public void deliverPublication(int number) {
      Publication publication = publicationCAO.getPublication(number);
      deliver(publication);
   }
}
