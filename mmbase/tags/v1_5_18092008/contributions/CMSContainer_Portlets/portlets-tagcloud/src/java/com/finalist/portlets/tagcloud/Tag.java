package com.finalist.portlets.tagcloud;

public class Tag {
	
	private int number;
	private String name;
	private String description;
	private int count;
	
	public Tag(String name, String description, int count) {
		super();
		this.name = name;
		this.description = description;
		this.count = count;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public int getNumber() {
		return number;
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
	public void setCount(int count) {
		this.count = count;
	}
}
