package com.finalist.newsletter.cao.impl;

import static com.finalist.newsletter.util.NewsletterSubscriptionUtil.convertFromNode;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.Query;
import org.mmbase.bridge.RelationManager;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.Constraint;
import org.mmbase.storage.search.FieldCompareConstraint;
import org.mmbase.storage.search.RelationStep;
import org.mmbase.storage.search.Step;
import org.mmbase.storage.search.StepField;
import org.mmbase.storage.search.implementation.BasicCompositeConstraint;
import org.mmbase.storage.search.implementation.BasicFieldValueConstraint;

import com.finalist.cmsc.beans.MMBaseNodeMapper;
import com.finalist.cmsc.paging.PagingStatusHolder;
import com.finalist.cmsc.paging.PagingUtils;
import com.finalist.cmsc.util.DateUtil;
import com.finalist.newsletter.cao.AbstractCAO;
import com.finalist.newsletter.cao.NewsletterSubscriptionCAO;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.domain.Term;
import com.finalist.newsletter.domain.Subscription.STATUS;

public class NewsletterSubscriptionCAOImpl extends AbstractCAO implements NewsletterSubscriptionCAO {

   private static Log log = LogFactory.getLog(NewsletterSubscriptionCAOImpl.class);

   public NewsletterSubscriptionCAOImpl() {
   }

   public NewsletterSubscriptionCAOImpl(Cloud cloud) {
      this.cloud = cloud;
   }

   public void setCloud(Cloud cloud) {
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
      Constraint titleConstraint = SearchUtil.createEqualConstraint(query, field, Integer.toString(userId));
      SearchUtil.addConstraint(query, titleConstraint);
      return query.getList();
   }

   public void addSubscriptionRecord(Subscription subscription, int userId) {
      String nodeType = "subscriptionrecord";
      NodeManager subscriptionrecordNodeManager = cloud.getNodeManager(nodeType);
      Node subscriptionrecordNode = subscriptionrecordNodeManager.createNode();

      subscriptionrecordNode.setIntValue("subscriber", userId);
      subscriptionrecordNode.setStringValue("status", subscription.getStatus().toString());
      subscriptionrecordNode.setStringValue("format", subscription.getMimeType());
      subscriptionrecordNode.commit();

      // add Relation to newsletter
      int nodeNumber = subscription.getNewsletter().getId();
      Node newsletternode = cloud.getNode(nodeNumber);
      RelationManager insrel = cloud.getRelationManager("subscriptionrecord", "newsletter", "newslettered");
      subscriptionrecordNode.createRelation(newsletternode, insrel).commit();
      subscription.setId(subscriptionrecordNode.getNumber());
   }

   public void modifySubscriptionStauts(Subscription subscription) {
      log.debug("Modify subscription status " + subscription.getId() + " to " + subscription.getStatus());
      String stauts = subscription.getStatus().toString();

      Node record = cloud.getNode(subscription.getId());
      record.setStringValue("status", stauts);
      record.commit();

      if ("INACTIVE".equals(stauts)) {
         record.deleteRelations("termed");
         record.commit();
      }

   }

   public void pause(int subscriptionId) {
      Node record = cloud.getNode(subscriptionId);
      record.setStringValue("status", "PAUSED");
      record.commit();
   }

   public void modifySubscriptionFormat(Subscription subscription) {
      int recordId = subscription.getId();
      Node record = cloud.getNode(recordId);

      String format = subscription.getMimeType();
      record.setStringValue("format", format);
      record.commit();
   }

   public void addSubscriptionTerm(Subscription subscription, int termId) {
      int recordId = subscription.getId();
      Node record = cloud.getNode(recordId);
      Node term = cloud.getNode(termId);

      RelationManager insrel = cloud.getRelationManager("subscriptionrecord", "term", "termed");
      record.createRelation(term, insrel).commit();
   }

   public void removeSubscriptionTerm(Subscription subscription, int termId) {
      int recordId = subscription.getId();
      Node record = cloud.getNode(recordId);

      List<Node> terms = record.getRelatedNodes("term");
      Iterator<Node> termsit = terms.iterator();
      record.deleteRelations("termed");
      for (int i = 0; i < terms.size(); i++) {
         Node term = termsit.next();
         if (termId != term.getNumber()) {
            RelationManager insrel = cloud.getRelationManager("subscriptionrecord", "term", "termed");
            record.createRelation(term, insrel).commit();
         }
      }
   }

