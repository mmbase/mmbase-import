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
import com.finalist.newsletter.cao.util.NewsletterSubscriptionUtil;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.domain.Tag;
import com.finalist.newsletter.util.POConvertUtils;

public class NewsletterSubscriptionCAOImpl implements NewsletterSubscriptionCAO {

	private static Log log = LogFactory
			.getLog(NewsletterSubscriptionCAOImpl.class);

	private Cloud cloud;

	public NewsletterSubscriptionCAOImpl() {

	}

	public NewsletterSubscriptionCAOImpl(Cloud cloud) {
		this.cloud = cloud;
	}

	public List<Newsletter> getAllNewsletter() {
		CloudProvider provider = CloudProviderFactory.getCloudProvider();
		cloud = provider.getCloud();
		List<Newsletter> list = new ArrayList<Newsletter>();
		Newsletter letter = new Newsletter();
		String newsletter = "newsletter";
		NodeManager manager = cloud.getNodeManager(newsletter);
		NodeQuery query = cloud.createNodeQuery();
		Step theStep = null;
		theStep = query.addStep(manager);
		query.setNodeStep(theStep);
		List<Node> nodelist = manager.getList(query);
		Iterator<Node> it = nodelist.iterator();
		for (int i = 0; i < nodelist.size(); i++) {
			Node node = it.next();
			letter.setTitle(node.getStringValue("title"));
			NodeManager tagManager = cloud.getNodeManager("tag");
			NodeList taglist = node.getRelatedNodes(tagManager);
			letter = NewsletterSubscriptionUtil.convertNodeListtoTagList(
					taglist, letter);
			System.out.println("title=" + letter.getTitle());
			list.add(letter);
		}
		return list;
	}

	public Newsletter getNewsletterById(int id) {
		CloudProvider provider = CloudProviderFactory.getCloudProvider();
		cloud = provider.getCloud();
		System.out.println("Newsletterid=" + id);
		Newsletter newsletter = new Newsletter();
		Node node = cloud.getNode(id);
		newsletter = NewsletterSubscriptionUtil.populateNewsletter(node,
				newsletter);
		NodeManager tagManager = cloud.getNodeManager("tag");
		NodeList taglist = node.getRelatedNodes(tagManager);
		// System.out.println("taglist.size()="+taglist.size());
		newsletter = NewsletterSubscriptionUtil.convertNodeListtoTagList(
				taglist, newsletter);
		return newsletter;
	}

	public List<Newsletter> getUserSubscriptionList(int userId) {
		List<Newsletter> list = new ArrayList<Newsletter>();
		Newsletter newsletter = new Newsletter();

		List<Node> resluts = new ArrayList<Node>();
		resluts = querySubcriptionByUser(userId);
		Iterator<Node> nodes = resluts.iterator();
		System.out.println("UserSelectResluts=" + resluts.size());
		for (int i = 0; i < resluts.size(); i++) {
			Node node = nodes.next();
			String status = node.getStringValue("status");
			newsletter.setStatus(status);
			Date interval = node.getDateValue("interval");
			newsletter.setInterval(interval);
			String format = node.getStringValue("format");
			newsletter.setFormat(format);

			NodeList newsletters = node.getRelatedNodes("newsletter");
			Iterator<Node> newsletterIterator = newsletters.iterator();
			for (int j = 0; j < newsletters.size(); j++) {
				String title = newsletterIterator.next()
						.getStringValue("title");
				newsletter.setTitle(title);
			}

			NodeManager tagManager = cloud.getNodeManager("tag");
			NodeList tags = node.getRelatedNodes(tagManager);
			System.out.println("selectTags=" + tags.size());
			List<Tag> tagList = new ArrayList<Tag>();
			Iterator<Node> tagsItetator = tags.iterator();
			for (int y = 0; y < tags.size(); y++) {
				Node selectTagNode = (Node) tagsItetator.next();
				Tag selectTag = new Tag();
				selectTag.setName(selectTagNode.getStringValue("name"));
				selectTag.setId(selectTagNode.getNumber());
				selectTag.setSubscription(true);
		   	tagList.add(selectTag);
				newsletter.setTags(tagList);
			}
			list.add(newsletter);
		}
		return list;
	}

	public List<Node> querySubcriptionByUser(int userId) {
		CloudProvider provider = CloudProviderFactory.getCloudProvider();
		cloud = provider.getCloud();
		List<Node> results = null;
		NodeManager recordManager = cloud.getNodeManager("subscriptionrecord");
		NodeQuery query = cloud.createNodeQuery();
		String subscriber = "subscriber";
		Step theStep = null;
		theStep = query.addStep(recordManager);
		query.setNodeStep(theStep);
		Field field = recordManager.getField(subscriber);
		String userIdString = String.valueOf(userId);
		Constraint titleConstraint = SearchUtil.createEqualConstraint(query,
				field, userId);

		SearchUtil.addConstraint(query, titleConstraint);
		return query.getList();
	}

