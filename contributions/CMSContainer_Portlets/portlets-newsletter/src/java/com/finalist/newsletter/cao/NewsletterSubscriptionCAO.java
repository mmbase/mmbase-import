package com.finalist.newsletter.cao;

import java.util.List;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.newsletter.domain.Newsletter;
import com.finalist.cmsc.services.community.person.Person;

public interface NewsletterSubscriptionCAO {
	public Newsletter getNewsletterById(int id);
	
	public List<Newsletter> getUserSubscriptionList(String userName);
	
	public List<Node> querySubcriptionByUser(String userName);
	
	public void updateSubscriptionRecord(Node node, String status);
	
	public List<Newsletter> getAllNewsletter();
	
   public List<Person> getSubscribers(int newsletterId);
}
