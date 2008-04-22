package com.finalist.newsletter.publisher;

import com.finalist.cmsc.services.community.person.Person;
import com.finalist.newsletter.domain.Publication;
import com.finalist.newsletter.domain.Subscription;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class FakeNewsletterPublisher extends NewsletterPublisher{

   private Map<Integer,List<Person>> massDelivers = new HashMap<Integer,List<Person>>();
   public Publication publication;
   public Subscription subscription;

   public FakeNewsletterPublisher(){
      
   }

   public void deliver(int publicationId, List<Person> persons) {
      massDelivers.put(publicationId,persons);
   }

   public  Map<Integer,List<Person>> getMassDelivers() {
      return massDelivers;
   }

   public void deliver(Publication publication, Subscription subscription) {
   this.publication = publication;
   this.subscription = subscription;
   }
}
