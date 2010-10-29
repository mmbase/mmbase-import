/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

/**
 * A list of node managers
 *
 * @author Pierre van Rooden
 * @version $Id$
 */
public interface NodeManagerList extends BridgeList<NodeManager> {

    /**
     * Returns the NodeManager at the indicated postion in the list
     * @param index the position of the NodeManager to retrieve
     * @return NodeManager at the indicated postion
     */
    public NodeManager getNodeManager(int index);

    /**
     * Returns an type-specific iterator for this list.
     * @return NodeManager iterator
     */
    public NodeManagerIterator nodeManagerIterator();

}
