package com.finalist.newsletter.services.impl;

import java.util.Date;

import org.mmbase.bridge.Cloud;

import com.finalist.community.DetailNewsletterInfo;
import com.finalist.community.UserInfo;
import com.finalist.newsletter.cao.impl.NewsletterSubscriptionCAOImpl;
import com.finalist.newsletter.services.NewsletterSubscriptionServices;

public class NewsletterSubscriptionServicesImpl implements NewsletterSubscriptionServices{
	public static UserInfo getUserInfo(String userName)
	{
		UserInfo userinfo = new UserInfo(userName);
		
		return userinfo;
	}
	
	
	public static void addSubscription(Cloud cloud,String userName,String status,String newsletter,String tag,Date interval,String format)
	{
		DetailNewsletterInfo detailNewsletterInfo = new DetailNewsletterInfo();
		detailNewsletterInfo.setInterval(interval);
		detailNewsletterInfo.setNewsletter(newsletter);
		detailNewsletterInfo.setStatus(status);
		detailNewsletterInfo.setSubscriber(userName);
		detailNewsletterInfo.setTag(tag);
		detailNewsletterInfo.setFormat(format);
		NewsletterSubscriptionCAOImpl.addSubscriptionRecord(cloud, detailNewsletterInfo);	
	}
	
	public static void deleteSubscription(Cloud cloud,int subscriptionId)
	{
		String status = "delete";
		DetailNewsletterInfo detailNewsletterInfo = new DetailNewsletterInfo();
		detailNewsletterInfo.setId(subscriptionId);
		detailNewsletterInfo.setStatus(status);
		NewsletterSubscriptionCAOImpl.getUpdateNode(cloud, detailNewsletterInfo);
	}
	
	public static void pauseSubscription(Cloud cloud,int subscriptionId)
	{
		String status = "pause";
		DetailNewsletterInfo detailNewsletterInfo = new DetailNewsletterInfo();
		detailNewsletterInfo.setId(subscriptionId);
		detailNewsletterInfo.setStatus(status);
		NewsletterSubscriptionCAOImpl.getUpdateNode(cloud, detailNewsletterInfo);
	}
	
	public static void resetSubscription(Cloud cloud,int subscriptionId)
	{
		String status = "reset";
		DetailNewsletterInfo detailNewsletterInfo = new DetailNewsletterInfo();
		detailNewsletterInfo.setId(subscriptionId);
		detailNewsletterInfo.setStatus(status);
		NewsletterSubscriptionCAOImpl.getUpdateNode(cloud, detailNewsletterInfo);
	}
	
}
