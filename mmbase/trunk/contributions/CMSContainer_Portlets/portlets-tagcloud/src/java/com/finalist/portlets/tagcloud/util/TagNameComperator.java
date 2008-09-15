package com.finalist.portlets.tagcloud.util;

import java.util.Comparator;

import com.finalist.portlets.tagcloud.Tag;

public class TagNameComperator implements Comparator {

	public int compare(Object arg0, Object arg1) {
		
		if(!(arg0 instanceof Tag) && !(arg1 instanceof Tag)) {
			throw new IllegalArgumentException("This comperator only works with tags");
		}
		else {
			return ((Tag)arg0).getName().compareTo(((Tag)arg1).getName());
		}
	}

}
