package com.finalist.newsletter.cao.impl;

import com.finalist.newsletter.cao.NewsletterSubscriptionCAO;
import com.finalist.newsletter.cao.AbstractCAO;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.Subscription;
import static com.finalist.newsletter.domain.Subscription.STATUS;
import com.finalist.newsletter.domain.Term;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.StringUtils;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.Constraint;
import org.mmbase.storage.search.Step;

import static com.finalist.newsletter.util.NewsletterSubscriptionUtil.*;
import com.finalist.newsletter.util.DateUtil;

import java.util.*;

public class NewsletterSubscriptionCAOImpl extends AbstractCAO implements NewsletterSubscriptionCAO {

   private static Log log = LogFactory.getLog(NewsletterSubscriptionCAOImpl.class);


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
      record.setStringValue("status", "PAUSE");
      record.commit();
   }

   public Set<Node> getRecordByNewsletterAndName(int newsletterId, String termName) {
      return null;
   }

   public Set<Node> getNewslettersByScriptionRecord(int authenticationId) {
      return null;
   }

   public Set<Node> getTermsByScriptionRecord(int authenticationId) {
      return null;
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

      RelationManager insrel = cloud.getRelationManager(
            "subscriptionrecord", "term", "termed");
      record.createRelation(term, insrel).commit();
   }

   public void removeSubscriptionTerm(Subscription subscription, int termId) {
      int recordId = subscription.getId();
      Node record = cloud.getNode(recordId);

      List<Node> terms = record.getRelatedNodes("term");
      Iterator termsit = terms.iterator();
      record.deleteRelations("termed");
      for (int i = 0; i < terms.size(); i++) {
         Node term = (Node) termsit.next();
         if (termId != term.getNumber()) {
            RelationManager insrel = cloud.getRelationManager(
                  "subscriptionrecord", "term", "termed");
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
      }
      else {
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

   public void updateLastBounce(int subscriptionId) {
      //todo test.
      Node subscription = getSubscriptionNodeById(subscriptionId);
      subscription.setIntValue("count_bounces",subscription.getIntValue("count_bounces"));
      subscription.setDateValue("last_bounce",new Date(System.currentTimeMillis()));
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
}
