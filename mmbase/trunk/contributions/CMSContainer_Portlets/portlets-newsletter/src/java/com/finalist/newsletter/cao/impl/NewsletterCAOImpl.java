package com.finalist.newsletter.cao.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.Query;
import org.mmbase.storage.search.Step;
import org.mmbase.storage.search.StepField;
import org.mmbase.storage.search.implementation.BasicFieldValueConstraint;

import com.finalist.newsletter.cao.NewsletterCAO;
import com.finalist.newsletter.cao.util.NlUtil;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.Tag;

public class NewsletterCAOImpl implements NewsletterCAO {

	private Cloud cloud;

	public NewsletterCAOImpl() {
	}

	public NewsletterCAOImpl(Cloud cloud) {
		super();
		this.cloud = cloud;
	}

	public List<Newsletter> getAllNewsletters (){

		NodeManager manager = cloud.getNodeManager("newsletter");
		NodeQuery query = cloud.createNodeQuery();
		query.setNodeStep(query.addStep(manager));
		List<Node> nodelist = manager.getList(query);
		
		NlUtil nlUtil = new NlUtil();
		return nlUtil.convertFromNodeList(nodelist);
	}

	public Newsletter getNewsletterById(int id) {
      Node newsletterNode = cloud.getNode(id);
      Newsletter newsletter = new Newsletter();
      newsletter.setNumber(newsletterNode.getIntValue("number"));
      newsletter.setTitle(newsletterNode.getStringValue("title"));
      List<Node> tagList = newsletterNode.getRelatedNodes();
      Iterator tagIt = tagList.iterator();
      for(int i=0;i<tagList.size();i++){
    	  Tag tag = new Tag();
    	  Node tagNode = (Node) tagIt.next();
    	  tag.setId(tagNode.getNumber());
    	  tag.setName(tagNode.getStringValue("name"));
    	  tag.setSubscription(false);
    	  newsletter.getTags().add(tag);
      }
      return newsletter;
   }

}
