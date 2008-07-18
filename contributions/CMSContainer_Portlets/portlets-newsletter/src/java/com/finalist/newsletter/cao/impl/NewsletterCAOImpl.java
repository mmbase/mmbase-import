package com.finalist.newsletter.cao.impl;

import com.finalist.newsletter.cao.AbstractCAO;
import com.finalist.newsletter.cao.NewsletterCAO;
import com.finalist.newsletter.cao.util.NlUtil;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.Term;
import org.apache.commons.lang.StringUtils;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.Constraint;
import org.mmbase.storage.search.RelationStep;
import org.mmbase.storage.search.Step;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class NewsletterCAOImpl extends AbstractCAO implements NewsletterCAO {
   private static Logger log = Logging.getLoggerInstance(NewsletterCAOImpl.class.getName());

   public NewsletterCAOImpl() {
   }

   public NewsletterCAOImpl(Cloud cloud) {
      this.cloud = cloud;
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
      return NlUtil.convertFromNodeList(list);
   }

   public int getNewsletterIdBySubscription(int id) {
      log.debug("Get newsletter by subsription "+id);
      Node subscriptionNode = cloud.getNode(id);
      List<Node> nodes = subscriptionNode.getRelatedNodes("newsletter");

      if(nodes.size()>0){
         return nodes.get(0).getNumber();
      }else{
         return -1;
      }
   }

   public Node getNewsletterNodeById(int newsletterId) {
      return cloud.getNode(newsletterId);
   }

   public Newsletter getNewsletterById(int id) {
      Node newsletterNode = cloud.getNode(id);


      Newsletter newsletter = convertFromNode(newsletterNode);
      
      List<Node> terms = newsletterNode.getRelatedNodes("term");
      log.debug("get newsletter by id:" + id + ",and get " + terms.size() + " terms with it.");
      Iterator termsIt = terms.iterator();

      for (int i = 0; i < terms.size(); i++) {
         Term term = new Term();
         Node termNode = (Node) termsIt.next();
         term.setId(termNode.getNumber());
         term.setName(termNode.getStringValue("name"));
         term.setSubscription(false);
         newsletter.getTerms().add(term);
      }

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

	public Set<Term> getNewsletterTermsByName(int newsletterId, String name, int pagesize, int offset) {

		NodeManager termNodeManager = cloud.getNodeManager("term");
		NodeManager newsletterNodeManager = cloud.getNodeManager("newsletter");
		NodeQuery query = cloud.createNodeQuery();
		
		Step termStep = query.addStep(termNodeManager);
		query.setNodeStep(termStep);
		
		if(StringUtils.isNotBlank(name)){
			Constraint nameConstrant =SearchUtil.createLikeConstraint(query, 
					termNodeManager.getField("name"), "%" + name + "%" );
			SearchUtil.addConstraint(query, nameConstrant);
		}
		
		RelationStep rStep = query.addRelationStep(newsletterNodeManager, "posrel", "destination");
		Constraint idConstraint = SearchUtil.createEqualConstraint(query,
				newsletterNodeManager.getField("number"), newsletterId);
		SearchUtil.addConstraint(query, idConstraint);
		
		System.out.println(query.toSql());
		NodeList nodeList = query.getList();
		System.out.println(nodeList.toString());
		return new HashSet<Term>();
	}

}
