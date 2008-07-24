package com.finalist.newsletter.services;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
   
   public Set<Publication> getPublicationByNewsletter(int id);
   
   public List<Publication> searchPublication(int id , String title, String subject, Date startDate, Date endDate, int pagesize, int offset, String order, String direction);
   
   public int searchPublicationCountForEdit(int id, String title, String subject, Date startDate, Date endDate);
   
   public List<Publication> searchPublicationStatistics(int newsletterId, String title,String subject, Date startTime, Date endTime, int pagesize, int offset, String order, String direction);
}
