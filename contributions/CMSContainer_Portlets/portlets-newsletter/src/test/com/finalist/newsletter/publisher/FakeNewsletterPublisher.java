package com.finalist.newsletter.publisher;

import com.finalist.cmsc.services.community.person.Person;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class FakeNewsletterPublisher extends NewsletterPublisher{

   private Map<Integer,List<Person>> map = new HashMap<Integer,List<Person>>();

   public FakeNewsletterPublisher(){
      
   }

   public void deliver(int publicationId, List<Person> persons) {
      map.put(publicationId,persons);
   }

   public  Map<Integer,List<Person>> getMap() {
      return map;
   }
}
