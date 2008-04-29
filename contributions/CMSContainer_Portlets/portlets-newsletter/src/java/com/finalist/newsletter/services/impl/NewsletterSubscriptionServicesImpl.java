package com.finalist.newsletter.services.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Node;

import com.finalist.newsletter.cao.NewsletterCAO;
import com.finalist.newsletter.cao.NewsletterSubscriptionCAO;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.domain.Term;
import com.finalist.newsletter.services.NewsletterSubscriptionServices;

public class NewsletterSubscriptionServicesImpl implements NewsletterSubscriptionServices{
	
	NewsletterSubscriptionCAO cao;
	NewsletterCAO newsletterCAO;
	
	private static Log log = LogFactory
	.getLog(NewsletterSubscriptionServicesImpl.class);
									
	public void setCao(NewsletterSubscriptionCAO cao) {
		this.cao = cao;
		
	}
	public void setNewsletterCao(NewsletterCAO newsletterCAO){
		this.newsletterCAO = newsletterCAO;
	}
	
	public List<Subscription> getSubscriptionList(String[] allowedLetters,int userId){
		
	    List<Subscription> subscriptionList = new ArrayList<Subscription>();
		int newsletterId;
		for(int i=0;i<allowedLetters.length;i++)
		{
			newsletterId = Integer.parseInt(allowedLetters[i]);
			subscriptionList.add(addRecordInfo(newsletterId,userId));
		}	
		return subscriptionList;
	}
	
	public Subscription addRecordInfo(int newsletterId, int userId) {
	
		Subscription subscription = cao.getSubscription(newsletterId,userId);
		Newsletter newsletter = newsletterCAO.getNewsletterById(newsletterId);
		if(subscription==null){
			Subscription newSubscription = new Subscription();
			newSubscription.setNewsletter(newsletter);
			newSubscription.setTerms(newsletter.getTerms());
			return newSubscription;
		}else{
			Set<Term> newsletterTerms = newsletter.getTerms();
			Set<Term> subscriptionTerms = subscription.getTerms();
			Iterator newsletterTermList = newsletterTerms.iterator();
			Iterator selectListIt = subscriptionTerms.iterator();
			for(int i=0;i<subscriptionTerms.size();i++)
			{
				Term term = (Term) newsletterTermList.next();
				if(subscriptionTerms.size()==0){
					subscriptionTerms.add(term);
				}else{
					if(subscriptionTerms.contains(term)){
						continue;
					}else{
						subscriptionTerms.add(term);
					}			
				}	
			}			
			subscription.setNewsletter(newsletter);
			return subscription;
		}	
	}
	
	public List<Subscription> getNewSubscription(String[] allowedLetters)
	{
		List<Subscription> list = new ArrayList<Subscription>();
		int nodenumber;
		for(int i=0;i<allowedLetters.length;i++)
		{
			nodenumber = Integer.parseInt(allowedLetters[i]);
			Newsletter newsletter = newsletterCAO.getNewsletterById(nodenumber);
			Subscription subscription = new Subscription();
			subscription.setNewsletter(newsletter);
			Newsletter test = subscription.getNewsletter();
			subscription.setTerms(newsletter.getTerms());
			list.add(subscription);
		}
		return list;
	}

	public boolean hasSubscription(int userId) {
		
		List<Node> list = cao.querySubcriptionByUser(userId);
		if(0==list.size()){
		return false;
		}
		else
		{
		return true;
		}
	}

	public void selectTermInLetter(int userId, int newsletterId, int termId) {
		Subscription subscription = cao.getSubscription(newsletterId, userId);
		Set<Term> termList = subscription.getTerms();
		Iterator it = termList.iterator();
		for(int i=0;i<termList.size();i++){
			Term term = (Term)it.next();
			if(termId==term.getId()){
				term.setSubscription(true);
			}
		}
		cao.addSubscriptionTerm(subscription,termId);
		
	}
	
	public void unSelectTermInLetter(int userId, int newsletterId, int termId) {
		Subscription subscription = cao.getSubscription(newsletterId, userId);
		Set<Term> termList = subscription.getTerms();
		Iterator it = termList.iterator();
		for(int i=0;i<termList.size();i++){
			Term term = (Term)it.next();
			if(termId==term.getId()){
				term.setSubscription(false);
			}
		}
		cao.removeSubscriptionTerm(subscription,termId);
	}

   public void modifyStauts(int userId, int newsletterId, String status) {
      log.debug("user " + userId + " change subscription status of newsletter " + newsletterId + " to " + status);
      Subscription subscription = cao.getSubscription(newsletterId, userId);
      subscription.setStatus(Subscription.STATUS.valueOf(status));
      cao.modifySubscriptionStauts(subscription);
   }

   public void modifyFormat(int userId, int newsletterId, String format) {
		Subscription subscription = cao.getSubscription(newsletterId, userId);
		subscription.setMimeType(format);
		cao.modifySubscriptionFormat(subscription);		
	}

	public boolean noSubscriptionRecord(int userId, int newsletterId) {
      Subscription subscription = cao.getSubscription(newsletterId, userId);
      return subscription == null;
	}

	public void addNewRecord(int userId, int newsletterId) {
		Subscription subscription = new Subscription();
		Newsletter newsletter = new Newsletter();
		newsletter.setId(newsletterId);
		subscription.setNewsletter(newsletter);
		subscription.setMimeType("html");
		subscription.setStatus(Subscription.STATUS.ACTIVE);
		cao.addSubscriptionRecord(subscription,userId);	
	}


	
	
}
