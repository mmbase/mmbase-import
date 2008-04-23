
package com.finalist.newsletter.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.cache.CachePolicy;

import com.finalist.newsletter.cao.impl.NewsletterSubscriptionCAOImpl;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.Subscription;

public interface NewsletterSubscriptionServices {
	
	public boolean hasSubscription(int userId);
	public Subscription addRecordInfo(int newsletterId, int userId);	
	public boolean noSubscriptionRecord(int userId,int newsletterId);
	public void selectTagInLetter(int userId,int newsletterId,int tagId);
	public void unSelectTagInLetter(int userId,int newsletterId,int tagId);
	public void modifyFormat(int userId,int newsletterId,String format);
	public void modifyStauts(int userId,int newsletterId,String status);
	public void addNewRecord(int userId,int newsletterId);
	public List<Subscription> getSubscriptionList(String[] newsletters, int userId);
	public List<Subscription> getNewSubscription(String[] newsletters);  
}
