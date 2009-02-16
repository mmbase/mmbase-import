/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.sitemanagement;

import java.io.Serializable;
import java.util.List;

import org.mmbase.bridge.Node;
import org.mmbase.core.event.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.navigation.*;

public class NavigationCacheEntryFactory extends MMBaseCacheEntryFactory {

   /** MMbase logging system */
   private static final Logger log = Logging.getLoggerInstance(NavigationCacheEntryFactory.class);

    public NavigationCacheEntryFactory() {
        super(PagesUtil.PAGE);
        for (NavigationItemManager manager : NavigationManager.getNavigationManagers()) {
            if (!PagesUtil.isPageType(manager.getTreeManager())) {
                registerListener(manager.getTreeManager());
            }
            List<String> relatedTypes = manager.getRelatedTypes();
            if (relatedTypes != null) {
               for (String relatedType : relatedTypes) {
                  if (NavigationManager.getNavigationManager(relatedType) == null ) {
                     registerListener(relatedType);
                  }
               }
            }
        }
    }


    @Override
    protected Serializable loadEntry(Serializable key) throws Exception {
        Node node = getNode(key);
        if (node == null) {
           log.debug("NavigationItem not found: " + key);
           return null;
       }

        NavigationItemManager manager = NavigationManager.getNavigationManager(node);
        if (manager != null) {
            return manager.loadNavigationItem(node);
        }
        return null;
    }

    @Override
    protected boolean isRelationEvent(RelationEvent event, String nodeType) {
        for (NavigationItemManager manager : NavigationManager.getNavigationManagers()) {
            if (super.isRelationEvent(event, manager.getTreeManager())) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected boolean isNodeEvent(NodeEvent event, String nodeType) {
        return NavigationManager.getNavigationManager(event.getBuilderName()) != null;
    }

    @Override
    public void notify(NodeEvent event) {
      super.notify(event);
      String eventNodeType = event.getBuilderName();

      if (event.getType() == Event.TYPE_CHANGE) {
         for (NavigationItemManager manager : NavigationManager.getNavigationManagers()) {
            List<String> relatedTypes = manager.getRelatedTypes();
            if (relatedTypes != null && relatedTypes.contains(eventNodeType)) {
               Node node = getCloud().getNode(event.getNodeNumber());
               Node navItem = manager.findItemForRelatedNode(node);
               if (navItem != null) {
                  refreshEntry(navItem.getNumber());
               }
            }
         }
      }
    }

    @Override
    public void notify(RelationEvent event) {
        if (isRelationEvent(event)) {
            Integer key = getKey(event);
            if (key != null) {
                switch (event.getType()) {
                    case Event.TYPE_CHANGE:
                        refreshEntry(key);
                        break;
                    case Event.TYPE_DELETE:
                        // don't remove page entry when layout removed from page.
                        if (PagesUtil.isPageType(event.getRelationSourceType()) && !event.getRelationDestinationType().equals(PagesUtil.LAYOUT)) {
                            deleteEntry(key);
                        }
                        break;
                    case Event.TYPE_NEW:
                        refreshEntry(key);
                        break;
                    case NodeEvent.TYPE_RELATION_CHANGE:
                        deleteEntry(key);
                        break;
                    default:
                        break;
                }
            }
        }
    }

}