   public Subscription getSubscription(int newsletterId, int userId) {
      log.debug("getSubscriptionrecord that newsletterId=" + newsletterId);

      Node subscriptionNode = null;
      List<Node> records = cloud.getNode(newsletterId).getRelatedNodes("subscriptionrecord");
      for (Node record : records) {
         if (record.getStringValue("subscriber").equals(Integer.toString(userId))) {
            subscriptionNode = record;
            break;
         }
      }

      if (null != subscriptionNode) {
         Subscription subscription = new Subscription();
         int subscriptionId = subscriptionNode.getIntValue("number");

         subscription.setId(subscriptionId);
         subscription.setMimeType(subscriptionNode.getStringValue("format"));
         subscription.setStatus(STATUS.valueOf(subscriptionNode.getStringValue("status")));
         subscription.setResumeDate(DateUtil.parser(subscriptionNode.getDateValue("pausetill")));
         List<Node> terms = subscriptionNode.getRelatedNodes("term");
         log.debug("Get subscription successful and get " + terms.size() + " term with it");
         for (Node termNode : terms) {
            Term term = new Term();
            term.setId(termNode.getNumber());
            term.setName(termNode.getStringValue("name"));
            term.setSubscription(true);
            subscription.getTerms().add(term);
         }
         return subscription;
      } else {
         log.debug("Get subscription failed,user " + userId + " may not subscripbe " + newsletterId);
         return null;
      }
   }

   public Set<Term> getTerms(int subscriptionId) {

      List<Node> termList = cloud.getNode(subscriptionId).getRelatedNodes("term");
      Set<Term> terms = new HashSet<Term>();

      for (Node termNode : termList) {
         Term term = new Term();
         term.setId(termNode.getNumber());
         term.setName(termNode.getStringValue("name"));
         term.setSubscription(true);
         terms.add(term);
      }

      return terms;
   }

   public List<Subscription> getSubscription(int newsletterId) {

      List<Node> records = cloud.getNode(newsletterId).getRelatedNodes("subscriptionrecord");
      log.debug("Get subscriptions of newsletter:" + newsletterId + " and get " + records.size() + " records in all");
      List<Subscription> subscribers = new ArrayList<Subscription>();
      for (Node record : records) {
         String status = record.getStringValue("status");
         if (STATUS.ACTIVE.equals(STATUS.valueOf(status))) {
            subscribers.add(convertFromNode(record));
         }
      }

      return subscribers;
   }

   public void createSubscription(int userId, int newsletterId) {
      log.debug("Create subscription user:" + userId + " newsletter:" + newsletterId);

      NodeManager recordManager = cloud.getNodeManager("subscriptionrecord");
      Node newsletter = cloud.getNode(newsletterId);

      Node recordNode = recordManager.createNode();
      recordNode.setStringValue("status", Subscription.STATUS.ACTIVE.toString());
      recordNode.setStringValue("subscriber", Integer.toString(userId));
      recordNode.setStringValue("format", "text/html");
      recordNode.commit();

      RelationManager insrel = cloud.getRelationManager("subscriptionrecord", "newsletter", "newslettered");
      recordNode.createRelation(newsletter, insrel).commit();

   }

   public Subscription getSubscriptionById(int id) {
      Node subscriptionNode = cloud.getNode(id);
      Subscription subscription = convertFromNode(subscriptionNode);
      List<Node> newsletterNodes = subscriptionNode.getRelatedNodes("newsletter");

      Newsletter newsletter = new Newsletter();
      newsletter.setTitle(newsletterNodes.get(0).getStringValue("subject"));
      subscription.setNewsletter(newsletter);

      return subscription;
   }

   public void updateSubscription(Subscription subscription) {
      Node node = cloud.getNode(subscription.getId());
      node.setStringValue("status", subscription.getStatus().toString());
      node.setDateValue("pausetill", subscription.getPausedTill());
      node.commit();

   }

