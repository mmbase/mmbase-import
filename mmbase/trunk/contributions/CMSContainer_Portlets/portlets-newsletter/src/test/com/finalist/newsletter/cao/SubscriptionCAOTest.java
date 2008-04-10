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

import junit.framework.TestCase;

public class SubscriptionCAOTest extends TestCase {

   Cloud cloud;
   NewsletterSubscriptionCAO cao;

   public void setUp() {
      CloudProvider provider = CloudProviderFactory.getCloudProvider();
      cloud = provider.getCloud();
      cao = new NewsletterSubscriptionCAOImpl();
   }

   public void teset() {
      CloudProvider provider = CloudProviderFactory.getCloudProvider();
      Cloud cloud = provider.getCloud();
      NodeManager manager = cloud.getNodeManager("article");
      Node node = manager.createNode();
      node.setStringValue("title", "3333333");
      node.setStringValue("intro", "introintrointrointrointro");
      node.commit();
      System.out.println("1111111111");
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

}
