/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

/**
 * A list of nodes
 *
 * @author Pierre van Rooden
 * @version $Id: NodeList.java,v 1.7 2003-03-04 13:44:40 nico Exp $
 */
public interface NodeList extends BridgeList {

    /**
     * Returns the Node at the indicated postion in the list
     * @param index the position of the Node to retrieve
     */
    public Node getNode(int index);

    /**
     * Returns an type-specific iterator for this list.
     */
    public NodeIterator nodeIterator();

    /**
     * Returns a sublist of this list.
     * @param fromIndex the position in the current list where the sublist starts (inclusive)
     * @param toIndex the position in the current list where the sublist ends (exclusive)
     */
    public NodeList subNodeList(int fromIndex, int toIndex);
    
}
