/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import java.util.List;
import java.util.Comparator;

/**
 * A list of nodes
 *
 * @author Pierre van Rooden
 * @version $Id: BridgeList.java,v 1.2 2002-09-23 15:57:34 pierre Exp $
 * @since  MMBase-1.6
 */
public interface BridgeList extends List {

    /**
     * Sorts this list according to a default sort order. 
     */
    public void sort();
    
    /**
     * Sorts this list according to a specified sort order
     *
     * @param comparator the comparator defining the sort order
     */
    public void sort(Comparator comparator);
}
