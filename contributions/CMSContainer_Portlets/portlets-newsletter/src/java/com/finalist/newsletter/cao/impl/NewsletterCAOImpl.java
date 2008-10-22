package com.finalist.newsletter.cao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import com.finalist.cmsc.beans.MMBaseNodeMapper;
import com.finalist.cmsc.paging.PagingStatusHolder;
import com.finalist.cmsc.paging.PagingUtils;
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
      return MMBaseNodeMapper.convertList(list, Term.class);
   }

   public List<Newsletter> getNewsletterByConstraint(String property, String constraintType, String value,
         boolean paging) {
      PagingStatusHolder pagingHolder = PagingUtils.getStatusHolder();
      NodeQuery query = cloud.createNodeQuery();
      NodeManager nodeManager = cloud.getNodeManager("newsletter");
      Step step = query.addStep(nodeManager);
      query.setNodeStep(step);
      if (StringUtils.isNotBlank(property)) {
         if (constraintType.equals("like")) {
            SearchUtil.addLikeConstraint(query, nodeManager.getField(property), value);
         }
      }
      if (pagingHolder != null && nodeManager.hasField(pagingHolder.getSort())) {
         Queries.addSortOrders(query, pagingHolder.getSort(), pagingHolder.getMMBaseDirection());
      } else if (null != pagingHolder.getSort()) {
         paging = false;
      }
      if (paging) {
         query.setMaxNumber(pagingHolder.getPageSize());
         query.setOffset(pagingHolder.getOffset());
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
      Newsletter newsletter = convertFromNode(newsletterNode);

      NodeList terms = newsletterNode.getRelatedNodes("term");
      log.debug("get newsletter by id:" + id + ",and get " + terms.size() + " terms with it.");
      List<Term> termList = MMBaseNodeMapper.convertList(terms, Term.class);
      newsletter.getTerms().addAll(termList);

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

   public List<Term> getNewsletterTermsByName(int newsletterId, String name, boolean paging) {
      PagingStatusHolder pagingHolder = PagingUtils.getStatusHolder();

      NodeManager termNodeManager = cloud.getNodeManager("term");
      NodeManager newsletterNodeManager = cloud.getNodeManager("newsletter");

      NodeQuery query = cloud.createNodeQuery();
      Step newsletterStep = query.addStep(newsletterNodeManager);
      query.setNodeStep(newsletterStep);
      Constraint idConstraint = SearchUtil.createEqualConstraint(query, newsletterNodeManager.getField("number"),
            newsletterId);
      SearchUtil.addConstraint(query, idConstraint);

      RelationStep relationStep = query.addRelationStep(termNodeManager, "posrel", "destination");
      Step termStep = relationStep.getNext();
      query.setNodeStep(termStep);
      if (StringUtils.isNotBlank(name)) {
         SearchUtil.addLikeConstraint(query, termNodeManager.getField("name"), name);
      }
      if (paging) {
         query.setMaxNumber(pagingHolder.getPageSize());
         query.setOffset(pagingHolder.getOffset());
      }
      Queries.addSortOrders(query, pagingHolder.getSort(), pagingHolder.getMMBaseDirection());
      List<Node> nodeList = query.getList();
      List<Term> terms = new ArrayList<Term>();
      for (Node termNode : nodeList) {
         int tmpNum = termNode.getNumber();
         String tmpName = termNode.getStringValue("name");
         Term term = new Term();
         term.setId(tmpNum);
         term.setName(tmpName);
         terms.add(term);

      }
      return terms;
   }

   public void processBouncesOfPublication(String publicationId, String userId, String bounceContent) {
      NodeManager bounceManager = cloud.getNodeManager("newsletterbounce");
      Node node = bounceManager.createNode();
      if (StringUtils.isNotEmpty(publicationId)) {
         node.setIntValue("newsletter", Integer.parseInt(publicationId));
      }
      if (StringUtils.isNotEmpty(userId)) {
         node.setIntValue("userid", Integer.parseInt(userId));
      }
      node.setStringValue("content", bounceContent);
      node.setDateValue("bouncedate", new Date());
      node.commit();

   }
}
