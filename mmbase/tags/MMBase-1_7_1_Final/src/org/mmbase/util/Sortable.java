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
 * @author Rico Jansen
 * @version $Id: Sortable.java,v 1.5 2003-03-10 11:51:11 pierre Exp $
 */
public interface Sortable
{
    /**
     * The compare function called by SortedVector to sort things
     * @see org.mmbase.util.SortedVector
     */
    public abstract int compare(Sortable other);
}
