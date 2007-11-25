package com.finalist.newsletter;

import org.mmbase.core.event.EventListener;
import org.mmbase.module.Module;
import org.mmbase.module.core.MMBase;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class NewsletterModule extends Module {

	private static Logger log = Logging.getLoggerInstance(NewsletterModule.class.getName());

	@Override
	public void init() {
		String nodeName = "newsletterpublication";
		EventListener listener = new NewsletterPublicationListener();
		MMBase.getMMBase().addNodeRelatedEventsListener(nodeName, listener);
		log.debug("MMBase module NewsletterModule instantiated");
	}
}