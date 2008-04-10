package com.finalist.newsletter.cao;

import com.finalist.newsletter.domain.Publication;

import java.util.List;
import java.util.Date;

public interface NewsletterPublicationCAO {
   public List<Publication> getIntimePublication(Date date);
   public void setStatus(Publication publication,Publication.STATUS status);
}
   