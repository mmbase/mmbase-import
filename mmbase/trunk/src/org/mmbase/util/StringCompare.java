/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.util;

/**
 * Class to compare two strings, used by SortedVector.
 * @see org.mmbase.util.SortedVector
 * @see org.mmbase.util.CompareInterface
 *
 * @author Rico Jansen
 * @version 05-Mar-1997
 */
public class StringCompare implements CompareInterface {

	/** 
	 * The compare function called by SortedVector to sort things
	 * @see org.mmbase.util.SortedVector
	 * @see org.mmbase.util.CompareInterface
	 */
	public int compare(Object thisone,Object other) {
		return(((String)thisone).compareTo((String)other));
	}
}
