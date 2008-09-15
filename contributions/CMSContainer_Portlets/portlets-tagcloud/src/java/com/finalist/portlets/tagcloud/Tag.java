package com.finalist.portlets.tagcloud;

public class Tag {
	
	private String name;
	private String description;
	private int count;
	public Tag(String name, String description, int count) {
		super();
		this.name = name;
		this.description = description;
		this.count = count;
	}
	public int getCount() {
		return count;
	}
	public String getDescription() {
		return description;
	}
	public String getName() {
		return name;
	}
}
