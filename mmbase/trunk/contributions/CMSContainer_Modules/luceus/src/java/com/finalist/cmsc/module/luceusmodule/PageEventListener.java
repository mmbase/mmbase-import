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

import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.navigation.PortletUtil;

/**
 * Handles Page events
 * 
 * @version $Revision $
 */
public class PageEventListener implements NodeEventListener, RelationEventListener {

   protected final static String TYPE_PAGE = "page";

   protected final static String TYPE_PORTLET = "portlet";

   private LuceusModule module;

   private static Logger log = Logging.getLoggerInstance(PageEventListener.class.getName());


   public PageEventListener(LuceusModule module) {
      this.module = module;
      MMBase.getMMBase().addNodeRelatedEventsListener(TYPE_PAGE, this);
      log.info("registered listener for: " + TYPE_PAGE);
   }


   public void notify(NodeEvent event) {
      log.debug("PageEventListener NodeEvent: " + event.getNodeNumber());
      switch (event.getType()) {
         case Event.TYPE_CHANGE:
         case Event.TYPE_NEW:
            module.updatePageIndex(event.getNodeNumber());
            break;
         case Event.TYPE_DELETE:
            module.deletePageIndex(event.getNodeNumber());
            break;
      }
   }


   public void notify(RelationEvent event) {
      String sourceType = event.getRelationSourceType();
      String destinationType = event.getRelationDestinationType();

      log.debug("PageEventListener RelationEvent source: " + event.getRelationSourceNumber() + " (" + sourceType
            + ") to " + event.getRelationDestinationNumber() + " (" + destinationType + ")");

      // update a page with a changed relation to a portlet
      if (PagesUtil.isPageType(sourceType) && PortletUtil.isPortletType(destinationType)) {
         switch (event.getType()) {
            case Event.TYPE_DELETE:
            case Event.TYPE_NEW:
            case Event.TYPE_CHANGE:
               int page = event.getRelationSourceNumber();
               module.updatePageIndex(page);
               break;
         }
      }

   }

}
