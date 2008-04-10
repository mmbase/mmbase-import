package com.finalist.newsletter.cao;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;

import com.finalist.newsletter.BaseNewsletterTest;
import com.finalist.newsletter.cao.impl.NewsletterCAOImpl;

public class NewsletterCAOTest extends BaseNewsletterTest {
   NewsletterCAOImpl cao;

   public void setUp() throws Exception {
      super.setUp();
      cao = new NewsletterCAOImpl();
      cao.setCloud(cloud);
   }

   public void testGetAllNewsletter() {
      assertEquals(8, cao.getAllNewsletters().size());
   }

   public void testGetNewsletterById() {
      NodeManager manager = cloud.getNodeManager("newsletter");
      Node node = manager.createNode();
      node.setStringValue("title", "testtitle");
      node.commit();

      assertNotNull(cao.getNewsletterById(node.getNumber()));
   }

}
