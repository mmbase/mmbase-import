/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.navigation;

import java.util.LinkedHashMap;

import com.finalist.cmsc.builders.TreeBuilder;

public abstract class NavigationBuilder extends TreeBuilder {

    @Override
    protected String getRelationName() {
        return NavigationUtil.NAVREL;
    }

    @Override
    protected LinkedHashMap<String,String> getPathManagers() {
        return NavigationUtil.getTreeManagers();
    }
    
    @Override
    protected void registerTreeManager() {
        String builderName = getTableName();
        NavigationUtil.registerTreeManager(builderName, getFragmentField(), isRoot());
    }

}
