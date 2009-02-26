/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.repository;

import java.util.LinkedHashMap;

import com.finalist.cmsc.builders.TreeBuilder;

public class ContentChannelBuilder extends TreeBuilder {

    @Override
    protected String getNameFieldname() {
        return RepositoryUtil.TITLE_FIELD;
    }

    @Override
    protected String getRelationName() {
        return RepositoryUtil.CHILDREL;
    }

    @Override
    protected LinkedHashMap<String,String> getPathManagers() {
        return RepositoryUtil.getTreeManagers();
    }

    @Override
    protected String getFragmentField() {
        return RepositoryUtil.FRAGMENT_FIELD;
    }

    @Override
    protected boolean isRoot() {
        return RepositoryUtil.CONTENTCHANNEL.equals(getTableName());
    }

    @Override
    protected void registerTreeManager() {
        RepositoryUtil.registerTreeManager(getTableName(), getFragmentField(), isRoot());
    }

}
