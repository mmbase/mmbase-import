package com.finalist.cmsc.rssfeed.builder;

import com.finalist.cmsc.navigation.NavigationBuilder;
import com.finalist.cmsc.navigation.NavigationManager;
import com.finalist.cmsc.rssfeed.newnav.RssFeedNavigationItemManager;


/**
 * @author Freek Punt
 */
public class RssFeedBuilder extends NavigationBuilder {
    
	
	public RssFeedBuilder() {
		NavigationManager.registerNavigationManager(new RssFeedNavigationItemManager());
	}

}
