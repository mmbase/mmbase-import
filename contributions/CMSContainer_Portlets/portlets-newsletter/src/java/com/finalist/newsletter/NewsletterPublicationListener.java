package com.finalist.newsletter;

import org.mmbase.core.event.Event;
import org.mmbase.core.event.NodeEvent;
import org.mmbase.core.event.NodeEventListener;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.navigation.ServerUtil;

public class NewsletterPublicationListener implements NodeEventListener {

   private static Logger log = Logging.getLoggerInstance(NewsletterPublicationListener.class.getName());

   public void notify(NodeEvent event) {
      log.debug("NewsletterPublicationListener invoked");
      if (ServerUtil.isLive()) {
         log.debug("The server is live");
         if (event.getType() == Event.TYPE_NEW) {
            log.debug("The event is of type " + Event.TYPE_NEW);
            int nodeNumber = event.getNodeNumber();
            String publicationNumber = String.valueOf(nodeNumber);
            Thread publisher = new NewsletterPublisher(publicationNumber);

            try {
               wait(10000);
            } catch (InterruptedException iex) {

            }
            publisher.start();
         } else {
            log.debug("The event is of type " + event.getBuilderName() + " and does not need processing");
         }
      } else {
         log.debug("The server is not live, processing of event not neccesary");
      }
   }
}
