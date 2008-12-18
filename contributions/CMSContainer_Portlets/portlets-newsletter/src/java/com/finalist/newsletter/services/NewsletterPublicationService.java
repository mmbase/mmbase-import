package com.finalist.newsletter.services;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.finalist.newsletter.domain.Publication;

public interface NewsletterPublicationService {

   public static final String SEND_SUCCESS = "sendSucess";
   public static final String SEND_FAIL = "sendFail";

   public void deliverAllPublications();

   public void deliver(int publicationId, String email, String mimeType);

   public Publication.STATUS getStatus(int publicationId);

   public void setStatus(int publciationId, Publication.STATUS status);

   public Map<String, List<String>> deliver(int publidcation);

   public int countAllPublications();

   public int countPublicationByNewsletter(int id);

   public int countSentPublications(int id);

   public Set<Publication> getPublicationByNewsletter(int id);

   public List<Publication> searchPublication(int newsletterId, String title, String subject, Date startDate, Date endDate, boolean paging);

   public List<Publication> searchPublication(String title, String subject, String description, String intro, boolean paging);
   
}
