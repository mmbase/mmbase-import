package com.finalist.newsletter;

import org.mmbase.core.event.Event;
import org.mmbase.core.event.NodeEvent;
import org.mmbase.core.event.NodeEventListener;

import com.finalist.newsletter.publisher.NewsletterPublisher;

public class NewsletterPublicationListener implements NodeEventListener {

   public void notify(NodeEvent event) {
      if (event.getType() == Event.TYPE_NEW) {
         int publicationNumber = event.getNodeNumber();
//         Thread publisher = new NewsletterPublisher(publicationNumber);
//         publisher.start();
      }
   }
}