package com.finalist.newsletter.services;

import java.util.List;
import java.util.Map;

import com.finalist.newsletter.domain.Publication;


public interface NewsletterPublicationService {
   
   public static final String SEND_SUCCESS = "sendSucess";
   public static final String SEND_FAIL = "sendFail";
   
   public void deliverAllPublication();

   public void deliver(int publicationId, String email, String mimeType);

   public Publication.STATUS getStatus(int publicationId);

   public void setStatus(int publciationId, Publication.STATUS status);

   public Map<String,List<String>> deliver(int publidcation);

   public int countAllPublications();

   public int countPublicationByNewsletter(int id);

   public int countSentPublications(int id);
}
