package com.finalist.newsletter.services;

import java.util.List;

import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.Term;

public interface NewsletterService {

   public List<Newsletter> getAllNewsletter(boolean paging);

   public String getNewsletterName(int newsletterId);

   public int countAllTerms();

   public List<Newsletter> getNewslettersByTitle(String title, boolean paging);

   public Newsletter getNewsletterBySubscription(int id);

   public void processBouncesOfPublication(String publicationId, String userId, String bounceContent);

   public List<Term> getNewsletterTermsByName(int newsletterId, String name, boolean paging);

   public List<Newsletter> getNewsletters(String subscriber, String title, boolean paging);

   void processBouncesOfPublication(String publicationId, String userId);

}
