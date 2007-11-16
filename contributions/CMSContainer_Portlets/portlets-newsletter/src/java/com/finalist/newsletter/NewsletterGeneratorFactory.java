package com.finalist.newsletter;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.newsletter.util.NewsletterSubscriptionUtil;

public class NewsletterGeneratorFactory {

	private static Logger log = Logging.getLoggerInstance(NewsletterPublisher.class.getName());
	private static NewsletterGeneratorFactory instance;

	private NewsletterGeneratorFactory() {

	}

	public static NewsletterGeneratorFactory getInstance() {
	         if (instance==null) {
	                  instance = new NewsletterGeneratorFactory();
	         }
	return instance;
	}

	public NewsletterGenerator getNewsletterGenerator(String publicationNumber, String mimeType) {
		if (mimeType.equals(NewsletterSubscriptionUtil.MIMETYPE_HTML)) {
			return (new NewsletterGeneratorHtml(publicationNumber));
		} else if (mimeType.equals(NewsletterSubscriptionUtil.MIMETYPE_PLAIN)) {
			return (new NewsletterGeneratorPlain(publicationNumber));
		} else {
			log.debug("No NewsletterGenerator returned because of unsupported mimetype");
			return (null);
		}
	}
}