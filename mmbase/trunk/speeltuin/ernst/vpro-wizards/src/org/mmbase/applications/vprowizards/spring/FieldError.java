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