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
   }

   public void testGetNewsletterById() {
	   NodeManager letterManager = cloud.getNodeManager("newsletter");
		Node node = letterManager.createNode();
		node.setStringValue("title", "food");
		node.commit();

		NodeManager termManager = cloud.getNodeManager("term");

		Node term = termManager.createNode();
		term.setStringValue("name", "meet");
		term.commit();
		Node term2 = termManager.createNode();
		term2.setStringValue("name", "bread");
		term2.commit();
		

		RelationManager insrel = cloud.getRelationManager("newsletter","term", "termed");
		
		node.createRelation(term, insrel).commit();
		node.createRelation(term2, insrel).commit();
		

      assertNotNull(cao.getNewsletterById(node.getNumber()));
	  assertEquals(2,cao.getNewsletterById(node.getNumber()).getTerms().size());
   }

}
