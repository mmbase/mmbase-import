package com.finalist.newsletter.services.impl;

import java.util.List;

import net.sf.mmapps.commons.util.StringUtil;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.services.community.person.Person;
import com.finalist.newsletter.cao.NewsLetterStatisticCAO;
import com.finalist.newsletter.cao.NewsletterPublicationCAO;
import com.finalist.newsletter.cao.NewsletterSubscriptionCAO;
import com.finalist.newsletter.domain.Publication;
import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.domain.Publication.STATUS;
import com.finalist.newsletter.publisher.NewsletterPublisher;
import com.finalist.newsletter.services.NewsletterPublicationService;

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
      Person person = new Person();
      person.setEmail(email);

      if(StringUtil.isEmptyOrWhitespace(PropertiesUtil.getProperty("mail.system.email"))) {
         subscription.setFromAddress("test@finalist.com");
      }
      else {         
         subscription.setFromAddress(PropertiesUtil.getProperty("mail.system.email"));
      }
      if(StringUtil.isEmptyOrWhitespace(PropertiesUtil.getProperty("mail.system.name"))) {
         subscription.setFromName("test to send publication");
      }
      else {         
         subscription.setFromName(PropertiesUtil.getProperty("mail.system.name"));
      }
      
      if(StringUtil.isEmptyOrWhitespace(PropertiesUtil.getProperty("mail.system.email"))) {
         subscription.setReplyAddress("test@finalist.com");
      }
      else {         
         subscription.setReplyAddress(PropertiesUtil.getProperty("mail.system.email"));
      }
      if(StringUtil.isEmptyOrWhitespace(PropertiesUtil.getProperty("mail.system.name"))) {
         subscription.setReplyname("test sending publication");
      }
      else {         
         subscription.setReplyname(PropertiesUtil.getProperty("mail.system.name"));
      }
      subscription.setSubscriber(person);
      subscription.setTitle("test to send publication");
      publisher.deliver(publication, subscription);
   }

   public void deliverPublication(int number) {
      Publication publication = publicationCAO.getPublication(number);
      deliver(publication);
   }
}
