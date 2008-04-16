package com.finalist.newsletter.cao;

import com.finalist.newsletter.domain.Publication;

import java.util.List;
import java.util.Date;

public interface NewsletterPublicationCAO {
   public List<Publication> getIntimePublication();
   public void setStatus(Publication publication,Publication.STATUS status);
   public Publication getPublication(int number);
}
   