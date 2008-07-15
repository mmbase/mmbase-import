package com.finalist.newsletter.cao;

import com.finalist.newsletter.BaseNewsletterTest;
import com.finalist.newsletter.domain.Publication;
import static com.finalist.newsletter.domain.Publication.*;
import com.finalist.newsletter.cao.impl.NewsletterPublicationCAOImpl;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.Node;
import org.dbunit.operation.DatabaseOperation;

import java.util.List;

public class PublicationCAOTest extends BaseNewsletterTest {

   NewsletterPublicationCAOImpl cao;
   NodeManager manager;

   public void setUp() throws Exception {
      super.setUp();
      clearAllNode("newsletterpublication");

      cao = (NewsletterPublicationCAOImpl) context.getBean("publicationCAO");
      manager = cloud.getNodeManager("newsletterpublication");
      dbtemp.execute("newsletter_service_ds.xml", DatabaseOperation.CLEAN_INSERT);
   }

   protected void tearDown() throws Exception {
      super.tearDown();
   }

   public void testGetIntimePublication() {
      createPublicationNode(STATUS.DELIVERED);
      createPublicationNode(STATUS.DELIVERED);
      createPublicationNode(STATUS.READY);

   }

   public void testSetStatus() {
      Node pubNode = createPublicationNode(STATUS.READY);

      Publication publication = new Publication();
      publication.setId(pubNode.getNumber());
      assertEquals(STATUS.DELIVERED.toString(), cloud.getNode(pubNode.getNumber()).getStringValue("status"));
   }

   public void testGetNewsletterId(){
      assertEquals(33111,cao.getNewsletterId(11133));
   }

   private Node createPublicationNode(STATUS status) {
      Node pubNode = manager.createNode();
      pubNode.setStringValue("status", status.toString());
      pubNode.commit();
      return pubNode;
   }

}
