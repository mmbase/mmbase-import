package com.finalist.newsletter.cao.impl;

import com.finalist.cmsc.services.community.person.Person;
import com.finalist.newsletter.cao.NewsletterSubscriptionCAO;
import com.finalist.newsletter.cao.util.NewsletterSubscriptionUtil;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.util.POConvertUtils;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.Constraint;
import org.mmbase.storage.search.Step;
import org.mmbase.module.corebuilders.InsRel;
import org.apache.commons.beanutils.ConvertUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class NewsletterSubscriptionCAOImpl implements NewsletterSubscriptionCAO {

   private Cloud cloud;

   public void setCloud(Cloud cloud) {
      this.cloud = cloud;
   }

   public NewsletterSubscriptionCAOImpl(Cloud cloud) {
      this.cloud = cloud;
   }

   public List<Newsletter> getAllNewsletter() {
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
         letter = NewsletterSubscriptionUtil.convertNodeListtoTagList(taglist, letter);
         list.add(letter);
      }
      return list;
   }

   public List<Subscription> getSubscription(int newsletterId) {
      NodeManager subscriptionManager = cloud.getNodeManager("subscriptionrecord");
      NodeManager newsletterManager = cloud.getNodeManager("newsletter");

      NodeQuery query = cloud.createNodeQuery();
      query.addStep(subscriptionManager);
      query.addRelationStep(newsletterManager);
      SearchUtil.addEqualConstraint(query,newsletterManager.getField("number"),newsletterId);

      return POConvertUtils.convertSubscriptions(query.getList());
   }


   public Newsletter getNewsletterById(int id) {

      Newsletter newsletter = new Newsletter();
      Node node = cloud.getNode(id);
      newsletter = NewsletterSubscriptionUtil.populateNewsletter(node, newsletter);
      NodeManager tagManager = cloud.getNodeManager("tag");
      NodeList taglist = node.getRelatedNodes(tagManager);
      newsletter = NewsletterSubscriptionUtil.convertNodeListtoTagList(taglist, newsletter);
      return newsletter;
   }

   public NewsletterSubscriptionCAOImpl() {

   }

   public List<Newsletter> getUserSubscriptionList(String userName) {
      List<Newsletter> list = new ArrayList<Newsletter>();
      Newsletter newsletter = new Newsletter();

      List<Node> resluts = new ArrayList<Node>();
      resluts = querySubcriptionByUser(userName);
      Iterator<Node> nodes = resluts.iterator();
      for (int i = 0; i < resluts.size(); i++) {
         Node node = nodes.next();
         int a = node.getNumber();
         String status = node.getStringValue("status");
         newsletter.setStatus(status);
         Date interval = node.getDateValue("interval");
         newsletter.setInterval(interval);
         String format = node.getStringValue("format");
         newsletter.setFormat(format);

         NodeList newsletters = node.getRelatedNodes("newsletter");
         Iterator<Node> newsletterIterator = newsletters.iterator();
         for (int j = 0; j < newsletters.size(); j++) {
            String title = newsletterIterator.next().getStringValue("title");
            newsletter.setTitle(title);
         }

         NodeManager tagManager = cloud.getNodeManager("tag");
         NodeList tags = node.getRelatedNodes(tagManager);
         Iterator<Node> tagsItetator = tags.iterator();
         for (int y = 0; y < tags.size(); y++) {
            String tag = tagsItetator.next().getStringValue("name");
            newsletter.setTags(tags);
         }
         list.add(newsletter);
      }

      return list;
   }

   public List<Node> querySubcriptionByUser(String userName) {
      List<Node> results = null;
      NodeManager recordManager = cloud.getNodeManager("subscriptionrecord");
      NodeQuery query = cloud.createNodeQuery();
      String subscriber = "subscriber";
      Step theStep = null;
      theStep = query.addStep(recordManager);
      query.setNodeStep(theStep);
      Field field = recordManager.getField(subscriber);
      Constraint titleConstraint = SearchUtil.createLikeConstraint(query, field, userName);
      SearchUtil.addConstraint(query, titleConstraint);
      return query.getList();

   }

   public void updateSubscriptionRecord(Node node, String status) {
      node.setStringValue("status", status);
      node.commit();
   }


}
