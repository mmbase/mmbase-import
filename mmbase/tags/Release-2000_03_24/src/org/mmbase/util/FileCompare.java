/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.util;
import java.io.File;

/**
 * Class to compare two Files on their modification time, used by SortedVector.
 * @see vpro.james.util.SortedVector
 * @see vpro.james.util.CompareInterface
 *
 * @author David V van Zeventer
 * @version 12 November 1998
 */
public class FileCompare implements CompareInterface {

	/** 
	 * The compare function called by SortedVector to sort things
	 * @see vpro.james.util.SortedVector
	 * @see vpro.james.util.CompareInterface
	 */
	public int compare(Object thisone,Object other) {
		return (int) (((File)thisone).lastModified()-((File)other).lastModified());
	}
}
