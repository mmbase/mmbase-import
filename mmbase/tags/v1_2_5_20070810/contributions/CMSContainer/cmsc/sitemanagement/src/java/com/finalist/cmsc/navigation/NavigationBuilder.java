/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.navigation;

import com.finalist.cmsc.builders.ChannelBuilder;

public class NavigationBuilder extends ChannelBuilder {

    protected String getNameFieldname() {
        return PagesUtil.TITLE_FIELD;
    }

    protected String[] getFragmentFieldname() {
        return NavigationUtil.fragmentFieldnames;
    }
    
    protected String getRelationName() {
        return NavigationUtil.NAVREL;
    }

    protected String[] getPathManagers() {
        return NavigationUtil.treeManagers;
    }

}
