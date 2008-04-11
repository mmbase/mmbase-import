package com.finalist.newsletter.services;

import com.finalist.cmsc.services.community.person.Person;
import com.finalist.newsletter.cao.NewsletterPublicationCAO;
import com.finalist.newsletter.cao.NewsletterSubscriptionCAO;
import com.finalist.newsletter.cao.NewsLetterStatisticCAO;
import com.finalist.newsletter.cao.impl.NewsletterPublicationCAOImpl;
import com.finalist.newsletter.cao.impl.NewsletterSubscriptionCAOImpl;
import com.finalist.newsletter.cao.impl.NewsLetterStatisticCAOImpl;
import com.finalist.newsletter.domain.Publication;
import com.finalist.newsletter.domain.Subscription;
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

   public void testDeliverAllPublication() {
      NewsletterPublicationServiceImpl service = new NewsletterPublicationServiceImpl();

      NewsletterPublicationCAO publicationCAO = (NewsletterPublicationCAO) mockController.getMock(NewsletterPublicationCAOImpl.class);
      NewsletterSubscriptionCAO subscriptionCAO = (NewsletterSubscriptionCAO) mockController.getMock(NewsletterSubscriptionCAOImpl.class);
      NewsLetterStatisticCAO statisticCAO = (NewsLetterStatisticCAO) mockController.getMock(NewsLetterStatisticCAOImpl.class);
      FakeNewsletterPublisher publisher = new FakeNewsletterPublisher();

      mockController.expect(new NewsletterPublicationCAOImpl() {
         public List<Publication> getIntimePublication() {
            List<Publication> pubs = new ArrayList<Publication>();
            addPublication(pubs, 1);
            addPublication(pubs, 3);
            return pubs;
         }
      });


      mockController.expect(new NewsletterSubscriptionCAOImpl() {
         public List<Subscription> getSubscribers(int newsletterId) {
            assertEquals(1, newsletterId);
            Subscription subscriptioin = new Subscription();
            return Collections.singletonList(subscriptioin);
         }
      });

      mockController.expect(new NewsLetterStatisticCAOImpl() {
         public void logPubliction(int id, int i) {
            assertEquals(1, id);
            assertEquals(1, i);
         }

      });
      mockController.expect(new NewsletterPublicationCAOImpl() {
         public void setStatus(Publication publication, Publication.STATUS status) {
            assertNotNull(publication);
            assertEquals(1, publication.getId());
            assertNotNull(status);
            assertEquals(status, Publication.STATUS.DELIVERED);
         }
      });


      mockController.expect(new NewsletterSubscriptionCAOImpl() {
         public List<Subscription> getSubscribers(int newsletterId) {
            assertEquals(3, newsletterId);
            Subscription subscriptioin = new Subscription();
            return Collections.singletonList(subscriptioin);
         }
      }

      );

      mockController.expect(new

            NewsLetterStatisticCAOImpl() {
               public void logPubliction(int id, int i) {
                  assertEquals(3, id);
                  assertEquals(1, i);
               }

            }

      );
      mockController.expect(new

            NewsletterPublicationCAOImpl() {
               public void setStatus(Publication publication, Publication.STATUS status) {
                  assertNotNull(publication);
                  assertEquals(3, publication.getId());
                  assertNotNull(status);
                  assertEquals(status, Publication.STATUS.DELIVERED);
               }
            }

      );


      service.setPublicationCAO(publicationCAO);
      service.setSubscriptionCAO(subscriptionCAO);
      service.setStatisticCAO(statisticCAO);
      service.setMailSender(publisher);

      service.deliverAllPublication();

      mockController.verify();

      assertEquals(2, publisher.getMap()

            .

                  keySet()

            .

            size()

      );

      assertEquals(1, publisher.getMap()

            .

                  get(1)

            .

            size()

      );

      assertEquals(1, publisher.getMap()

            .

                  get(3)

            .

            size()

      );

   }

   private void addPublication(List<Publication> pubs, int id) {
      Publication publication = new Publication();
      publication.setId(id);
      publication.setNewsletterId(id);
      pubs.add(publication);
   }
}
