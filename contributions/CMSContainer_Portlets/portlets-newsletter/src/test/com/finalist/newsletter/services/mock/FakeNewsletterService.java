package com.finalist.newsletter.services.mock;

import java.util.ArrayList;
import java.util.List;

import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.services.NewsletterService;

public class FakeNewsletterService implements NewsletterService{

	public List<Newsletter> getAllNewsletter() {
		List<Newsletter> letters = new ArrayList<Newsletter>();
		Newsletter l1 = new Newsletter();
		l1.setNumber(123);
		l1.setTitle("123");
		
		Newsletter l2 = new Newsletter();
		l2.setNumber(123);
		l2.setTitle("123");
		
		Newsletter l3 = new Newsletter();
		l2.setNumber(999);
		l2.setTitle("ALL");
		
		letters.add(l1);
		letters.add(l2);
		letters.add(l3);
		return letters;
	}

   public String getNewsletterName(int newsletterId) {
      return null;
   }

}
