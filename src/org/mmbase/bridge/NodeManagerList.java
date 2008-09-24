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
 * @version $Id: NodeManagerList.java,v 1.7 2004-10-09 09:39:31 nico Exp $
 */
public interface NodeManagerList extends NodeList {

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
