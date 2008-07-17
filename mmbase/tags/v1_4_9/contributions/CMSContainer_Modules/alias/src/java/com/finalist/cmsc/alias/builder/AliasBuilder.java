package com.finalist.cmsc.alias.builder;

import com.finalist.cmsc.navigation.NavigationBuilder;
import com.finalist.cmsc.navigation.NavigationManager;
import com.finalist.cmsc.alias.AliasNavigationItemManager;
import com.finalist.cmsc.alias.util.AliasUtil;

/**
 * @author Freek Punt
 */
public class AliasBuilder extends NavigationBuilder {
    
    @Override
    protected String getNameFieldname() {
        return AliasUtil.TITLE_FIELD;
    }
	
	public AliasBuilder() {
		NavigationManager.registerNavigationManager(new AliasNavigationItemManager());
	}

    @Override
    protected String getFragmentField() {
        return AliasUtil.FRAGMENT_FIELD;
    }

    @Override
    protected boolean isRoot() {
        return false;
    }

}
