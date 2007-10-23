package com.finalist.cmsc.rssfeed.newnav;

import java.util.Map;

import net.sf.mmapps.commons.beans.MMBaseNodeMapper;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.navigation.NavigationItemManager;
import com.finalist.cmsc.portalImpl.NavigationItemRenderer;
import com.finalist.cmsc.rssfeed.beans.om.RssFeed;
import com.finalist.cmsc.rssfeed.publish.RssFeedPublisher;
import com.finalist.cmsc.rssfeed.util.RssFeedUtil;
import com.finalist.cmsc.services.sitemanagement.SiteCache;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.cmsc.services.sitemanagement.tree.PageTree;

public class RssFeedNavigationItemManager implements NavigationItemManager {

    private static Logger log = Logging.getLoggerInstance(RssFeedNavigationItemManager.class.getName());
	
	private NavigationItemRenderer renderer = new RssFeedNavigationRenderer();
	
	public Object getChild(Node parentNode, int index) {
  	    NodeList feeds = RssFeedUtil.getOrderedChildren(parentNode); 
  	    if (index < feeds.size()) {
  		    return feeds.get(index);
  	    }
		return null;
	}

	public int getChildCount(Node parent) {
		return RssFeedUtil.getChildCount(parent);
	}

	public NavigationItem getNavigationItem(String path) {
		NavigationItem navigationItem = SiteManagement.getNavigationItemFromPath(path);
		if(navigationItem instanceof RssFeed) {
			return navigationItem;
		}
		return null;
	}

	public NavigationItem getNavigationItem(int number) {
		NavigationItem navigationItem = SiteManagement.getNavigationItem(number);
		if(navigationItem instanceof RssFeed) {
			return navigationItem;
		}
		return null;
	}

	public NavigationItemRenderer getRenderer() {
		return renderer;
	}

	public String getFragementFieldname() {
		return RssFeedUtil.FRAGMENT_FIELD;
	}

	public String getTreeManager() {
		return RssFeedUtil.RSSFEED;
	}

	public void loadNavigationItems(SiteCache cache, Cloud cloud) {
		cache.loadNavigationItems(cloud, getTreeManager());
	}

	public void updateCache(Map<String, PageTree> trees, Integer key, String newFragment) {
        for (PageTree tree : trees.values()) {
            if (tree.containsPageTreeNode(key)) {
                tree.replace(key, newFragment);
            }
        }
	}

	public NavigationItem loadNavigationItem(Integer key, Node node) {
        if (node == null || !RssFeedUtil.isRssFeedType(node)) {
            log.debug("Rss feed not found: " + key);
            return null;
        }
        
        RssFeed rssFeed = null;
        rssFeed = (RssFeed) MMBaseNodeMapper.copyNode(node, RssFeed.class);

        return rssFeed;
	}

	public Object getPublisher(Cloud cloud, String type) {
		if(type.equals(getTreeManager())) {
			return new RssFeedPublisher(cloud);
		}
		else {
			return null;
		}
	}
}
