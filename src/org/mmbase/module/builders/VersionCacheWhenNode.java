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
public class VersionCacheWhenNode extends Object {

    private Vector types = new Vector();
    private Vector nodes = new Vector();

    public void addType(String type) {
        types.addElement(type);
    }

    public void addNode(String node) {
        nodes.addElement(node);
    }

    public Vector getTypes() {
        return types;
    }

    public Vector getNodes() {
        return nodes;
    }
}