	public void addSubscriptionRecord(Newsletter newsletter, int userId) {
		String nodeType = "subscriptionrecord";
		NodeManager subscriptionrecordNodeManager = cloud
				.getNodeManager(nodeType);
		Node subscriptionrecordNode = subscriptionrecordNodeManager
				.createNode();
		subscriptionrecordNode.setIntValue("subscriber", userId);
		subscriptionrecordNode.setStringValue("status", newsletter.getStatus());
		subscriptionrecordNode.setStringValue("format", newsletter.getFormat());
		subscriptionrecordNode.commit();
		// add Relation to newsletter
		int nodeNumber = newsletter.getId();
		Node newsletternode = cloud.getNode(nodeNumber);
		System.out.println("newsletternode=" + newsletternode.getNumber());
		RelationManager insrel = cloud.getRelationManager("subscriptionrecord",
				"newsletter", "newslettered");
		subscriptionrecordNode.createRelation(newsletternode, insrel).commit();
	}

	public void modifySubscriptionStauts(Newsletter newsletter, int userId) {
		System.out.println("modifySubscriptionStauts");
		String stauts = newsletter.getStatus();
		int newsletterId = newsletter.getId();
		List<Node> records = getSubscriptionrecord(newsletterId, userId);
		if (0 != records.size()) {
			Node record = records.get(0);
			record.setStringValue("status", stauts);
			record.commit();
			if ("unSubscription".equals(stauts)) {
				record.deleteRelations("tagged");
			}
		}

	}

	public void modifySubscriptionFormat(Newsletter newsletter, int userId) {
		int newsletterId = newsletter.getId();
		System.out
				.println("newsletterId=" + newsletterId + ";userId=" + userId);
		List<Node> records = getSubscriptionrecord(newsletterId, userId);
		if (0 != records.size()) {
			Node record = records.get(0);
			System.out.println("recordId=" + record);
			String format = newsletter.getFormat();
			record.setStringValue("format", format);
			record.commit();
		}
	}

	public void modifySubscriptionInterval(Newsletter newsletter, int userId) {
		int newsletterId = newsletter.getId();
		List<Node> records = getSubscriptionrecord(newsletterId, userId);
		if (0 != records.size()) {
			Node record = records.get(0);
			Date interval = newsletter.getInterval();
			record.setDateValue("interval", interval);
			record.commit();
		}
	}

	public List<Node> getSubscriptionrecord(int newsletterId, int userId) {
		// get subscriptionrecord
		CloudProvider provider = CloudProviderFactory.getCloudProvider();
		cloud = provider.getCloud();
		System.out.println("getSubscriptionrecord that newsletterId="
				+ newsletterId);
		NodeManager recordManager = cloud.getNodeManager("subscriptionrecord");
		NodeQuery query = cloud.createNodeQuery();
		Step theStep = null;
		theStep = query.addStep(recordManager);
		query.setNodeStep(theStep);
		Field subscriberField = recordManager.getField("subscriber");
		Constraint titleConstraint = SearchUtil.createEqualConstraint(query,
				subscriberField, userId);
		SearchUtil.addConstraint(query, titleConstraint);
		List<Node> list = query.getList();
		List<Node> results = new ArrayList<Node>();
		Iterator it = list.iterator();
		for (int i = 0; i < list.size(); i++) {
			Node record = (Node) it.next();
			List<Node> newsletters = record.getRelatedNodes("newsletter");
			Iterator newsletterIt = newsletters.iterator();
			for (int j = 0; j < newsletters.size(); j++) {
				Node newsletter = (Node) newsletterIt.next();
				if (newsletterId == newsletter.getNumber()) {
					results.add(record);
					System.out.println("record=" + record.getNumber());
				}
			}
		}
		return results;
	}

	public void addSubscriptionTag(Newsletter newsletter, int userId, int tagId) {
		int newsletterId = newsletter.getId();
		List<Node> records = getSubscriptionrecord(newsletterId, userId);
		if (0 != records.size()) {
			System.out.println("addSubscriptionTag");
			Node record = records.get(0);
			Node tag = cloud.getNode(tagId);
			RelationManager insrel = cloud.getRelationManager(
					"subscriptionrecord", "tag", "tagged");
			record.createRelation(tag, insrel).commit();
		}
	}

	public void removeSubscriptionTag(Newsletter newsletter, int userId,
			int tagId) {
		int newsletterId = newsletter.getId();
		List<Node> records = getSubscriptionrecord(newsletterId, userId);
		if (0 != records.size()) {
			Node record = records.get(0);
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
				.getField("subscriber"), userId);
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
			subscription.setStatus(Subscription.STATUS.valueOf(subscriptionNode.getStringValue("subscriptionrecord.status")));			
			
			List<Node> tagList =  cloud.getNode(subscriptionId).getRelatedNodes("tag");
			log.debug("tagList.size()="+tagList.size());
			Iterator tagIt = tagList.iterator();
			for(int i=0;i<tagList.size();i++){
				Tag tag = new Tag();
				Node tagNode = (Node) tagIt.next();
				tag.setId(tagNode.getNumber());
				tag.setName(tagNode.getStringValue("name"));
				tag.setSubscription(true);
				subscription.getTags().add(tag);
			}
		} else {
			log.debug("Get subscription failed,user " + userId
					+ " may not subscripbe " + newsletterId);
			return null;
		}

		return subscription;

	}

	public List<Subscription> getSubscription(int newsletterId) {

		Node letterNode = cloud.getNode(newsletterId);
		List<Node> records = letterNode.getRelatedNodes("subscriptionrecord");

		Iterator<Node> it = records.iterator();
		while (it.hasNext()) {
			Node node = it.next();
			if (!"active".equals(node.getStringValue("status"))) {
				it.remove();
			}
		}

		return POConvertUtils.convertSubscriptions(records);

	}

}
