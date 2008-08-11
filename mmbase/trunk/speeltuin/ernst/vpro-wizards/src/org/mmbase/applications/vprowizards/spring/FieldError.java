package org.mmbase.applications.vprowizards.spring;

import java.util.Locale;

/**
 * Indien er tijdens het verwerken van de acties een fout optreedt worden fielderror gegooit. Deze geven aan welke
 * velden niet verwerkt konden worden.
 * 
 * @author Rob Vermeulen (VPRO)
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