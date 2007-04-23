/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.repository.forms;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.repository.RepositoryUtil;

public class QuickSearchAction 
    extends com.finalist.cmsc.struts.QuickSearchAction {

    protected Node getChannelFromPath(Cloud cloud, String quicksearch) {
        return RepositoryUtil.getChannelFromPath(cloud, quicksearch);
    }

}
