/**
 * Security from within MMBase
 * Copyright 2002 (Not yet OpenSouce, needs some tweaking)
 * @author Eduard Witteveen
 */
package org.mmbase.security.implementation.cloud;

import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.core.MMObjectBuilder;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class UserBuilder extends MMObjectBuilder {
    private static Logger log=Logging.getLoggerInstance(UserBuilder.class.getName());    
    private org.mmbase.util.Encode encoder = null;    
    
    public boolean init() {
        String encoding = (String) getInitParameters().get("encoding");
        if(encoding==null) {
            log.warn("no property 'encoding' defined in '"+getTableName()+".xml' using default encoding");
            encoder = new org.mmbase.util.Encode("BASE64");
        }
        else {
            encoder = new org.mmbase.util.Encode(encoding);
        }
        log.info("Using " + encoder.getEncoding() + " as our encoding for password");
        return super.init();
    }

    public boolean setValue(MMObjectNode node,String fieldname, Object originalValue) {
        // the field with the name bar may not be changed.....
        if(fieldname.equals("username")) {
	    Object newValue = node.values.get(fieldname);
	    if(originalValue!=null && !originalValue.equals(newValue)) {
		// restore the original value...
                node.values.put(fieldname,originalValue);
                return false;
	    }
     	}
        else if(fieldname.equals("password")) {
            Object newValue = node.values.get(fieldname);
	    if(originalValue!=null && !originalValue.equals(newValue)) {
		// encode the new value...
                node.values.put(fieldname,encode((String)newValue));
            }
        }		
        return true;
    }   

    public void setDefaults(MMObjectNode node) {
	// set it to '""' so that we know the difference
	node.setValue("password","");
    }
    
    public boolean exists(String username, String password) {
        java.util.Enumeration e = searchWithWhere("username LIKE('"+username+"')");
	while(e.hasMoreElements()) {       
            MMObjectNode node = (MMObjectNode) e.nextElement();
            if(encode(password).equals(node.getStringValue("password"))) {
                // found it !
                return true;
            }    
        }
        return false;
    }
	
    public String encode(String value) {
	return encoder.encode(value);
    }                
}
