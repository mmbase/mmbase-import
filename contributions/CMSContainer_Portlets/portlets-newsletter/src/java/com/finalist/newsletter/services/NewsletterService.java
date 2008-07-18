package com.finalist.newsletter.services;

import java.util.List;
import java.util.Set;

import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.Term;

public interface NewsletterService {

   public List<Newsletter> getAllNewsletter();

   public String getNewsletterName(String newsletterId);

   public int countAllNewsletters();

   public int countAllTerms();

   public List<Newsletter> getNewslettersByTitle(String title);

   public Newsletter getNewsletterBySubscription(int id);

   public List<Newsletter> getNewsletters(String subscriber, String title);

   void processBouncesOfPublication(String publicationId,String userId);
   
   public Set<Term> getNewsletterTermsByName(int newsletterId, String name, int pagesize, int offset);
}
