package com.finalist.newsletter.services;

import java.util.List;

import com.finalist.newsletter.domain.Newsletter;

public interface NewsletterService {

   public List<Newsletter> getAllNewsletter();

   public String getNewsletterName(int newsletterId);

   public int countAllNewsletters();

   public int countAllTerms();

   public List<Newsletter> getNewslettersByTitle(String title);

   public Newsletter getNewsletterBySubscription(int id);
}
