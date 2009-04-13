/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.directreaction;

import org.mmbase.core.event.Event;
import org.mmbase.core.event.RelationEvent;
import org.mmbase.core.event.RelationEventListener;
import org.mmbase.module.core.MMBase;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.directreaction.util.ReactionUtil;
import com.finalist.cmsc.repository.ContentElementUtil;

/**
 * Handles events on content
 */
public class ContentElementEventListener implements RelationEventListener {

   protected final static String TYPE_CONTENT_ELEMENT = "contentelement";

   private DirectreactionModule module;

   private static final Logger log = Logging.getLoggerInstance(ContentElementEventListener.class.getName());


   public ContentElementEventListener(DirectreactionModule module) {
      this.module = module;
      MMBase.getMMBase().addNodeRelatedEventsListener(TYPE_CONTENT_ELEMENT, this);
      log.info("registered listener for: " + TYPE_CONTENT_ELEMENT);
   }

   public void notify(RelationEvent event) {
      int ceNumber = 0;
      int reNumber = 0;

      String sourceType = event.getRelationSourceType();
      String destinationType = event.getRelationDestinationType();

      if (ContentElementUtil.isContentType(sourceType)) {
         ceNumber = event.getRelationSourceNumber();
      }
      else if (ContentElementUtil.isContentType(destinationType)) {
         ceNumber = event.getRelationDestinationNumber();
      }

      if (ReactionUtil.isReaction("" + event.getRelationSourceNumber())) {
         reNumber = event.getRelationSourceNumber();
      }
      else if (ReactionUtil.isReaction("" + event.getRelationDestinationNumber())) {
         reNumber = event.getRelationDestinationNumber();
      }

      if (reNumber > 0) {
         log.debug("ReactionEventListener RelationEvent contentelement: " + ceNumber + " with reaction " + reNumber);
      }
      else {
         log.debug("ReactionEventListener RelationEvent source: " + event.getRelationSourceNumber() + " to "
               + event.getRelationDestinationNumber());
      }

      if (ceNumber > 0) {
         if (event.getType() == Event.TYPE_DELETE) {
            if (reNumber > 0) {
               module.deleteContentReaction(reNumber);
            }
         }
      }
   }
}
