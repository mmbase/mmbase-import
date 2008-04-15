package com.finalist.newsletter.cao;

import java.util.List;

import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.RelationManager;

import com.finalist.newsletter.cao.impl.NewsletterSubscriptionCAOImpl;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.BaseNewsletterTest;

import junit.framework.TestCase;

public class SubscriptionCAOTest extends BaseNewsletterTest {

   NewsletterSubscriptionCAO cao;

   public void setUp() throws Exception {
      super.setUp();
      cao = new NewsletterSubscriptionCAOImpl(cloud);
   }

   public void testGetNewsletterById() {
      int number = initNewsletters();

      Newsletter letter = cao.getNewsletterById(number);
      assertNotNull(letter);
      assertEquals("testtitle", letter.getTitle());
      assertEquals(2, letter.getTags().size());
      assertEquals("namet", letter.getTags().get(0).getName());

   }

   private int initNewsletters() {
      NodeManager letterManager = cloud.getNodeManager("newsletter");
      Node node = letterManager.createNode();
      node.setStringValue("title", "testtitle");
      node.commit();


      NodeManager tagManager = cloud.getNodeManager("tag");

      Node tag1 = tagManager.createNode();
      tag1.commit();

      RelationManager insrel = cloud.getRelationManager(letterManager, tagManager, "related");
      insrel.createRelation(node, tag1);
      insrel.commit();

//		Lettermanager.createRelation(tag1, cloud.getRelationManager("newslettertheme")).commit();

      List a = cloud.getRelationManagers();
      return node.getNumber();
   }

   public void testGetSubscription() {

      NodeManager letterManager = cloud.getNodeManager("newsletter");
      Node node = letterManager.createNode();
      node.setStringValue("title", "testtitle");
      node.commit();

      NodeManager subscriptionmanager = cloud.getNodeManager("subscriptionrecord");
      Node snode = subscriptionmanager.createNode();
      snode.setStringValue("status","active");
      snode.commit();

      Node snode2 = subscriptionmanager.createNode();
      snode2.setStringValue("status","inactive");
      snode2.commit();

      Node snode3 = subscriptionmanager.createNode();
      snode3.setStringValue("status","active");
      snode3.commit();

      RelationManager relManager = cloud.getRelationManager("newsletter","subscriptionrecord","newslettered");
      relManager.createRelation(node,snode).commit();
      relManager.createRelation(node,snode2).commit();
      relManager.createRelation(node,snode3).commit();

      List<Subscription> subscriptions = cao.getSubscription(node.getNumber());
      assertEquals(2,subscriptions.size());
   }

}
