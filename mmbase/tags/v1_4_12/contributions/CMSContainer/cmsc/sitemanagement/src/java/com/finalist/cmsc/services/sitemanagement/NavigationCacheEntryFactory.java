/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.sitemanagement;

import java.io.Serializable;

import org.mmbase.bridge.*;
import org.mmbase.core.event.*;

import com.finalist.cmsc.navigation.*;

public class NavigationCacheEntryFactory extends MMBaseCacheEntryFactory {

    public NavigationCacheEntryFactory() {
        super(PagesUtil.PAGE);
        for (NavigationItemManager manager : NavigationManager.getNavigationManagers()) {
            if (!PagesUtil.isPageType(manager.getTreeManager())) {
                registerListener(manager.getTreeManager());
            }
        }
    }


    @Override
    protected Serializable loadEntry(Serializable key) throws Exception {
        Node node = getNode(key);
        NavigationItemManager manager = NavigationManager.getNavigationManager(node);
        if (manager != null) {
            return manager.loadNavigationItem((Integer) key, node);
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
