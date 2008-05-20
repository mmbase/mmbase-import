package com.finalist.newsletter.services.mock;

import java.util.List;

import com.finalist.newsletter.cao.NewsletterCAO;
import com.finalist.newsletter.domain.Newsletter;

public class FakeNewsletterCAO {

	public Newsletter getNewsletterById(int id) {
		Newsletter letter = new Newsletter();
		String title = "";
		switch (id) {
			case 1:
				title = "r1";
				break;
			case 2:
				title = "r2";
			default:
				break;
		}
		
		letter.setTitle(title);
		return letter;
	}

	public List<Newsletter> getAllNewsletters() {
		// TODO Auto-generated method stub
		return null;
	}

}
