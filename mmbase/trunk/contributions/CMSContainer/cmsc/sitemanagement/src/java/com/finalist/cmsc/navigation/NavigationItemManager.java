package com.finalist.cmsc.navigation;

import java.util.Map;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.services.sitemanagement.SiteCache;
import com.finalist.cmsc.services.sitemanagement.tree.PageTree;

public interface NavigationItemManager {

	public abstract NavigationItem getNavigationItem(String path);

	public abstract NavigationItem getNavigationItem(int number);
	
	public abstract int getChildCount(Node parent);

	public abstract Object getChild(Node parentNode, int i);

	public abstract NavigationTreeItemRenderer getRenderer();

	public abstract String getTreeManager();

	public abstract String getFragementFieldname();

	public abstract void loadNavigationItems(SiteCache cache, Cloud cloud);

	public abstract void updateCache(Map<String, PageTree> trees, Integer key, String newFragment);

	public abstract NavigationItem loadNavigationItem(Integer key, Node node);

	public Object getPublisher(Cloud cloud, String type);

}
