/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import org.mmbase.module.core.MMBase;

/**
 * An abstract implementation of the StorageManagerFactory implements ways for setting and retrieving attributes.
 *
 * @author Pierre van Rooden
 * @since MMBase-1.7
 * @version $Id: AbstractStorageManagerFactory.java,v 1.1 2003-07-21 09:31:01 pierre Exp $
 */
public class AbstractStorageManagerFactory implements StorageManagerFactory {

    /**
     * A reference to the MMBase module
     */
    protected MMBase mmbase;
    // the map with configuration data
    private Map attributes; 

    /**
     * Stores the MMBase reference, and initializes the attribute map.  
     */
	public init(MMBase mmbase) {
        this.mmbase = mmbase;
        attributes = Collections.SynchronisedMap(new HashMap());
    }

	abstract public StorageManager getStorageManager();

    public Map getAttributes() {
        return Collections.UnmodifiableMap(attributes);
    }

	public void setAttributes(Map attributes) {
        this.attributes.setAll(attributes);
    }

	public Object getAttribute(Object key) {
        return attributes.get(key);
    }

	public void setAttribute(Object key, Object value) {
        attributes.set(key,value);
    }
    
	public boolean hasOption(Object key) {
        Object o = getAttribute(key);
        return (o instanceof Boolean) && o.booleanValue();
    }
	
	public void setOption(Object key, boolean value) {
        setAttribute(key,new Boolean(value));
    }
    
	abstract public int getVersion();

}
