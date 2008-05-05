package com.finalist.newsletter.cao;

import java.util.List;
import java.util.Set;
import java.util.Collection;

import com.finalist.newsletter.domain.Publication;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.Term;

public interface NewsletterPublicationCAO {
   public List<Integer> getIntimePublicationIds();

   public void setStatus(int publicationId, Publication.STATUS status);

   public Publication getPublication(int number);

   public int getNewsletterId(int publicationId);

   public List<Publication> getAllPublications();

   public List<Publication> getPublicationsByNewsletter(int id, Publication.STATUS status);

   public Set<Term> getTermsByPublication(int publicationId);
}
