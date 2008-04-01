package com.finalist.newsletter.domain;

import java.util.ArrayList;
import java.util.List;

public class Newsletter {
	
	private List<Tag> tags = new ArrayList<Tag>();
	private String title;

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	

	
	
}
