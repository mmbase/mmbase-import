package com.finalist.newsletter.cao;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.RelationManager;

import com.finalist.newsletter.BaseNewsletterTest;
import com.finalist.newsletter.cao.impl.NewsletterCAOImpl;

public class NewsletterCAOTest extends BaseNewsletterTest {
   NewsletterCAOImpl cao;

   public void setUp() throws Exception {
      super.setUp();
      cao = new NewsletterCAOImpl(cloud);
   }

   public void testGetAllNewsletter() {
      assertEquals(8, cao.getAllNewsletters().size());
   }

   public void testGetNewsletterById() {
	   NodeManager letterManager = cloud.getNodeManager("newsletter");
		Node node = letterManager.createNode();
		node.setStringValue("title", "food");
		node.commit();

		NodeManager tagManager = cloud.getNodeManager("tag");

		Node tag1 = tagManager.createNode();
		tag1.setStringValue("name", "meet");
		tag1.commit();
		Node tag2 = tagManager.createNode();
		tag2.setStringValue("name", "bread");
		tag2.commit();
		

		RelationManager insrel = cloud.getRelationManager("newsletter","tag", "tagged");
		
		node.createRelation(tag1, insrel).commit();
		node.createRelation(tag2, insrel).commit();
		

      assertNotNull(cao.getNewsletterById(node.getNumber()));
	  assertEquals(2,cao.getNewsletterById(node.getNumber()).getTags().size());	
   }

}
