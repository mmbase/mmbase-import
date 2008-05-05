package com.finalist.newsletter.services.impl;

import java.util.List;

import com.finalist.newsletter.cao.NewsletterCAO;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.services.NewsletterService;
import org.mmbase.bridge.NodeManager;

public class NewsletterServiceImpl implements NewsletterService {

   NewsletterCAO newsletterCAO;

   public NewsletterServiceImpl() {

   }

   public NewsletterServiceImpl(NewsletterCAO newsletterCAO) {

      this.newsletterCAO = newsletterCAO;
   }

   public void setNewsletterCAO(NewsletterCAO newsletterCAO) {

      this.newsletterCAO = newsletterCAO;
   }

   public List<Newsletter> getAllNewsletter() {

      return newsletterCAO.getNewsletterByConstraint(null, null, null);
   }

   public String getNewsletterName(int newsletterId) {

      return newsletterCAO.getNewsletterById(newsletterId).getTitle();
   }

   public int countAllNewsletters() {
      return getAllNewsletter().size();
   }

   public int countAllTerms() {
      return newsletterCAO.getALLTerm().size();
   }

   public List<Newsletter> getNewslettersByTitle(String title) {
      return newsletterCAO.getNewsletterByConstraint("title", "like", title);
   }

   public Newsletter getNewsletterBySubscription(int id) {
       int newsletterId = newsletterCAO.getNewsletterIdBySubscription(id);
       return newsletterCAO.getNewsletterById(newsletterId);
   }


}
