/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.tree;


/**
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 * @version $Id$
 */
public interface DepthProvider {

    public static final String KEY = "org.mmbase.taglib.depth";

    /**
     * Returns the 'size' of the current cluster node (the number of steps (minus the relation steps))
     */
    public int getDepth();


}