   public List<Subscription> getSubscriptionByUserIdAndStatus(int userId, STATUS status) {

      NodeManager recordManager = cloud.getNodeManager("subscriptionrecord");

      Query query = recordManager.createQuery();
      if (null != status) {
         SearchUtil.addEqualConstraint(query, recordManager.getField("status"), status.toString());
      }
      SearchUtil.addEqualConstraint(query, recordManager.getField("subscriber"), Integer.toString(userId));

      List<Node> subscriptions = query.getList();

      List<Subscription> subs = new ArrayList<Subscription>();
      for (Node node : subscriptions) {
         subs.add(convertFromNode(node));
      }

      return subs;
   }

   public List<Node> getAllSubscriptions() {

      NodeQuery query = cloud.createNodeQuery();
      Step step = query.addStep(cloud.getNodeManager("subscriptionrecord"));
      query.setNodeStep(step);
      List<Node> list = query.getList();

      return list;
   }

   public Set<Node> getRecordByNewsletterAndName(int newsletterId, String termName) {
      NodeManager manager = cloud.getNodeManager("term");
      Node newsletterNode = cloud.getNode(newsletterId);
      NodeQuery nodeQuery = SearchUtil.createRelatedNodeListQuery(newsletterNode, "term", "posrel");

      if (StringUtils.isNotBlank(termName)) {
         Step step = nodeQuery.getNodeStep();
         StepField fieldName = nodeQuery.addField(step, manager.getField("name"));
         BasicFieldValueConstraint constraintTitle = new BasicFieldValueConstraint(fieldName, "%" + termName + "%");
         constraintTitle.setOperator(FieldCompareConstraint.LIKE);
         BasicCompositeConstraint constraints = new BasicCompositeConstraint(2);
         constraints.addChild(constraintTitle);
         nodeQuery.setConstraint(constraints);
      }
      List<Node> list = nodeQuery.getList();
      Set<Node> results = new HashSet<Node>();
      List<Node> recordList = null;

      for (Node termNode : list) {
         recordList = termNode.getRelatedNodes("subscriptionrecord", "termed", "source");
         for (Node recordNode : recordList) {
            if (recordNode != null) {
               results.add(recordNode);
            }
         }
      }
      return results;
   }

   public Set<Node> getNewslettersByScriptionRecord(int authenticationId) {
      NodeManager recordManager = cloud.getNodeManager("subscriptionrecord");
      Query query = recordManager.createQuery();
      SearchUtil.addEqualConstraint(query, recordManager.getField("subscriber"), Integer.toString(authenticationId));
      List<Node> subscriptions = query.getList();
      Set<Node> newsletters = new HashSet<Node>();
      for (Node subscription : subscriptions) {
         List<Node> tmpNewsletters = subscription.getRelatedNodes("newsletter", "newslettered", "source");
         for (Node newsletter : tmpNewsletters) {
            if (newsletter != null)
               newsletters.add(newsletter);
         }
      }
      return newsletters;
   }

   public List<Newsletter> getNewslettersByScription(int subscriberId, String title, boolean paging){
      PagingStatusHolder pagingHolder = PagingUtils.getStatusHolder();
      
      NodeManager subscriptionNodeManager = cloud.getNodeManager("subscriptionrecord");
      NodeManager newsletterNodeManager = cloud.getNodeManager("newsletter");
      NodeQuery query = cloud.createNodeQuery();
      Step subscriptionStep = query.addStep(subscriptionNodeManager);
      query.setNodeStep(subscriptionStep);
      if(subscriberId>0){
         SearchUtil.addEqualConstraint(query, subscriptionNodeManager.getField("subscriber"), Integer.toString(subscriberId));
      }
      RelationStep newsletterRelStep = query.addRelationStep(newsletterNodeManager, "newslettered","source");
      Step newsletterStep = newsletterRelStep.getNext();
      query.setNodeStep(newsletterStep);
      if(StringUtils.isNotBlank(title)){
         SearchUtil.addLikeConstraint(query, newsletterNodeManager.getField("title"), title);
      }
      if (paging) {
         query.setMaxNumber(pagingHolder.getPageSize());
         query.setOffset(pagingHolder.getOffset());
      }
      if (null != pagingHolder) {
         Queries.addSortOrders(query, pagingHolder.getSort(), pagingHolder.getMMBaseDirection());
      }
      List<Node> results = query.getList();
      List<Newsletter> newsletters = MMBaseNodeMapper.convertList(results, Newsletter.class);
      return newsletters;
   }

