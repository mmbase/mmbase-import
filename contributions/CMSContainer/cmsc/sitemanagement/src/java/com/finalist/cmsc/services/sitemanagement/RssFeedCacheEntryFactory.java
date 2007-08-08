/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.services.sitemanagement;

import java.io.Serializable;

import net.sf.mmapps.commons.beans.MMBaseNodeMapper;

import org.mmbase.bridge.*;
import org.mmbase.core.event.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.beans.om.*;
import com.finalist.cmsc.navigation.*;


public class RssFeedCacheEntryFactory extends MMBaseCacheEntryFactory {

    public RssFeedCacheEntryFactory() {
        super(RssFeedUtil.RSSFEED);
    }

    /** MMbase logging system */
    private static Logger log = Logging.getLoggerInstance(RssFeedCacheEntryFactory.class.getName());

    protected Serializable loadEntry(Serializable key) throws Exception {
        return loadRssFeed((Integer) key);
    }

    private RssFeed loadRssFeed(Integer key) {
        Node rssFeedNode = getNode(key);
        if (rssFeedNode == null || !RssFeedUtil.isRssFeedType(rssFeedNode)) {
            log.debug("Rss feed not found: " + key);
            return null;
        }
        
        RssFeed rssFeed = null;
        rssFeed = (RssFeed) MMBaseNodeMapper.copyNode(rssFeedNode, RssFeed.class);

        return rssFeed;
    }

}
