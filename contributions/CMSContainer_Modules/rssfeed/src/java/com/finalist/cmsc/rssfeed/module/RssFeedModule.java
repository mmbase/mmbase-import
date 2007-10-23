package com.finalist.cmsc.rssfeed.module;

import org.mmbase.module.Module;

import com.finalist.cmsc.navigation.NavigationManager;
import com.finalist.cmsc.rssfeed.newnav.RssFeedNavigationItemManager;


/**
 * @author Freek Punt
 */
public class RssFeedModule extends Module {
    
	public void init() {
		NavigationManager.registerNavigationManager(new RssFeedNavigationItemManager());
	}

}
