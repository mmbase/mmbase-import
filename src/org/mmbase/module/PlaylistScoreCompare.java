/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module;

import java.util.*;
import org.mmbase.util.*;

public class PlaylistScoreCompare implements CompareInterface {
	String compareField;

	public PlaylistScoreCompare() {
	}

	public int compare(Object thisOne, Object other) {
		int object1;
		int object2;
		int result = 0;

		object1 = ((PlaylistItem)thisOne).score;
		object2 = ((PlaylistItem)other).score;

		return(object2-object1);
	}


} 
