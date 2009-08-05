/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.beans.om;

import com.finalist.cmsc.beans.NodeBean;

/**
 * @author Nico Klasens
 */
@SuppressWarnings("serial")
public class Stylesheet extends NodeBean {

	private String title;
	private String description;
	private String resource;
	private String media;
	private boolean overwriteable;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getMedia() {
		return media;
	}

	public void setMedia(String media) {
		this.media = media;
	}

	public boolean isOverwriteable() {
		return overwriteable;
	}

	public void setOverwriteable(boolean overwriteable) {
		this.overwriteable = overwriteable;
	}
}
