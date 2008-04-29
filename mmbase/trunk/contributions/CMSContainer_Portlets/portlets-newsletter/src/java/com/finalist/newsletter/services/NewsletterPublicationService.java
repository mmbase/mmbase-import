package com.finalist.newsletter.services;

import com.finalist.newsletter.domain.Publication;
import com.finalist.newsletter.domain.Term;

import java.util.Set;


public interface NewsletterPublicationService {
   public void deliverAllPublication();

   public void deliver(int publicationId, String email, String mimeType);

   public Publication.STATUS getStatus(int publicationId);

   public void setStatus(int publciationId, Publication.STATUS status);

   public void deliver(int publidcation);
}
