package com.finalist.newsletter.cao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.finalist.newsletter.domain.Publication;
import com.finalist.newsletter.domain.Term;
import org.mmbase.bridge.Node;

public interface NewsletterPublicationCAO {
   public List<Integer> getIntimePublicationIds();

   public void setStatus(int publicationId, Publication.STATUS status);

   public Publication getPublication(int number);

   public Node getPublicationNode(int number);

   public int getNewsletterId(int publicationId);

   public List<Publication> getAllPublications();

   public List<Publication> getPublicationsByNewsletter(int id, Publication.STATUS status);

   public Set<Term> getTermsByPublication(int publicationId);

   public void renamePublicationTitle(int publicationId);

   public List<Publication> getPublicationsByNewsletterAndPeriod(int id, String title, String subject, Date startDate, Date endDate, boolean paging);

   public List<Publication> getPublications(String title, String subject, String description, String intro, boolean paging);

}
