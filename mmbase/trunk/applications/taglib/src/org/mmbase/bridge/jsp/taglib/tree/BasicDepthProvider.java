/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.tree;


/**
 * @author Michiel Meeuwissen
 * @since MMBase-1.8.6
 * @version $Id$
 */
public class BasicDepthProvider implements DepthProvider {

    private final int depth;
    public BasicDepthProvider(int d) {
        depth = d;
    }
    public int getDepth() {
        return depth;
    }

    public String toString() {
        return "DEPTH:" + depth;
    }


}

