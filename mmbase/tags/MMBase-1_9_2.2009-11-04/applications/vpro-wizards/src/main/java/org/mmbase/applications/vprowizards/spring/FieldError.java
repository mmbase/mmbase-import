/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/ 
package org.mmbase.applications.vprowizards.spring;

import java.util.Locale;

/**
 * This is the kind of error that will be created when something went wrong with a field value, or setting a field on a node. This kind of error
 * should be displayed in the referrer page (in connection with a specific field input element). It is primerily intended for validation errors.
 * @author Ernst Bunders
 */
public class FieldError extends GlobalError {
	private static final long serialVersionUID = 1L;
	private String field;
	public static final String MODEL_MAPPING_KEY="fieldErrors";

	public FieldError(String field, String messageKey, Locale locale) {
		super(messageKey, locale);
		this.field = field;
	}
	
	public FieldError(String field, String messageKey, String[] properties, Locale locale) {
		super(messageKey, properties, locale);
		this.field = field;
	}

	public String getField() {
		return field;
	}

}