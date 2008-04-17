package com.finalist.newsletter.cao.impl;

import com.finalist.newsletter.BaseNewsletterTest;
import com.finalist.newsletter.domain.Publication;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.RelationManager;

public class NewsletterPublicationCAOImplTest extends BaseNewsletterTest {

   NewsletterPublicationCAOImpl cao ;

   protected void setUp() throws Exception {
      super.setUp();
      cao = new NewsletterPublicationCAOImpl(cloud);
   }

   public void testGetPublication() {
      NodeManager letterManager = cloud.getNodeManager("newsletter");
      Node newsletter = letterManager.createNode();
      newsletter.setStringValue("from_mail", "from_mail@mail.com");
      newsletter.setStringValue("from_name", "from_name");
      newsletter.setStringValue("replyto_mail", "replyto_mail@mail.com");
      newsletter.setStringValue("replyto_name", "replayto_name");
      newsletter.commit();

      NodeManager subscriptionmanager = cloud.getNodeManager("newsletterpublication");
      Node relatedNode = subscriptionmanager.createNode();
      relatedNode.commit();

      Node unrelatedNode = subscriptionmanager.createNode();
      unrelatedNode.commit();

      RelationManager relManager = cloud.getRelationManager("newsletter","subscriptionrecord","newslettered");
      relManager.createRelation(newsletter,relatedNode).commit();

      Publication publication = cao.getPublication(relatedNode.getNumber());
   }
}
