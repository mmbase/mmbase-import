/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

/**
 * Interface to sort objects.
 * @see org.mmbase.util.SortedVector
 *
 * @deprecated I think this is the same as java.util.Comparable
 * @author Rico Jansen
 * @version $Id: Sortable.java,v 1.1 2008-03-25 21:00:25 nklasens Exp $
 */
public interface Sortable {

    /**
     * The compare function called by SortedVector to sort things
     * @see org.mmbase.util.SortedVector
     */
    public abstract int compare(Sortable other);
}
