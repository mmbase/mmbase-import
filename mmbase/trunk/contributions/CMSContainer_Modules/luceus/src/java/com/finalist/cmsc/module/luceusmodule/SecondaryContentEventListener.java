/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.module.luceusmodule;

import org.mmbase.core.event.Event;
import org.mmbase.core.event.NodeEvent;
import org.mmbase.core.event.NodeEventListener;
import org.mmbase.module.core.MMBase;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @author Freek Punt
 * @author Wouter Heijke
 * @version $Revision $
 */
public class SecondaryContentEventListener implements NodeEventListener {

   private static Logger log = Logging.getLoggerInstance(SecondaryContentEventListener.class.getName());

   protected final static String TYPE_IMAGES = "images";

   protected final static String TYPE_URLS = "urls";

   protected final static String TYPE_ATTACHMENTS = "attachments";

   private LuceusModule module;


   public SecondaryContentEventListener(LuceusModule module) {
      this.module = module;
      if (module.isDoImages()) {
         addEventListener(TYPE_IMAGES);
      }
      if (module.isDoUrls()) {
         addEventListener(TYPE_URLS);
      }
      if (module.isDoAttachments()) {
         addEventListener(TYPE_ATTACHMENTS);
      }
   }


   private void addEventListener(String builder) {
      MMBase.getMMBase().addNodeRelatedEventsListener(builder, this);
      log.info("registered listener for: " + builder);
   }


   public void notify(NodeEvent event) {
      log.debug("SecondaryContentEventListener NodeEvent: " + event.getNodeNumber());

      switch (event.getType()) {
         // case Event.TYPE_DELETE:
         // TODO NIJ-341
         case Event.TYPE_NEW:
         case Event.TYPE_CHANGE:
            if (event.getNewValues().size() > 0 || event.getOldValues().size() > 0) {
               module.updateSecondaryContentIndex(event.getNodeNumber());
            }
            break;
      }
   }
}
