package com.finalist.newsletter.cao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.Query;
import org.mmbase.bridge.RelationManager;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.Constraint;
import org.mmbase.storage.search.RelationStep;
import org.mmbase.storage.search.Step;

import com.finalist.newsletter.cao.NewsletterSubscriptionCAO;
import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.domain.Tag;
import com.finalist.newsletter.services.CommunityModuleAdapter;
import static com.finalist.newsletter.domain.Subscription.STATUS;

public class NewsletterSubscriptionCAOImpl implements NewsletterSubscriptionCAO {

	private static Log log = LogFactory
			.getLog(NewsletterSubscriptionCAOImpl.class);

	private Cloud cloud;

	public NewsletterSubscriptionCAOImpl() {
	}
	public NewsletterSubscriptionCAOImpl(Cloud cloud) {
		this.cloud = cloud;
	}

	public List<Node> querySubcriptionByUser(int userId) {

		NodeManager recordManager = cloud.getNodeManager("subscriptionrecord");
		NodeQuery query = cloud.createNodeQuery();
		String subscriber = "subscriber";

		Step theStep = null;
		theStep = query.addStep(recordManager);
		query.setNodeStep(theStep);

		Field field = recordManager.getField(subscriber);
		Constraint titleConstraint = SearchUtil.createEqualConstraint(query,
				field, Integer.toString(userId));
		SearchUtil.addConstraint(query, titleConstraint);
		return query.getList();
	}

	public void addSubscriptionRecord(Subscription subscription, int userId) {
		String nodeType = "subscriptionrecord";
		NodeManager subscriptionrecordNodeManager = cloud
				.getNodeManager(nodeType);
		Node subscriptionrecordNode = subscriptionrecordNodeManager
				.createNode();

		subscriptionrecordNode.setIntValue("subscriber", userId);
		subscriptionrecordNode.setStringValue("status", subscription.getStatus().toString());
		subscriptionrecordNode.setStringValue("format", subscription.getMimeType());
		subscriptionrecordNode.commit();

		// add Relation to newsletter
		int nodeNumber = subscription.getNewsletter().getId();
		Node newsletternode = cloud.getNode(nodeNumber);
		RelationManager insrel = cloud.getRelationManager("subscriptionrecord",
				"newsletter", "newslettered");
		subscriptionrecordNode.createRelation(newsletternode, insrel).commit();
		subscription.setId(subscriptionrecordNode.getNumber());
	}

	public void modifySubscriptionStauts(Subscription subscription) {
      log.debug("Modify subscription status"+subscription.getId()+" to "+subscription.getStatus());
      String stauts = subscription.getStatus().toString();

      Node record = cloud.getNode(subscription.getId());
		record.setStringValue("status", stauts);
		record.commit();

		if ("INACTIVE".equals(stauts)) {
			record.setStringValue("format", "html");
			record.deleteRelations("tagged");
			record.commit();
			}

	}

	public void modifySubscriptionFormat(Subscription subscription) {
		int recordId = subscription.getId();
		Node record = cloud.getNode(recordId);

		String format = subscription.getMimeType();
		record.setStringValue("format", format);
		record.commit();
	}

	public void addSubscriptionTag(Subscription subscription,int tagId) {
			int recordId = subscription.getId();
			Node record = cloud.getNode(recordId);
			Node tag = cloud.getNode(tagId);

			RelationManager insrel = cloud.getRelationManager(
					"subscriptionrecord", "tag", "tagged");
			record.createRelation(tag, insrel).commit();
	}

	public void removeSubscriptionTag(Subscription subscription,int tagId) {
			int recordId = subscription.getId();
			Node record = cloud.getNode(recordId);

			List<Node> taglist = record.getRelatedNodes("tag");
			Iterator taglistIt = taglist.iterator();
			record.deleteRelations("tagged");
			for (int i = 0; i < taglist.size(); i++) {
				Node tag = (Node) taglistIt.next();
				if (tagId != tag.getNumber()) {
					RelationManager insrel = cloud.getRelationManager(
							"subscriptionrecord", "tag", "tagged");
					record.createRelation(tag, insrel).commit();
				}
			}
	}

