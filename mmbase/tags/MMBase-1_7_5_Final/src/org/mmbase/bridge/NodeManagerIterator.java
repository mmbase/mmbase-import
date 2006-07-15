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
 * @version $Id: NodeManagerIterator.java,v 1.6 2003-03-04 13:44:41 nico Exp $
 */
public interface NodeManagerIterator extends NodeIterator {

    /**
     * Returns the next element in the iterator as a NodeManager
     */
    public NodeManager nextNodeManager();

    /**
     * Returns the previous element in the iterator as a NodeManager
     */
    public NodeManager previousNodeManager();

}
