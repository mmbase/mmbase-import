/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import org.mmbase.bridge.*;

import java.util.*;

/**
 * A specialized iterator for 'TreeLists'
 *
 * @author  Michiel Meeuwissen
 * @version $Id: TreeIterator.java,v 1.1 2003-12-18 12:17:12 michiel Exp $
 * @since   MMBase-1.7
 * @see org.mmbase.bridge.util.TreeList
 */

public interface TreeIterator extends NodeIterator {
    /**
     * Depth of the last node fetched with next() or nextNode()
     */
    int currentDepth();

}
