package com.finalist.newsletter;

import org.mmbase.core.event.NodeEvent;
import org.mmbase.core.event.NodeEventListener;
import org.mmbase.module.core.MMBase;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.navigation.ServerUtil;
import com.finalist.cmsc.repository.status.StatusCronJob;

public class NewsletterPublicationListener implements NodeEventListener {

	private static Logger log = Logging.getLoggerInstance(NewsletterPublicationListener.class.getName());

	public void notify(NodeEvent event) {
		log.debug("NewsletterPublicationListener invoked");
		if (ServerUtil.isLive()) {
			log.debug("The server is live");
			if (event.getType() == NodeEvent.TYPE_NEW) {
				log.debug("The event is of type " + NodeEvent.TYPE_NEW);
				int nodeNumber = event.getNodeNumber();
				String publicationNumber = String.valueOf(nodeNumber);
				NewsletterPublisher publisher = new NewsletterPublisher(publicationNumber);

				try {
					wait(10000);
				} catch (InterruptedException iex) {

				}
				publisher.startPublishing();
			} else {
				log.debug("The event is of type " + event.getBuilderName() + " and does not need processing");
			}
		} else {
			log.debug("The server is not live, processing of event not neccesary");
		}
	}
}
