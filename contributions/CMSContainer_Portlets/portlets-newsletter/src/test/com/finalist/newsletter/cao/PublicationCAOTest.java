package com.finalist.newsletter.cao;

import com.finalist.newsletter.BaseNewsletterTest;
import com.finalist.newsletter.domain.Publication;
import static com.finalist.newsletter.domain.Publication.*;
import com.finalist.newsletter.cao.impl.NewsletterPublicationCAOImpl;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.Node;

import java.util.List;

public class PublicationCAOTest extends BaseNewsletterTest {

   NewsletterPublicationCAOImpl cao;
   NodeManager manager;

   public void setUp() throws Exception {
      super.setUp();
      cao = new NewsletterPublicationCAOImpl(cloud);
      manager = cloud.getNodeManager("newsletterpublication");
      clearAllNode("newsletterpublication");
   }

   public void testGetIntimePublication() {
      createPublicationNode(STATUS.DELIVERED);
      createPublicationNode(STATUS.DELIVERED);
      createPublicationNode(STATUS.READY);

      List<Publication> publications = cao.getIntimePublication();
      assertEquals(1,publications.size());
      assertEquals(STATUS.READY,publications.get(0).getStatus());
   }

   public void testSetStatus() {
      Node pubNode = createPublicationNode(STATUS.READY);

      Publication publication = new Publication();
      publication.setId(pubNode.getNumber());
      cao.setStatus(publication, STATUS.DELIVERED);
      assertEquals(STATUS.DELIVERED.toString(), cloud.getNode(pubNode.getNumber()).getStringValue("status"));
   }

   private Node createPublicationNode(STATUS status) {
      Node pubNode = manager.createNode();
      pubNode.setStringValue("status", status.toString());
      pubNode.commit();
      return pubNode;
   }

}