   public Set<Node> getTermsByScriptionRecord(int authenticationId) {
      NodeManager recordManager = cloud.getNodeManager("subscriptionrecord");
      Query query = recordManager.createQuery();
      SearchUtil.addEqualConstraint(query, recordManager.getField("subscriber"), Integer.toString(authenticationId));
      List<Node> subscriptions = query.getList();
      Set<Node> terms = new HashSet<Node>();
      for (Node subscription : subscriptions) {
         List<Node> tmpTerms = subscription.getRelatedNodes("term", "termed", "destination");
         for (Node term : tmpTerms) {
            if (term != null)
               terms.add(term);
         }
      }
      return terms;
   }

   public void updateLastBounce(int subscriptionId) {
      // todo test.
      Node subscription = getSubscriptionNodeById(subscriptionId);
      if (subscription.getIntValue("count_bounces") > 0) {
         subscription.setIntValue("count_bounces", subscription.getIntValue("count_bounces") + 1);
      } else {
         subscription.setIntValue("count_bounces", 1);
      }
      subscription.setDateValue("last_bounce", new Date(System.currentTimeMillis()));
      subscription.commit();
   }

   private Node getSubscriptionNodeById(int subscriptionId) {
      return cloud.getNode(subscriptionId);
   }

   public Node getSubscriptionNode(int newsletterId, int userId) {
      Node subscriptionNode = null;

      List<Node> records = cloud.getNode(newsletterId).getRelatedNodes("subscriptionrecord");
      for (Node record : records) {
         if (record.getStringValue("subscriber").equals(Integer.toString(userId))) {
            subscriptionNode = record;
            break;
         }
      }

      if (null == subscriptionNode) {
         log.debug("Get subscription failed,user " + userId + " may not subscripbe " + newsletterId);
         return null;
      }

      return subscriptionNode;
   }

   public List<Node> getSubscriptionsByTerms(int newsletterId, String terms) {
      NodeManager termNodeManager = cloud.getNodeManager("term");
      NodeManager newsletterNodeManager = cloud.getNodeManager("newsletter");
      NodeManager subscriptionNodeManger = cloud.getNodeManager("subscriptionrecord");

      NodeQuery query = cloud.createNodeQuery();
      List<Node> subscriptions;
      if (StringUtils.isNotBlank(terms)) {
         Step termStep = query.addStep(termNodeManager);
         query.setNodeStep(termStep);
         String nameLikeStr = null;
         String[] tmpTerms = terms.split(" ");
         for (String termName : tmpTerms) {
            SearchUtil.addLikeConstraint(query, termNodeManager.getField("name"), termName);
         }
         RelationStep newsletterRelStep = query.addRelationStep(newsletterNodeManager, "posrel", "source");
         Step newsletterStep = newsletterRelStep.getNext();
         query.setNodeStep(newsletterStep);
         SearchUtil.addEqualConstraint(query, newsletterNodeManager.getField("number"), newsletterId);

         RelationStep subscriptionRelStep = query.addRelationStep(subscriptionNodeManger, "newslettered", "destination");
         Step subscriptionStep = subscriptionRelStep.getNext();
         query.setNodeStep(subscriptionStep);
         subscriptions = query.getList();
      } else {
         Node newsletterNode = cloud.getNode(newsletterId);
         subscriptions = newsletterNode.getRelatedNodes(subscriptionNodeManger, "newslettered", "destination");
      }

      return subscriptions;
   }

   public int countSubscription(int newsletterId) {
      List<Node> records = cloud.getNode(newsletterId).getRelatedNodes("subscriptionrecord");
      log.debug("Get subscriptions of newsletter:" + newsletterId + " and get " + records.size() + " records in all");
      int subscribers = 0;
      for (Node record : records) {
         String status = record.getStringValue("status");
         if (STATUS.ACTIVE.equals(STATUS.valueOf(status))) {
            subscribers++;
         }
      }
      return subscribers;
   }
}
