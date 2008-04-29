package com.finalist.newsletter.cao;

import java.util.List;
import java.util.Set;

import com.finalist.newsletter.domain.Publication;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.Term;

public interface NewsletterPublicationCAO {
   public List<Integer> getIntimePublicationIds();
   public void setStatus(int publicationId,Publication.STATUS status);
   public Publication getPublication(int number);
   public int getNewsletterId(int publicationId);
}
   