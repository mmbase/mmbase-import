package com.finalist.newsletter.cao;

import com.finalist.newsletter.BaseNewsletterTest;
import com.finalist.newsletter.domain.Publication;
import com.finalist.newsletter.cao.impl.NewsletterPublicationCAOImpl;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.Node;

public class PublicationCAOTest extends BaseNewsletterTest{

   NewsletterPublicationCAOImpl cao ;


   public void testGetIntimePublication(){
      cao = new NewsletterPublicationCAOImpl(cloud);
   }
   public void testSetStatus(){
      NodeManager manager = cloud.getNodeManager("newsletterpublication");
      Node pubNode = manager.createNode();
      pubNode.setStringValue("status","init");
      pubNode.commit();

      Publication publication =new Publication();
      publication.setId(pubNode.getNumber());
      cao.setStatus(publication, Publication.STATUS.DELIVERED);
      assertEquals(Publication.STATUS.DELIVERED,cloud.getNode(pubNode.getNumber()).getStringValue("status"));
   }

}
