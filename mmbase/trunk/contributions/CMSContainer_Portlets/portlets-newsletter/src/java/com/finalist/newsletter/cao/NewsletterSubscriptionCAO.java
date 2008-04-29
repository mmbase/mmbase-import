package com.finalist.newsletter.cao;

import java.util.List;
import java.util.Set;

import org.mmbase.bridge.Node;

import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.domain.Term;

public interface NewsletterSubscriptionCAO {

	public List<Node> querySubcriptionByUser(int userId);

	public void addSubscriptionRecord(Subscription subscription, int userId);

	public void modifySubscriptionStauts(Subscription subscription);

	public void modifySubscriptionFormat(Subscription subscription);

	public void addSubscriptionTerm(Subscription subscription,int termId);

	public void removeSubscriptionTerm(Subscription subscription,int termId);

	public List<Subscription> getSubscription(int newsletterId);

	public Subscription getSubscription(int newsletterId, int userId);

   Set<Term> getTerms(int id);
}
