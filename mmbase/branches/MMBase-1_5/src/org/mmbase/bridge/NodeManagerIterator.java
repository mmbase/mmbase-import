/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;
import java.util.ListIterator;

/**
 * A list of nodes
 *
 * @author Pierre van Rooden
 * @version $Id: NodeManagerIterator.java,v 1.3 2002-01-31 10:05:08 pierre Exp $
 */
public interface NodeManagerIterator extends ListIterator {

    /**
     * Returns the next element in the iterator as a NodeManager
     */
    public NodeManager nextNodeManager();

}
