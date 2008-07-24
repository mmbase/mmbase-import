package com.finalist.newsletter.cao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import net.sf.mmapps.commons.beans.MMBaseNodeMapper;

import org.apache.commons.lang.StringUtils;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.Constraint;
import org.mmbase.storage.search.RelationStep;
import org.mmbase.storage.search.Step;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.newsletter.cao.AbstractCAO;
import com.finalist.newsletter.cao.NewsletterCAO;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.Term;

public class NewsletterCAOImpl extends AbstractCAO implements NewsletterCAO {
	private static Logger log = Logging.getLoggerInstance(NewsletterCAOImpl.class.getName());

	public NewsletterCAOImpl() {
	}

	public List<Term> getALLTerm() {
		NodeQuery query = cloud.createNodeQuery();
		Step step = query.addStep(cloud.getNodeManager("term"));
		query.setNodeStep(step);
		NodeList list = query.getList();
		return list;
	}

	public List<Newsletter> getNewsletterByConstraint(String property, String constraintType, String value) {
		NodeQuery query = cloud.createNodeQuery();
		NodeManager nodeManager = cloud.getNodeManager("newsletter");
		Step step = query.addStep(nodeManager);
		query.setNodeStep(step);
		if (StringUtils.isNotBlank(property)) {
			if (constraintType.equals("like")) {
				SearchUtil.addLikeConstraint(query, nodeManager.getField(property), value);
			}
		}
		NodeList list = query.getList();
		return MMBaseNodeMapper.convertList(list, Newsletter.class);
	}

	public int getNewsletterIdBySubscription(int id) {
		log.debug("Get newsletter by subsription " + id);
		Node subscriptionNode = cloud.getNode(id);
		NodeList nodes = subscriptionNode.getRelatedNodes("newsletter");

		if (nodes.size() > 0) {
			return nodes.getNode(0).getNumber();
		} else {
			return -1;
		}
	}

	public Node getNewsletterNodeById(int newsletterId) {
		return cloud.getNode(newsletterId);
	}

	public Newsletter getNewsletterById(int id) {
		Node newsletterNode = cloud.getNode(id);

		// <<<<<<< NewsletterCAOImpl.java
		// Newsletter newsletter = convertFromNode(newsletterNode);
		//      
		// List<Node> terms = newsletterNode.getRelatedNodes("term");
		// log.debug("get newsletter by id:" + id + ",and get " + terms.size() +
		// " terms with it.");
		// Iterator termsIt = terms.iterator();
		//
		// for (int i = 0; i < terms.size(); i++) {
		// Term term = new Term();
		// Node termNode = (Node) termsIt.next();
		// term.setId(termNode.getNumber());
		// term.setName(termNode.getStringValue("name"));
		// term.setSubscription(false);
		// newsletter.getTerms().add(term);
		// }
		// =======
		Newsletter newsletter = convertFromNode(newsletterNode);

		NodeList terms = newsletterNode.getRelatedNodes("term");
		log.debug("get newsletter by id:" + id + ",and get " + terms.size() + " terms with it.");
		List<Term> termList = MMBaseNodeMapper.convertList(terms, Term.class);
		newsletter.getTerms().addAll(termList);
		// >>>>>>> 1.16

		return newsletter;
	}

	private Newsletter convertFromNode(Node newsletterNode) {
		Newsletter newsletter = new Newsletter();
		newsletter.setId(newsletterNode.getIntValue("number"));
		newsletter.setTitle(newsletterNode.getStringValue("title"));
		newsletter.setReplyAddress(newsletterNode.getStringValue("replyto_mail"));
		newsletter.setReplyName(newsletterNode.getStringValue("replyto_name"));
		newsletter.setFromAddress(newsletterNode.getStringValue("from_mail"));
		newsletter.setFromName(newsletterNode.getStringValue("from_name"));
		return newsletter;
	}

