package nl.vpro.redactie.actions;

import java.util.*;

import org.apache.commons.collections15.FactoryUtils;
import org.apache.commons.collections15.MapUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.web.multipart.MultipartFile;

public class Action{
	private Map<String,String> fields = new HashMap<String,String>();
	private Map<String,DateTime> dateFields = new HashMap<String,DateTime>();
	private Set<String> number = new HashSet<String>();
	private Set<String> id = new HashSet<String>();
	private Set<String> type = new HashSet<String>();
	private MultipartFile file =  null;

	@SuppressWarnings("unchecked")
	public Action() {
		dateFields = MapUtils.lazyMap(dateFields, FactoryUtils.instantiateFactory(DateTime.class));
	}

	public Map<String, String> getFields() {
		return fields;
	}

	public void setFields(Map<String, String> fields) {
		this.fields = fields;
	}

	public Set<String> getNumber() {
		return number;
	}

	public void setNumber(Set<String> numbers) {
		this.number = numbers;
	}

	public Set<String> getId() {
		return id;
	}

	public void setId(Set<String> id) {
		this.id = id;
	}

	public Set<String> getType() {
		return type;
	}

	public void setType(Set<String> type) {
		this.type = type;
	}

	public Map<String, DateTime> getDateFields() {
		return dateFields;
	}

	public void setDateFields(Map<String, DateTime> dateFields) {
		this.dateFields = dateFields;
	}

	/**
	 * @return null if there was no file uploaded for this action
	 */
	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
