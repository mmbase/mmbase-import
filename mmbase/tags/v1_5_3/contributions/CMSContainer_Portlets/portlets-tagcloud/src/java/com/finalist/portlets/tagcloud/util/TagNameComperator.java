package com.finalist.portlets.tagcloud.util;

import java.util.Comparator;

import com.finalist.portlets.tagcloud.Tag;

public class TagNameComperator implements Comparator {

	private String orderby;
	private String direction;
	
	public TagNameComperator(String orderby, String direction) {
		this.orderby = orderby;
		this.direction = direction;
	}

	public int compare(Object arg0, Object arg1) {
		
		if(!(arg0 instanceof Tag) && !(arg1 instanceof Tag)) {
			throw new IllegalArgumentException("This comperator only works with tags");
		}
		else {
			
			Tag t0 = (direction != null && direction.equalsIgnoreCase("down"))?(Tag)arg1:(Tag)arg0;
			Tag t1 = (direction != null && direction.equalsIgnoreCase("down"))?(Tag)arg0:(Tag)arg1;
			
			if(orderby == null || orderby.equals("name")) {
				return t0.getName().compareToIgnoreCase(t1.getName());
			}
			if(orderby.equals("description")) {
				return t0.getDescription().compareToIgnoreCase(t1.getDescription());
			}
			else {
				return t0.getCount() - t1.getCount();
			}
		}
	}

}
