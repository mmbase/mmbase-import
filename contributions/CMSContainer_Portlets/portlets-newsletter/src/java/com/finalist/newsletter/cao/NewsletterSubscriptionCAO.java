package com.finalist.newsletter.cao;

import java.util.List;
import java.util.Set;

import org.mmbase.bridge.Node;

import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.domain.Tag;

public interface NewsletterSubscriptionCAO {

	public List<Node> querySubcriptionByUser(int userId);

	public void addSubscriptionRecord(Subscription subscription, int userId);

	public void modifySubscriptionStauts(Subscription subscription);

	public void modifySubscriptionFormat(Subscription subscription);

	public void addSubscriptionTag(Subscription subscription,int tagId);

	public void removeSubscriptionTag(Subscription subscription,int tagId);	

	public List<Subscription> getSubscriptionByNewsletterId(int newsletterId);

	public Subscription getSubscription(int newsletterId, int userId);

   Set<Tag> getTags(int id);
}
