package com.finalist.cmsc.fileupload.mmbase;

/**
 * Holds some 'constants' that are used in the builders that belong to this
 * file upload module.
 *
 * @author Auke van Leeuwen
 */
public enum Builder {
	FILE("file");

	private String name;

	private Builder(final String name) {
		this.name = name;
	}

	/**
	 * Returns the MMBase name of this builder.
	 *
	 * @return the name of the MMBase builder
	 */
	public String getName() {
		return name;
	}
}
