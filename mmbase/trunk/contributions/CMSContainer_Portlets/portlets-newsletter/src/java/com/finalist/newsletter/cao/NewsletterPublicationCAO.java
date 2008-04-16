package com.finalist.newsletter.cao;

import java.util.List;

import com.finalist.newsletter.domain.Publication;

public interface NewsletterPublicationCAO {
   public List<Publication> getIntimePublication();
   public void setStatus(Publication publication,Publication.STATUS status);
   public Publication getPublication(int number);
}
   