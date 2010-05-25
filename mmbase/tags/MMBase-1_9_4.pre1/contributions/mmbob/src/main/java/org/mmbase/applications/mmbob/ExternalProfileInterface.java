package org.mmbase.applications.mmbob;

public interface ExternalProfileInterface {
	public String getValue(String account,String name);
	public boolean setValue(String account,String name,String value);
}
