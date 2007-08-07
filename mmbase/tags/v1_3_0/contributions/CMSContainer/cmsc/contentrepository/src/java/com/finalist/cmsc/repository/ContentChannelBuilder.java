/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.repository;

import com.finalist.cmsc.builders.ChannelBuilder;

public class ContentChannelBuilder extends ChannelBuilder {

    @Override
    protected String getNameFieldname() {
        return RepositoryUtil.TITLE_FIELD;
    }
    
    @Override
    protected String[] getFragmentFieldname() {
        return RepositoryUtil.fragmentFieldnames;
    }

    @Override
    protected String getRelationName() {
        return RepositoryUtil.CHILDREL;
    }

    @Override
    protected String[] getPathManagers() {
        return RepositoryUtil.treeManagers;
    }

}
