/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.portalImpl.services.sitemanagement;

import java.io.Serializable;

import net.sf.mmapps.commons.beans.MMBaseNodeMapper;

import org.mmbase.bridge.Node;

import com.finalist.cmsc.beans.om.Stylesheet;
import com.finalist.cmsc.navigation.PagesUtil;

public class StyleSheetCacheEntryFactory extends MMBaseCacheEntryFactory {
    
    public StyleSheetCacheEntryFactory() {
        super(PagesUtil.STYLESHEET);
    }

    protected Serializable loadEntry(Serializable key) throws Exception {
        Node stylesheetNode = getNode(key);
        Stylesheet stylesheet = (Stylesheet) MMBaseNodeMapper.copyNode(stylesheetNode, Stylesheet.class);
        return stylesheet;
    }

}