	public Subscription getSubscription(int newsletterId, int userId) {
		log.debug("getSubscriptionrecord that newsletterId=" + newsletterId);
		NodeManager recordManager = cloud.getNodeManager("subscriptionrecord");
		NodeManager newsletterManager = cloud.getNodeManager("newsletter");

		Query query = cloud.createQuery();
		Step parameterStep = query.addStep(recordManager);
		RelationStep relationStep = query.addRelationStep(newsletterManager,
				"newslettered", "DESTINATION");
		Step newsletterStep = relationStep.getNext();

		query.addField(parameterStep, recordManager.getField("subscriber"));
		query.addField(newsletterStep, newsletterManager.getField("number"));

		SearchUtil.addEqualConstraint(query, recordManager
				.getField("subscriber"), Integer.toString(userId));
		SearchUtil.addEqualConstraint(query, newsletterManager
				.getField("number"), newsletterId);

		List<Node> subscriptionList = query.getList();
		Subscription subscription = new Subscription();
		if (0 != subscriptionList.size()) {
			Node subscriptionNode = subscriptionList.get(0);
			int subscriptionId = subscriptionNode.getIntValue("subscriptionrecord.number");
			log.debug("Get subscription successful");

			subscription.setId(subscriptionId);
			subscription.setMimeType(subscriptionNode.getStringValue("subscriptionrecord.format"));
			subscription.setStatus(STATUS.valueOf(subscriptionNode.getStringValue("subscriptionrecord.status")));
			List<Node> tagList =  cloud.getNode(subscriptionId).getRelatedNodes("tag");

			Iterator tagIt = tagList.iterator();
			for(int i=0;i<tagList.size();i++){
				Tag tag = new Tag();
				Node tagNode = (Node) tagIt.next();
				tag.setId(tagNode.getNumber());
				tag.setName(tagNode.getStringValue("name"));
				tag.setSubscription(true);
				subscription.getTags().add(tag);
			}
			return subscription;
		} else {
			log.debug("Get subscription failed,user " + userId
					+ " may not subscripbe " + newsletterId);
			return null;
		}
	}

   public Set<Tag> getTags(int subscriptionId) {

      List<Node> tagList = cloud.getNode(subscriptionId).getRelatedNodes("tag");
      Set<Tag> tags = new HashSet<Tag>();

      for (Node tagNode : tagList) {
         Tag tag = new Tag();
         tag.setId(tagNode.getNumber());
         tag.setName(tagNode.getStringValue("name"));
         tag.setSubscription(true);
      }

      return tags;
   }



   public List<Subscription> getSubscriptionByNewsletterId(int newsletterId) {

      List<Node> records = cloud.getNode(newsletterId).getRelatedNodes("subscriptionrecord");
      log.debug("Get subscriptions of newsletter:"+newsletterId+" and get "+records.size()+" records in all");
      List<Subscription> subscribers = new ArrayList<Subscription>();
      for (Node record : records) {
         String status = record.getStringValue("status");
         if (STATUS.ACTIVE.equals(STATUS.valueOf(status))) {
            subscribers.add(convertFromNode(record));
         }
      }

      return subscribers;
   }

   private Subscription convertFromNode(Node node){
      Subscription subscription = new Subscription();
      subscription.setId(node.getIntValue("number"));
      subscription.setMimeType(node.getStringValue("format"));
      subscription.setStatus(Subscription.STATUS.valueOf(node.getStringValue("status")));
      subscription.setSubscriber(CommunityModuleAdapter.getUserById(node.getStringValue("subscriber")));
      subscription.setSubscriberId(node.getStringValue("subscriber"));
      return subscription;
   }

}
