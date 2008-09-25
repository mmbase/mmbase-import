package com.finalist.newsletter.services.mock;

import java.util.ArrayList;
import java.util.List;

import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.services.NewsletterService;

public class FakeNewsletterService {

	public List<Newsletter> getAllNewsletter() {
		List<Newsletter> letters = new ArrayList<Newsletter>();
		Newsletter l1 = new Newsletter();
		return letters;
	}

	public String getNewsletterName(int newsletterId) {
		return null;
	}

}
