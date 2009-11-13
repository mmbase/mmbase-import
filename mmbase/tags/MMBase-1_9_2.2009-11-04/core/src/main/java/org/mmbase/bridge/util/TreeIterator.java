/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import org.mmbase.bridge.*;


/**
 * A specialized iterator for 'TreeLists'
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.7
 * @see org.mmbase.bridge.util.TreeList
 */

public interface TreeIterator extends NodeIterator {
    /**
     * Depth of the last node fetched with next() or nextNode()
     * @return Depth of the last node fetched
     */
    int currentDepth();


    /**
     * Returns the 'parent' node of the most recently returned Node. Or <code>null</code> if there
     * is no such node.
     * @since MMBase-1.8.6
     */
    Node getParent();

    /**
     * Returns all nodes with the same parent as the most recently return Node (include that node
     * itself).
     * @since MMBase-1.8.6
     */
    NodeList getSiblings();

}
