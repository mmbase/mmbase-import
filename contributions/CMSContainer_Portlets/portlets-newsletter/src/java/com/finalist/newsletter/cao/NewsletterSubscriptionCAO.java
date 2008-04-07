package com.finalist.newsletter.cao;

import java.util.List;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.community.DetailNewsletterInfo;
import com.finalist.newsletter.domain.Newsletter;

public interface NewsletterSubscriptionCAO {
	public Newsletter getNewsletterById(int id);
	
	public List<Newsletter> getUserSubscriptionList(String userName);
	
	public List<Node> querySubcriptionByUser(String userName);
	
	public void addSubscriptionRecord(DetailNewsletterInfo detailNewsletterInfo);
	
	public void updateSubscriptionRecord(Node node, String status);
	
	public Node getUpdateNode(DetailNewsletterInfo detailNewsletterInfo);
	
	public List<Newsletter> getAllNewsletter();
	
	
}
