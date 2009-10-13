package org.mmbase.applications.mmbob;

public interface ExternalProfileInterface {
    
    /**
     * retreve a field value from some external profile
     * @param account
     * @param name
     * @return
     */
    public String getValue(String account, String name);

    /**
     * set the value of a field of some external profile 
     * @param account
     * @param name is a string with values seperated by '\n\r'. fields are 'dienstId', type , veldId.
     * @param value
     * @return true when the operation was a success.
     */
    public boolean setValue(String account, String name, String value);
}
