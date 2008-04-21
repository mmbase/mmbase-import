package com.finalist.newsletter.services.impl;

import java.util.List;

import com.finalist.newsletter.cao.NewsletterCAO;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.services.NewsletterService;

public class NewsletterServiceImpl implements NewsletterService {

	NewsletterCAO newsletterCAO;

	public NewsletterServiceImpl() {

	}

	public NewsletterServiceImpl(NewsletterCAO newsletterCAO) {

		this.newsletterCAO = newsletterCAO;
	}

	public void setNewsletterCAO (NewsletterCAO newsletterCAO){

		this.newsletterCAO = newsletterCAO;
	}

	public List<Newsletter> getAllNewsletter (){

		return newsletterCAO.getAllNewsletters();
	}

	public String getNewsletterName (int newsletterId){

		return newsletterCAO.getNewsletterById(newsletterId).getTitle();
	}

}
