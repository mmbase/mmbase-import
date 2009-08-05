package com.finalist.newsletter;

import org.mmbase.core.event.EventListener;
import org.mmbase.module.Module;
import org.mmbase.module.core.MMBase;

import com.finalist.cmsc.util.ServerUtil;

public class NewsletterModule extends Module {

   @Override
   public void init() {
      if (ServerUtil.isLive()) {
         String nodeName = "newsletterpublication";
         EventListener listener = new NewsletterPublicationListener();
         MMBase.getMMBase().addNodeRelatedEventsListener(nodeName, listener);
      }
   }
}