	public List<Term> getNewsletterTermsByName(int newsletterId, String name, int pagesize, int offset, String order, String direction) {

		NodeManager termNodeManager = cloud.getNodeManager("term");
		NodeManager newsletterNodeManager = cloud.getNodeManager("newsletter");

		NodeQuery query = cloud.createNodeQuery();
		// <<<<<<< NewsletterCAOImpl.java

		Step newsletterStep = query.addStep(newsletterNodeManager);
		query.setNodeStep(newsletterStep);
		Constraint idConstraint = SearchUtil.createEqualConstraint(query, newsletterNodeManager.getField("number"), newsletterId);
		SearchUtil.addConstraint(query, idConstraint);

		RelationStep relationStep = query.addRelationStep(termNodeManager, "posrel", "destination");
		Step termStep = relationStep.getNext();
		// =======
		//
		// Step termStep = query.addStep(termNodeManager);
		// >>>>>>> 1.16
		query.setNodeStep(termStep);
		// <<<<<<< NewsletterCAOImpl.java
		if (StringUtils.isNotBlank(name)) {
			SearchUtil.addLikeConstraint(query, termNodeManager.getField("name"), name);
		}
		query.setMaxNumber(pagesize);
		query.setOffset(offset);
		String orderBy = "number";
		if (!"number".equals(order.trim()))
			orderBy = order.trim();
		Queries.addSortOrders(query, orderBy, direction);
		List<Node> nodeList = query.getList();
		List<Term> terms = new ArrayList<Term>();
		for (Node termNode : nodeList) {
			int tmpNum = termNode.getNumber();
			String tmpName = termNode.getStringValue("name");
			Term term = new Term();
			term.setId(tmpNum);
			term.setName(tmpName);
			terms.add(term);
			// =======
			//
			// if(StringUtils.isNotBlank(name)){
			// Constraint nameConstrant =SearchUtil.createLikeConstraint(query,
			// termNodeManager.getField("name"), "%" + name + "%" );
			// SearchUtil.addConstraint(query, nameConstrant);
			// >>>>>>> 1.16
		}
		// <<<<<<< NewsletterCAOImpl.java
		return terms;
	}

	public int getNewsletterTermsCountByName(int newsletterId, String name) {
		NodeManager termNodeManager = cloud.getNodeManager("term");
		NodeManager newsletterNodeManager = cloud.getNodeManager("newsletter");

		NodeQuery query = cloud.createNodeQuery();

		Step newsletterStep = query.addStep(newsletterNodeManager);
		query.setNodeStep(newsletterStep);
		Constraint idConstraint = SearchUtil.createEqualConstraint(query, newsletterNodeManager.getField("number"), newsletterId);
		// =======
		//
		// RelationStep rStep = query.addRelationStep(newsletterNodeManager,
		// "posrel", "destination");
		// Constraint idConstraint = SearchUtil.createEqualConstraint(query,
		// newsletterNodeManager.getField("number"), newsletterId);
		// >>>>>>> 1.16
		SearchUtil.addConstraint(query, idConstraint);
		// <<<<<<< NewsletterCAOImpl.java

		RelationStep relationStep = query.addRelationStep(termNodeManager, "posrel", "destination");
		Step termStep = relationStep.getNext();
		query.setNodeStep(termStep);
		if (StringUtils.isNotBlank(name)) {
			SearchUtil.addLikeConstraint(query, termNodeManager.getField("name"), name);
		}
		return query.getList().size();
		// =======
		//
		// System.out.println(query.toSql());
		// NodeList nodeList = query.getList();
		// System.out.println(nodeList.toString());
		// return new HashSet<Term>();
		// >>>>>>> 1.16
	}

   public void processBouncesOfPublication(String publicationId, String userId,
         String bounceContent) {
      NodeManager bounceManager = cloud.getNodeManager("newsletterbounce"); 
         Node node = bounceManager.createNode();
         if(StringUtils.isNotEmpty(publicationId)){
            node.setIntValue("newsletter", Integer.parseInt(publicationId));
         }
         if(StringUtils.isNotEmpty(userId)){
            node.setIntValue("userid", Integer.parseInt(userId));
         }
         node.setStringValue("content",bounceContent);
         node.setDateValue("bouncedate",new Date());
         node.commit();
      
   }

   public Set<Term> getNewsletterTermsByName(int newsltterId, String name,
         int pagesize, int offset) {
      // TODO Auto-generated method stub
      return null;
   }

}
