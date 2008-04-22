package com.finalist.newsletter.cao.impl;

import com.finalist.newsletter.BaseNewsletterTest;
import com.finalist.newsletter.domain.Publication;
import static com.finalist.newsletter.domain.Publication.STATUS;
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

      cao = new NewsletterPublicationCAOImpl(cloud){
         protected String getNewsletterPath(Node newsletterPublicationNode) {
            return "/publication/pub";
         }

         protected String getHostUrl() {
            return "www.iamhosting.com";
         }
      };

      NodeManager letterManager = cloud.getNodeManager("newsletter");
      Node newsletter = letterManager.createNode();
      newsletter.setStringValue("from_mail", "from_mail@mail.com");
      newsletter.setStringValue("from_name", "from_name");
      newsletter.setStringValue("replyto_mail", "replyto_mail@mail.com");
      newsletter.setStringValue("replyto_name", "replayto_name");
      newsletter.commit();

      NodeManager publicationManager = cloud.getNodeManager("newsletterpublication");
      Node relatedNode = publicationManager.createNode();
      relatedNode.setStringValue("status", STATUS.INITIAL.toString());
      relatedNode.commit();

      Node unrelatedNode = publicationManager.createNode();
      unrelatedNode.commit();

      RelationManager relManager = cloud.getRelationManager("newsletter","newsletterpublication","related");
      relManager.createRelation(newsletter,relatedNode).commit();

      Publication publication = cao.getPublication(relatedNode.getNumber());
      assertEquals("www.iamhosting.com/publication/pub",publication.getUrl());
      assertEquals(STATUS.INITIAL,publication.getStatus());
      assertEquals(relatedNode.getNumber(),publication.getId());

      assertEquals("from_mail@mail.com",publication.getNewsletter().getFromAddress());
      assertEquals("from_name",publication.getNewsletter().getFromName());
      assertEquals("replyto_mail@mail.com",publication.getNewsletter().getReplyAddress());
      assertEquals("replayto_name",publication.getNewsletter().getReplyName());

      assertEquals(newsletter.getNumber(),publication.getNewsletter().getId());

   }

}
