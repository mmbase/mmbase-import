package com.finalist.cmsc.fileupload.mmbase;

/**
 * Holds some 'constants' that are used to identify the names that mmbase uses
 * for fields in the builders.
 *
 * @author Auke van Leeuwen
 */
public enum Field {
	TITLE("title"),
	DESCRIPTION("description"),
	FILENAME("filename");

	private String name;

	private Field(final String fieldName) {
		this.name = fieldName;
	}

	/**
	 * Returns the name of the field that is used in the MMBase builder.
	 *
	 * @return the name of the field.
	 */
	public String getName() {
		return name;
	}
}
