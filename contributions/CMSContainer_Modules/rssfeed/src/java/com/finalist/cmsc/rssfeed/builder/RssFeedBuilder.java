package com.finalist.cmsc.rssfeed.builder;

import com.finalist.cmsc.navigation.*;
import com.finalist.cmsc.rssfeed.newnav.RssFeedNavigationItemManager;
import com.finalist.cmsc.rssfeed.util.RssFeedUtil;

/**
 * @author Freek Punt
 */
public class RssFeedBuilder extends NavigationBuilder {
    
    @Override
    protected String getNameFieldname() {
        return RssFeedUtil.TITLE_FIELD;
    }
	
	public RssFeedBuilder() {
		NavigationManager.registerNavigationManager(new RssFeedNavigationItemManager());
	}

    @Override
    protected String getFragmentField() {
        return RssFeedUtil.FRAGMENT_FIELD;
    }

    @Override
    protected boolean isRoot() {
        return false;
    }

}
