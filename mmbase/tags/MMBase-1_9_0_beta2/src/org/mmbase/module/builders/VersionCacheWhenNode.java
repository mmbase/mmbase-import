/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;

/**
 * @javadoc
 * @deprecated is this (cacheversionfile) used? seems obsolete now
 * @author Daniel Ockeloen
 * @version $Id: VersionCacheWhenNode.java,v 1.7 2008-06-12 11:25:10 michiel Exp $
 */
class VersionCacheWhenNode {

    private Vector<String> types = new Vector<String>();
    private Vector<String> nodes = new Vector<String>();

    public void addType(String type) {
        types.addElement(type);
    }

    public void addNode(String node) {
        nodes.addElement(node);
    }

    public Vector<String> getTypes() {
        return types;
    }

    public Vector<String> getNodes() {
        return nodes;
    }
}
