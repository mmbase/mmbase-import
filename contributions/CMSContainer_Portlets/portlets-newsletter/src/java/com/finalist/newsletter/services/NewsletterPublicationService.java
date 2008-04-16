package com.finalist.newsletter.services;

import com.finalist.newsletter.domain.Publication;
import com.finalist.newsletter.publisher.NewsletterPublisher;

import java.util.List;

public interface NewsletterPublicationService {
   public void deliverAllPublication();

   public void testDeliver(int number,String email,String mineType);
}
