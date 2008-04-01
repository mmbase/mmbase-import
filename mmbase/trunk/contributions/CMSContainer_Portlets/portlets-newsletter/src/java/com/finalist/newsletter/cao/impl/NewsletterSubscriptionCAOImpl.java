package com.finalist.newsletter.cao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.Constraint;
import org.mmbase.storage.search.Step;

import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.community.DetailNewsletterInfo;
import com.finalist.newsletter.cao.NewsletterSubscriptionCAO;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.cao.util.NewsletterSubscriptionUtil;

public class NewsletterSubscriptionCAOImpl implements NewsletterSubscriptionCAO {

	private Cloud cloud;

	public void setCloud(Cloud cloud) {
		this.cloud = cloud;
	}

	public Newsletter getNewsletterById(int id) {
		
		Newsletter newsletter = new Newsletter();
		Node node = cloud.getNode(id);
		newsletter = NewsletterSubscriptionUtil.populateNewsletter(node, newsletter);
		NodeList taglist = node.getRelatedNodes("tag");		
		newsletter = NewsletterSubscriptionUtil.convertNodeListtoTagList(taglist, newsletter);
		return newsletter;
	}

	public NewsletterSubscriptionCAOImpl() {
		CloudProvider provider = CloudProviderFactory.getCloudProvider();
		cloud = provider.getCloud();
	}

	public static DetailNewsletterInfo getUserSubscriptionList(String userName,
			Cloud cloud) {
		DetailNewsletterInfo detailNewsletterInfo = new DetailNewsletterInfo();

		List<Node> resluts = new ArrayList<Node>();
		resluts = NewsletterSubscriptionCAOImpl.querySubcriptionByUser(
				userName, cloud);
		detailNewsletterInfo.setSubscriber(userName);
		Iterator<Node> nodes = resluts.iterator();
		for (int i = 0; i < resluts.size(); i++) {
			Node node = nodes.next();
			String status = node.getStringValue("status");
			detailNewsletterInfo.setStatus(status);
			Date interval = node.getDateValue("interval");
			detailNewsletterInfo.setInterval(interval);
			String format = node.getStringValue("format");
			detailNewsletterInfo.setFormat(format);

			NodeList newsletters = node.getRelatedNodes("newsletter");
			Iterator<Node> newsletterIterator = newsletters.iterator();
			for (int j = 0; j < newsletters.size(); j++) {
				String newsletter = newsletterIterator.next().getStringValue(
						"title");
				detailNewsletterInfo.setNewsletter(newsletter);
			}

			NodeList tags = node.getRelatedNodes("tags");
			Iterator<Node> tagsItetator = tags.iterator();
			for (int y = 0; y < tags.size(); y++) {
				String tag = tagsItetator.next().getStringValue("title");
				detailNewsletterInfo.setTag(tag);
			}
		}

		return detailNewsletterInfo;
	}

	public static List<Node> querySubcriptionByUser(String userName, Cloud cloud) {
		String nodeType = "subscriptionrecord";
		NodeManager manager = cloud.getNodeManager(nodeType);
		List<Node> results = createDetailQuery(cloud, manager, userName);
		return results;
	}

	public static List<Node> createDetailQuery(Cloud cloud,
			NodeManager manager, String userName) {
		NodeQuery query = cloud.createNodeQuery();
		String subscriber = "subscriber";
		Step theStep = null;
		theStep = query.addStep(manager);
		query.setNodeStep(theStep);
		Field field = manager.getField(subscriber);
		Constraint titleConstraint = SearchUtil.createLikeConstraint(query,
				field, userName);
		SearchUtil.addConstraint(query, titleConstraint);
		NodeList results = cloud.getList(query);
		return results;
	}

	public static void addSubscriptionRecord(Cloud cloud,
			DetailNewsletterInfo detailNewsletterInfo) {
		String nodeType = "subscriptionrecord";
		NodeManager subscriptionrecordNodeManager = cloud
				.getNodeManager(nodeType);
		Node subscriptionrecordNode = subscriptionrecordNodeManager
				.createNode();
		subscriptionrecordNode.setStringValue("subscriber",
				detailNewsletterInfo.getSubscriber());
		subscriptionrecordNode.setStringValue("status", detailNewsletterInfo
				.getStatus());
		subscriptionrecordNode.setDateValue("interval", detailNewsletterInfo
				.getInterval());
		subscriptionrecordNode.setStringValue("newsletter",
				detailNewsletterInfo.getNewsletter());
		subscriptionrecordNode.setStringValue("tag", detailNewsletterInfo
				.getTag());
		subscriptionrecordNode.setStringValue("format", detailNewsletterInfo
				.getFormat());
		subscriptionrecordNode.commit();
		detailNewsletterInfo
				.setId(subscriptionrecordNode.getIntValue("number"));
	}

	public static void updateSubscriptionRecord(Node node, String status) {
		node.setStringValue("status", status);
		node.commit();
	}

	public static Node getUpdateNode(Cloud cloud,
			DetailNewsletterInfo detailNewsletterInfo) {
		int nodeNumber = detailNewsletterInfo.getId();
		Node node = cloud.getNode(nodeNumber);
		return node;
	}

}
