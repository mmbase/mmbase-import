package nl.vpro.redactie;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Indien er tijdens het verwerken van de acties een fout optreedt worden
 * fielderror gegooit. Deze geven aan welke velden niet verwerkt konden worden.
 * 
 * @author Rob Vermeulen (VPRO)
 */
public class FieldError {
	private String message;
	private String field;
	
	public FieldError(String field, String message) {
		this.message = message;
		this.field = field;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}	
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}	
}