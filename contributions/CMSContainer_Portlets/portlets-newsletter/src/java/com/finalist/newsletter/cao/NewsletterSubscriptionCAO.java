package com.finalist.newsletter.cao;

import java.util.Date;
import java.util.List;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.Subscription;

public interface NewsletterSubscriptionCAO {
	public Newsletter getNewsletterById(int id);

	public List<Newsletter> getUserSubscriptionList(int userId);

	public List<Node> querySubcriptionByUser(int userId);

	public List<Newsletter> getAllNewsletter();

	public void addSubscriptionRecord(Newsletter newsletter, int userId);

	public void modifySubscriptionStauts(Newsletter newsletter, int userId);

	public void modifySubscriptionFormat(Newsletter newsletter, int userId);

	public void modifySubscriptionInterval(Newsletter newsletter, int userId);

	public void addSubscriptionTag(Newsletter newsletter, int userId, int tagId);

	public void removeSubscriptionTag(Newsletter newsletter, int userId,
			int tagId);

	public List<Node> getSubscriptionrecord(int newsletterId, int userId);

	public List<Subscription> getSubscription(int newsletterId);

	public Subscription getSubscription(int newsletterId, int userId);
}
