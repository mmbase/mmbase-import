/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.repository;

import com.finalist.cmsc.builders.ChannelBuilder;

public class ContentChannelBuilder extends ChannelBuilder {

    protected String getNameFieldname() {
        return RepositoryUtil.TITLE_FIELD;
    }
    
    protected String[] getFragmentFieldname() {
        return new String[] { RepositoryUtil.FRAGMENT_FIELD };
    }

    protected String getRelationName() {
        return RepositoryUtil.CHILDREL;
    }

    protected String[] getPathManagers() {
        return new String[] { RepositoryUtil.CONTENTCHANNEL };
    }

}
