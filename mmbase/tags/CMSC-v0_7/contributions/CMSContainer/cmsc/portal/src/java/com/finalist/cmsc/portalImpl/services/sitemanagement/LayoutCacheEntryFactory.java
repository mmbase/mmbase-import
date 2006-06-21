/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.portalImpl.services.sitemanagement;

import java.io.Serializable;

import net.sf.mmapps.commons.beans.MMBaseNodeMapper;

import org.mmbase.bridge.*;

import com.finalist.cmsc.beans.om.Layout;
import com.finalist.cmsc.navigation.PagesUtil;

public class LayoutCacheEntryFactory extends MMBaseCacheEntryFactory {

    public LayoutCacheEntryFactory() {
        super(PagesUtil.LAYOUT);
    }

    protected Serializable loadEntry(Serializable key) throws Exception {
        Node layoutNode = getNode(key);
        Layout layout = (Layout) MMBaseNodeMapper.copyNode(layoutNode, Layout.class);
        
        RelationList rellist = PagesUtil.getAllowedNamedRelations(layoutNode);
        for (RelationIterator iter = rellist.relationIterator(); iter.hasNext();) {
            Relation relation = iter.nextRelation();
            Node definitionNode = relation.getDestination();
            String name = relation.getStringValue(PagesUtil.NAME_FIELD);
            layout.addDefinition(name, definitionNode.getNumber());
        }
        
        return layout;
    }

}
