package com.finalist.newsletter.services.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.newsletter.cao.NewsletterCAO;
import com.finalist.newsletter.cao.NewsletterSubscriptionCAO;
import com.finalist.newsletter.cao.impl.NewsletterSubscriptionCAOImpl;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.Tag;
import com.finalist.newsletter.services.NewsletterSubscriptionServices;

public class NewsletterSubscriptionServicesImpl implements NewsletterSubscriptionServices{
	
	NewsletterSubscriptionCAO cao;
	NewsletterCAO newsletterCAO;
										
	public void setCao(NewsletterSubscriptionCAO cao) {
		this.cao = new NewsletterSubscriptionCAOImpl();
	}
	
	public List<Newsletter> getNewsletterList(String[] allowedLetters,int userId){
		List<Newsletter> allowedLetterList = getAllowedNewsletterList(allowedLetters);
		List<Newsletter> recordList = cao.getUserSubscriptionList(userId);
		List<Newsletter> list = new ArrayList<Newsletter>();
		Newsletter allowedNewsletter = new Newsletter();
		Iterator it = allowedLetterList.iterator();
		for(int i=0;i<allowedLetterList.size();i++)
		{
			allowedNewsletter = (Newsletter) it.next();
			allowedNewsletter = addRecordInfo(allowedNewsletter,recordList);
			list.add(allowedNewsletter);
		}	
		return list;
	}
	
	public Newsletter addRecordInfo(Newsletter allowedNewsletter, List<Newsletter> recordList) {

//
//		Iterator it = recordList.iterator();
//		for(int i=0;i<recordList.size();i++)
//		{
//			Newsletter recordNewsletter = (Newsletter) it.next();
//			if(allowedNewsletter.getTitle().equals(recordNewsletter.getTitle()))
//			{
//				newsletter.setFormat(recordNewsletter.getFormat());
//				newsletter.setInterval(recordNewsletter.getInterval());
//				newsletter.setStatus(recordNewsletter.getStatus());
//				newsletter.setTags(recordNewsletter.getTags());
//			}
//		}
		return null;
	}
	public Newsletter addRecordInfo(Newsletter allowedNewsletter, int userId) {
//
//		cao.getSubscriptionrecord(newsletterId, userId)
//		Iterator it = recordList.iterator();
//		for(int i=0;i<recordList.size();i++)
//		{
//			Newsletter recordNewsletter = (Newsletter) it.next();
//			if(allowedNewsletter.getTitle().equals(recordNewsletter.getTitle()))
//			{
//				newsletter.setFormat(recordNewsletter.getFormat());
//				newsletter.setInterval(recordNewsletter.getInterval());
//				newsletter.setStatus(recordNewsletter.getStatus());
//				newsletter.setTags(recordNewsletter.getTags());
//			}
//		}
		return null;
	}
	public List<Newsletter> getAllowedNewsletterList(String[] allowedLetters)
	{
		List<Newsletter> list = new ArrayList<Newsletter>();
		cao = new NewsletterSubscriptionCAOImpl();
		int nodenumber;
		for(int i=0;i<allowedLetters.length;i++)
		{
			nodenumber = Integer.parseInt(allowedLetters[i]);
			Newsletter newsletter = cao.getNewsletterById(nodenumber);
	    	list.add(newsletter);
		}
		return list;
	}
	
	public List<Newsletter> getAllNewsletterListUsed()
	{
		List<Newsletter> list = new ArrayList();
		list = cao.getAllNewsletter();
		return list;
	}

	public boolean hasSubscription(int userId) {
		
		NewsletterSubscriptionCAO cao = new NewsletterSubscriptionCAOImpl();
		System.out.println("userId="+userId);
		List<Node> list = cao.querySubcriptionByUser(userId);
		System.out.println("list="+list.size());
		if(0==list.size()){
		return false;
		}
		else
		{
			System.out.println("@@@@@@@@");
		return true;
		}
	}

	public void selectTagInLetter(int userId, int newsletterId, int tagId) {
		Newsletter newsletter = cao.getNewsletterById(newsletterId);
		Set<Tag> tagList = newsletter.getTags();
		Iterator it = tagList.iterator();
		for(int i=0;i<tagList.size();i++){
			Tag tag = (Tag)it.next();
			if(tagId==tag.getId()){
				tag.setSubscription(true);
			}
		}
		cao.addSubscriptionTag(newsletter, userId, tagId);
		
	}
	
	public void unSelectTagInLetter(int userId, int newsletterId, int tagId) {
		Newsletter newsletter = cao.getNewsletterById(newsletterId);
		Set<Tag> tagList = newsletter.getTags();
		Iterator it = tagList.iterator();
		for(int i=0;i<tagList.size();i++){
			Tag tag = (Tag)it.next();
			if(tagId==tag.getId()){
				tag.setSubscription(false);
			}
		}
		cao.removeSubscriptionTag(newsletter, userId, tagId);
	}

	public void modifyStauts(int userId, int newsletterId, String status) {
		System.out.println("-----modifyStauts-----");
		Newsletter newsletter = cao.getNewsletterById(newsletterId);
		newsletter.setStatus(status);
		cao.modifySubscriptionStauts(newsletter, userId);
	}
	
	public void modifyFormat(int userId, int newsletterId, String format) {
		System.out.println("modifyformat");
		Newsletter newsletter = cao.getNewsletterById(newsletterId);		
		newsletter.setFormat(format);
		cao.modifySubscriptionFormat(newsletter, userId);		
	}

	public void modifyInterval(int userId, int newsletterId, Date interval) {
		Newsletter newsletter = cao.getNewsletterById(newsletterId);
		newsletter.setInterval(interval);
		cao.modifySubscriptionFormat(newsletter, userId);				
	}

	public boolean noSubscriptionRecord(int userId, int newsletterId) {
		List<Node> list = cao.getSubscriptionrecord(newsletterId, userId);	
		if(list.size()==0){
			return true;
		}else{
			return false;
		}
	}

	public void addNewRecord(int userId, int newsletterId) {
		System.out.println("userId="+userId+"newsletterId"+newsletterId);
		Newsletter newsletter = new Newsletter();
		newsletter.setId(newsletterId);
		newsletter.setFormat("HTML");
		newsletter.setStatus("normal");
		cao.addSubscriptionRecord(newsletter, userId);	
	}

	
	
}
