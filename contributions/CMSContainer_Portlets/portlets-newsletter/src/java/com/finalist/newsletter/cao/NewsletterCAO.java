package com.finalist.newsletter.cao;

import com.finalist.newsletter.domain.Newsletter;

import java.util.List;

public interface NewsletterCAO {
   public Newsletter getNewsletterById(int id);

   public List<Newsletter> getAllNewsletters();
}
