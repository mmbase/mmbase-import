package org.mmbase.util;

/**
 * Interface to sort objects.
 * @see org.mmbase.util.SortedVector
 *
 * @author Rico Jansen
 * @version 05-Mar-1997
 */
public interface Sortable
{
	/** 
	 * The compare function called by SortedVector to sort things
	 * @see org.mmbase..util.SortedVector
	 */
	public abstract int compare(Sortable other);
}
