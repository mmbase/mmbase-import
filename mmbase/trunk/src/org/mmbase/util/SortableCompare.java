package org.mmbase.util;

/**
 * Class to compare two strings, used by SortedVector.
 * This one is to sort objects supporting the Sortable interface
 * @see org.mmbase.util.Sortable
 * @see org.mmbase.util.SortedVector
 * @see org.mmbase.util.CompareInterface
 *
 * @author Rico Jansen
 * @version 05-Mar-1997
 */
public class SortableCompare implements CompareInterface {

	/** 
	 * The compare function called by SortedVector to sort things
	 * @see org.mmbase.util.SortedVector
	 * @see org.mmbase.util.CompareInterface
	 */
	public int compare(Object thisone,Object other) {
		return(((Sortable)thisone).compare((Sortable)other));
	}
}
