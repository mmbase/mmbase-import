package com.finalist.newsletter.services;

import java.util.List;

import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.Term;

public interface NewsletterService {

	public List<Newsletter> getAllNewsletter();

	public String getNewsletterName(String newsletterId);

	public int countAllNewsletters();

	public int countAllTerms();

	public List<Newsletter> getNewslettersByTitle(String title);

	public Newsletter getNewsletterBySubscription(int id);

	public int getNewsletterTermsCountByName(int newsletterId, String terms);

   public void processBouncesOfPublication(String publicationId,String userId,String bounceContent);
   
	public List<Term> getNewsletterTermsByName(int newsletterId, String name, int pagesize, int offset, String order, String direction);

	public List<Newsletter> getNewsletters(String subscriber, String title);

	void processBouncesOfPublication(String publicationId, String userId);

}
