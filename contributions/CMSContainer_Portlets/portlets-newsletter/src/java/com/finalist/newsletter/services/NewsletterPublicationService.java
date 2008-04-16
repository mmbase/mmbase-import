package com.finalist.newsletter.services;


public interface NewsletterPublicationService {
   public void deliverAllPublication();

   public void deliver(int number,String email,String mimeType);
   public void deliverPublication(int number);
}
