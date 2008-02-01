package com.finalist.cmsc.navigation;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.beans.om.NavigationItem;

public interface NavigationItemManager {

    String getTreeManager();
    
    boolean isRoot();

    Class<? extends NavigationItem> getItemClass();
    
    // PORTAL
    
	NavigationItemRenderer getRenderer();

	//SITEMANAGEMENT
	
    NavigationItem loadNavigationItem(Integer key, Node node);

	//MMBASE
	
    NavigationTreeItemRenderer getTreeRenderer();
	
    // PUBLISH SERVICE

	Object getPublisher(Cloud cloud, String type);

   void deleteNode(Node pageNode);

}
