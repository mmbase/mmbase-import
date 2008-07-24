package com.finalist.newsletter.services.impl;

import com.finalist.newsletter.cao.NewsLetterStatisticCAO;
import com.finalist.newsletter.cao.NewsletterCAO;
import com.finalist.newsletter.cao.NewsletterPublicationCAO;
import com.finalist.newsletter.cao.NewsletterSubscriptionCAO;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.StatisticResult;
import com.finalist.newsletter.domain.Term;
import com.finalist.newsletter.services.NewsletterService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Node;

import java.util.List;
import java.util.Set;

public class NewsletterServiceImpl implements NewsletterService {

	private static Log log = LogFactory.getLog(NewsletterServiceImpl.class);

	NewsletterCAO newsletterCAO;
	NewsletterSubscriptionCAO subscriptionCAO;
	NewsletterPublicationCAO publicationCAO;
	NewsLetterStatisticCAO statisticCAO;

	public void setNewsletterCAO(NewsletterCAO newsletterCAO) {
		this.newsletterCAO = newsletterCAO;
	}

	public void setSubscriptionCAO(NewsletterSubscriptionCAO subscriptionCAO) {
		this.subscriptionCAO = subscriptionCAO;
	}

	public void setPublicationCAO(NewsletterPublicationCAO publicationCAO) {
		this.publicationCAO = publicationCAO;
	}

	public void setStatisticCAO(NewsLetterStatisticCAO statisticCAO) {
		this.statisticCAO = statisticCAO;
	}

	public List<Newsletter> getAllNewsletter() {
		return newsletterCAO.getNewsletterByConstraint(null, null, null);
	}

	public String getNewsletterName(String newsletterId) {
		String name = "";

		if (StringUtils.isNotBlank(newsletterId)) {
			name = newsletterCAO.getNewsletterById(Integer.parseInt(newsletterId)).getTitle();
		}

		return name;
	}

	public int countAllNewsletters() {
		return getAllNewsletter().size();
	}

	public int countAllTerms() {
		return newsletterCAO.getALLTerm().size();
	}

	public List<Newsletter> getNewslettersByTitle(String title) {

		log.debug(String.format("Get newsletter by title %s", title));

		return newsletterCAO.getNewsletterByConstraint("title", "like", title);
	}

	public Newsletter getNewsletterBySubscription(int id) {
		int newsletterId = newsletterCAO.getNewsletterIdBySubscription(id);
		if (newsletterId < 1) {
			return null;
		}
		return newsletterCAO.getNewsletterById(newsletterId);
	}

	public List<Newsletter> getNewsletters(String subscriber, String title) {

		log.debug(String.format("Get Newsletters by subscriber %s and title %s", subscriber, title));

		boolean sc = StringUtils.isNotBlank(subscriber);
		boolean tc = StringUtils.isNotBlank(title);

		if (sc && tc) {
			return getAllNewsletterBySubscriberAndTitle(subscriber, title);
		} else if (sc && !tc) {
			return getAllNewsletterBySubscriber(subscriber);
		} else if (tc) {
			return getNewslettersByTitle(title);
		} else {
			return getAllNewsletter();
		}
	}

   public void processBouncesOfPublication(String publicationId,String userId) {
      //todo test.
      int pId = Integer.parseInt(publicationId);
      int uId = Integer.parseInt(userId);
      int newsletterId = publicationCAO.getNewsletterId(pId);
      Node newsletterNode = newsletterCAO.getNewsletterNodeById(newsletterId);
      Node subscriptionNode =  subscriptionCAO.getSubscriptionNode(newsletterId,uId);
      int bouncesCount = subscriptionNode.getIntValue("count_bounces");
      int maxAllowedBonce = newsletterNode.getIntValue("max_bounces");
      
      if(bouncesCount > maxAllowedBonce){
         subscriptionCAO.pause(subscriptionNode.getNumber());
      }
      statisticCAO.logPubliction(uId,newsletterId, StatisticResult.HANDLE.BOUNCE);
      subscriptionCAO.updateLastBounce(subscriptionNode.getNumber());
   }

	private List<Newsletter> getAllNewsletterBySubscriber(String subscriber) {
		return null;
	}

	private List<Newsletter> getAllNewsletterBySubscriberAndTitle(String subscriber, String title) {
		return null;
	}

	public List<Term> getNewsletterTermsByName(int newsletterId, String name, int pagesize, int offset, String order, String direction) {
		List<Term> terms = newsletterCAO.getNewsletterTermsByName(newsletterId, name, pagesize, offset, order, direction);
		return terms;
	}

	public int getNewsletterTermsCountByName(int newsletterId, String tmpName) {
		int resultCount = newsletterCAO.getNewsletterTermsCountByName(newsletterId, tmpName);
		return resultCount;
	}

   public Set<Term> getNewsletterTermsByName(int newsletterId, String name,
         int pagesize, int offset) {
      // TODO Auto-generated method stub
      return null;
   }

   public void processBouncesOfPublication(String publicationId, String userId,
         String bounceContent) {
      newsletterCAO.processBouncesOfPublication(publicationId, userId, bounceContent);
   }

}
