package com.finalist.newsletter.publisher;

import junit.framework.TestCase;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;

import com.finalist.newsletter.domain.Publication;
import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.domain.Newsletter;

public class NewsletterPublisherTest extends TestCase {

   public void testDeliverSingle(){
      NewsletterPublisher publisher = new NewsletterPublisher(){
         protected void send(Message message) throws MessagingException {
            assertNotNull(message);
         }

         protected void setBody(Message message, Publication publication, Subscription subscription) throws MessagingException {
            message.setText("testText");
         }

         protected Session getMailSession() {
            return null;
         }
      };

      Subscription subscription = new Subscription();
      subscription.setNewsletter(new Newsletter());
      publisher.deliver(new Publication(),subscription);
   }
}
