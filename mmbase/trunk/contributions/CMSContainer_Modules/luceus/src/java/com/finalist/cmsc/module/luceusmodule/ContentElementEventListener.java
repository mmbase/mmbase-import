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
import org.mmbase.core.event.RelationEvent;
import org.mmbase.core.event.RelationEventListener;
import org.mmbase.module.core.MMBase;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;

/**
 * Handles events on content
 */
public class ContentElementEventListener implements NodeEventListener, RelationEventListener {

   protected final static String TYPE_CONTENT_ELEMENT = "contentelement";

   protected final static String TYPE_CONTENT_CHANNEL = "contentchannel";

   private LuceusModule module;

   private static Logger log = Logging.getLoggerInstance(ContentElementEventListener.class.getName());


   public ContentElementEventListener(LuceusModule module) {
      this.module = module;
      MMBase.getMMBase().addNodeRelatedEventsListener(TYPE_CONTENT_ELEMENT, this);
      log.info("registered listener for: " + TYPE_CONTENT_ELEMENT);
   }


   public void notify(NodeEvent event) {
      log.debug("ContentElementEventListener NodeEvent: " + event.getNodeNumber());
      switch (event.getType()) {
         case Event.TYPE_DELETE:
            module.deleteContentIndex(event.getNodeNumber());
            break;
         case Event.TYPE_NEW:
         case Event.TYPE_CHANGE:
            module.updateContentIndex(event.getNodeNumber());
            break;
      }
   }


   public void notify(RelationEvent event) {
      int ceNumber = 0;
      int cNumber = 0;

      String sourceType = event.getRelationSourceType();
      String destinationType = event.getRelationDestinationType();

      if (ContentElementUtil.isContentType(sourceType)) {
         ceNumber = event.getRelationSourceNumber();
      }
      else if (ContentElementUtil.isContentType(destinationType)) {
         ceNumber = event.getRelationDestinationNumber();
      }

      if (RepositoryUtil.isContentChannel("" + event.getRelationSourceNumber())) {
         cNumber = event.getRelationSourceNumber();
      }
      else if (RepositoryUtil.isContentChannel("" + event.getRelationDestinationNumber())) {
         cNumber = event.getRelationDestinationNumber();
      }

      if (cNumber > 0) {
         log.debug("ContentElementEventListener RelationEvent channel: " + cNumber + " with element " + ceNumber);
      }
      else {
         log.debug("ContentElementEventListener RelationEvent source: " + event.getRelationSourceNumber() + " to "
               + event.getRelationDestinationNumber());
      }

      if (ceNumber > 0) {
         switch (event.getType()) {
            case Event.TYPE_DELETE:
               if (cNumber > 0) {
                  module.deleteChannelContentIndex(cNumber, ceNumber);
               }
               break;
            case Event.TYPE_NEW:
            case Event.TYPE_CHANGE:
               module.updateContentIndex(ceNumber);
               break;
         }
      }
   }
}
