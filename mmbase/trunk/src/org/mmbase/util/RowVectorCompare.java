/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;
import org.mmbase.util.*;

/**
 * RowVector compare can sort Vectors in Vectors (rows) based in given item 
 * number 
 *
 */
public class RowVectorCompare implements CompareInterface {
	int comparePos=1;

	public RowVectorCompare(int pos) {
		comparePos = pos;
	}

	public int compare(Object thisOne, Object other) {
		Object object1;
		Object object2;
		int result = 0;

		object1 = ((Vector)thisOne).elementAt(comparePos);
		object2 = ((Vector)other).elementAt(comparePos);

		if(object1 instanceof String)
			result = internalStringCompare(object1, object2);
		else if(object1 instanceof Integer)
			result = internalIntCompare(object1, object2);

		return (result);
	}

	int internalIntCompare(Object thisOne, Object other) {
		return(((Integer)thisOne).intValue()-((Integer)other).intValue());
	}

	int internalStringCompare(Object thisOne, Object other) {
		return(((String)thisOne).compareTo((String)other));
	}
} 
