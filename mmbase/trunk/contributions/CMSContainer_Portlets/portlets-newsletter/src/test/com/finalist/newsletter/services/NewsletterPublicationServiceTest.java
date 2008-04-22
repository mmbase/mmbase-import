package com.finalist.newsletter.services;

import com.finalist.newsletter.cao.NewsLetterStatisticCAO;
import com.finalist.newsletter.cao.NewsletterPublicationCAO;
import com.finalist.newsletter.cao.NewsletterSubscriptionCAO;
import com.finalist.newsletter.cao.impl.NewsLetterStatisticCAOImpl;
import com.finalist.newsletter.cao.impl.NewsletterPublicationCAOImpl;
import com.finalist.newsletter.cao.impl.NewsletterSubscriptionCAOImpl;
import com.finalist.newsletter.domain.Publication;
import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.publisher.FakeNewsletterPublisher;
import com.finalist.newsletter.services.impl.NewsletterPublicationServiceImpl;
import com.sevenirene.archetype.testingplatform.impl.logic.mock.MockController;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NewsletterPublicationServiceTest extends TestCase {

   MockController mockController;

   public void setUp() {
      mockController = new MockController();
   }

   public void testDeliverTest() {
      NewsletterPublicationServiceImpl service = new NewsletterPublicationServiceImpl();
      FakeNewsletterPublisher fakepuFakeNewsletterPublisher = new FakeNewsletterPublisher();

      NewsletterPublicationCAO publicationCAO = (NewsletterPublicationCAO) mockController.getMock(NewsletterPublicationCAOImpl.class);
      service.setPublicationCAO(publicationCAO);
      service.setMailSender(fakepuFakeNewsletterPublisher);

      mockController.expect(new NewsletterPublicationCAOImpl() {
         public Publication getPublication(int number) {
            assertEquals(1, number);
            Publication publication = new Publication();

            Newsletter letter = new Newsletter();
            letter.setId(9999);

            publication.setNewsletter(letter);

            return publication;
         }
      });

      service.deliver(1, "test@test.test", "html");

      assertEquals("test@test.test", fakepuFakeNewsletterPublisher.subscription.getSubscriber().getEmail());
      assertEquals("html", fakepuFakeNewsletterPublisher.subscription.getMimeType());
      assertEquals(9999, fakepuFakeNewsletterPublisher.publication.getNewsletter().getId());

      mockController.verify();
   }


}
