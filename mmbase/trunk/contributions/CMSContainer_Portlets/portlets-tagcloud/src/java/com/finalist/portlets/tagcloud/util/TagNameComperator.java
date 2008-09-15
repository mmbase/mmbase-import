package com.finalist.portlets.tagcloud.util;

import java.util.Comparator;

import com.finalist.portlets.tagcloud.Tag;

public class TagNameComperator implements Comparator {

	private String orderby;
	
	public TagNameComperator(String orderby) {
		this.orderby = orderby;
	}

	public int compare(Object arg0, Object arg1) {
		
		if(!(arg0 instanceof Tag) && !(arg1 instanceof Tag)) {
			throw new IllegalArgumentException("This comperator only works with tags");
		}
		else {
			if(orderby.equals("name")) {
				return ((Tag)arg0).getName().compareTo(((Tag)arg1).getName());
			}
			else {
				return ((Tag)arg0).getCount() - ((Tag)arg1).getCount();
			}
		}
	}

}
