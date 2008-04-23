package com.finalist.newsletter.services.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.newsletter.cao.NewsletterCAO;
import com.finalist.newsletter.cao.NewsletterSubscriptionCAO;
import com.finalist.newsletter.cao.impl.NewsletterCAOImpl;
import com.finalist.newsletter.cao.impl.NewsletterSubscriptionCAOImpl;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.domain.Tag;
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
			newSubscription.setTags(newsletter.getTags());	
			return newSubscription;
		}else{
			Set<Tag> newsletterTags = newsletter.getTags();
			Set<Tag> subscriptionTags = subscription.getTags();
			Iterator newsletterTagListIt = newsletterTags.iterator();
			Iterator selectListIt = subscriptionTags.iterator();
			for(int i=0;i<newsletterTags.size();i++)
			{
				Tag tag = (Tag) newsletterTagListIt.next();		
				if(subscriptionTags.size()==0){
					subscriptionTags.add(tag);
				}else{
					if(subscriptionTags.contains(tag)){
						continue;
					}else{
						subscriptionTags.add(tag);
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
			subscription.setTags(newsletter.getTags());	
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

	public void selectTagInLetter(int userId, int newsletterId, int tagId) {
		Subscription subscription = cao.getSubscription(newsletterId, userId);
		Set<Tag> tagList = subscription.getTags();
		Iterator it = tagList.iterator();
		for(int i=0;i<tagList.size();i++){
			Tag tag = (Tag)it.next();
			if(tagId==tag.getId()){
				tag.setSubscription(true);
			}
		}
		cao.addSubscriptionTag(subscription,tagId);
		
	}
	
	public void unSelectTagInLetter(int userId, int newsletterId, int tagId) {
		Subscription subscription = cao.getSubscription(newsletterId, userId);
		Set<Tag> tagList = subscription.getTags();
		Iterator it = tagList.iterator();
		for(int i=0;i<tagList.size();i++){
			Tag tag = (Tag)it.next();
			if(tagId==tag.getId()){
				tag.setSubscription(false);
			}
		}
		cao.removeSubscriptionTag(subscription,tagId);
	}

	public void modifyStauts(int userId, int newsletterId, String status) {
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
		if(cao.getSubscription(newsletterId, userId)==null){
			return true;
		}else{
			return false;
		}
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
