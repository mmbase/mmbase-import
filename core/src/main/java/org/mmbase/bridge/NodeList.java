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
 * @version $Id$
 */
public interface NodeList extends BridgeList<Node> {

    /**
     * In the propery of the list with this name you find back the original Query object
     * by which this NodeList was created (if it as created like that)
     * @since MMBase-1.7
     */
    public static final String QUERY_PROPERTY = "query";

    /**
     * The node-step property will be set on a cluster node list which is the result of a {@link
     * NodeQuery} (which can also result 'real' nodes). This happens when you can
     * {@link Cloud#getList(Query)} with a NodeQuery argument.
     * @since MMBase-1.8
     */
    public static final String NODESTEP_PROPERTY = "nodestep";

    /**
     * Returns the Node at the indicated postion in the list
     * @param index the position of the Node to retrieve
     * @return Node at the indicated postion
     */
    public Node getNode(int index);

    /**
     * Returns an type-specific iterator for this list.
     * @return Node iterator
     */
    public NodeIterator nodeIterator();

    /**
     * Returns a sublist of this list.
     * @param fromIndex the position in the current list where the sublist starts (inclusive)
     * @param toIndex the position in the current list where the sublist ends (exclusive)
     * @return sublist of this list
     */
    public NodeList subNodeList(int fromIndex, int toIndex);

}
