/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;
import java.util.ListIterator;

/**
 * An iterator for Nodes, provider {@link #nextNode} and {@link #previousNode}. Note that since
 * MMBase-1.9/Java 1.5, this simply implements <code>ListIterator&lt;Node&gt;</code> and {@link
 * #next} and {@link #previous} don't require casting either.
 *
 * @author Pierre van Rooden
 * @version $Id$
 */
public interface NodeIterator extends ListIterator<Node> {

    /**
     * Returns the next element in the iterator as a Node
     * @return next Node
     */
    Node nextNode();

    /**
     * Returns the previous element in the iterator as a Node
     * @return previous Node
     * @since MMBase-1.7
     */
    Node previousNode();

}
