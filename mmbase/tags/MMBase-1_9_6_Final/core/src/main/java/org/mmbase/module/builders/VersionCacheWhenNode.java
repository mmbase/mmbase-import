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
 * @version $Id$
 */
class VersionCacheWhenNode {

    private List<String> types = new Vector<String>();
    private List<String> nodes = new Vector<String>();

    public void addType(String type) {
        types.add(type);
    }

    public void addNode(String node) {
        nodes.add(node);
    }

    /**
     * @javadoc
     */
    public List<String> getTypes() {
        return types;
    }

    /**
     * @javadoc
     */
    public List<String> getNodes() {
        return nodes;
    }
}
