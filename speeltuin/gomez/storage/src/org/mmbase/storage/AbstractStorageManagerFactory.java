/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage;

import java.io.InputStream;
import java.util.*;
import org.xml.sax.InputSource;

import org.mmbase.storage.util.StorageReader;
import org.mmbase.storage.util.Scheme;
import org.mmbase.module.core.MMBase;

/**
 * An abstract implementation of the StorageManagerFactory implements ways for setting and retrieving attributes.
 *
 * @author Pierre van Rooden
 * @since MMBase-1.7
 * @version $Id: AbstractStorageManagerFactory.java,v 1.9 2003-07-25 14:47:25 pierre Exp $
 */
public abstract class AbstractStorageManagerFactory implements StorageManagerFactory {

    /**
     * A reference to the MMBase module
     */
    protected MMBase mmbase;
    /**
     * The class used to instantiate storage managers.
     * The classname is retrieved from the storage configuration file
     */
    protected Class storageManagerClass;

    /**
     * The map with configuration data
     */
    protected Map attributes;
    
    /**
     * The list with type mappings
     */
    protected List typeMappings;

    // The map with disallowed fieldnames and (if given) alternates
    private Map disallowedFields;
    
    // The map with alternate fieldnames for disallowed fieldnames.
    // this is the reverse of the disallowedFields map, but only for disallowed fields with alternate values.
    private Map alternateFields;
    
    /**
     * Stores the MMBase reference, and initializes the attribute map.
     * Opens and reads the StorageReader for this factory.
     * @see load()
     */
    public final void init(MMBase mmbase) throws StorageError {
        this.mmbase = mmbase;
        attributes = Collections.synchronizedMap(new HashMap());
        disallowedFields = new HashMap();
        alternateFields = new HashMap();
        typeMappings = Collections.synchronizedList(new ArrayList());
        try {
            load();
        } catch (StorageException se) {
            // pass exceptions as a StorageError to signal a serious (unrecoverable) error condition
            throw new StorageError(se);
        }
    }

    /**
     * Opens and reads the storage configuration document.
     * Override this method to add additional configuration code before or after the configuration document is read.
     * @throws StorageException if the storage could not be accessed or necessary configuration data is missing or invalid
     */
    protected void load() throws StorageException {
        StorageReader reader = getDocumentReader();
        // get the storage manager class
        storageManagerClass = reader.getStorageManagerClass();
        // get attributes
        setAttributes(reader.getAttributes());
        // get disallowed fields
        setDisallowedFields(reader.getDisallowedFields());
        // get type mappings
        typeMappings.addAll(reader.getTypeMappings());
        Collections.sort(typeMappings);
    }

    /**
     * Obtains a StorageManager that grants access to the storage.
     * The instance represents a temporary connection to the storage -
     * do not store the result of this call as a static or long-term member of a class.
     * @return a StorageManager instance
     */
    public StorageManager getStorageManager() throws StorageException {
        try {
            StorageManager storageManager = (StorageManager)storageManagerClass.newInstance();
            storageManager.init(this);
            return storageManager;
        } catch(InstantiationException ie) {
            throw new StorageException(ie);
        } catch(IllegalAccessException iae) {
            throw new StorageException(iae);
        }
    }

    /**
     * Locates and opens the storage configuration document.
     * The configuration document to open can be set in mmbasereoot (using the storage property).
     * The property should point to a resource which is to be present in the MMBase classpath.
     * If not given or the resource cannot be found, this method throws an exception. 
     * Overriding factories may provide ways to auto-detect the location of a configuration file, or
     * dismiss with its use.
     * @throws StorageException if something went wrong while obtaining the document reader, or if no reader can be found
     * @return a StorageReader instance
     */
    public StorageReader getDocumentReader() throws StorageException {
        // determine storage resource.
        // use the parameter set in mmbaseroot if it is given
        String storagepath = mmbase.getInitParameter("storage");
        if (storagepath == null) {
            throw new StorageConfigurationException("No storage resource specified.");
        } else {
            InputStream resource = this.getClass().getResourceAsStream(storagepath);
            if (resource == null) {
                throw new StorageConfigurationException("Storage resource '"+storagepath+"' not found.");
            }
            InputSource in = new InputSource(resource);
            in.setSystemId("resource://" + storagepath);
            return new StorageReader(this,in);
        }
    }
    
    public Map getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public void setAttributes(Map attributes) {
        this.attributes.putAll(attributes);
    }

    public Object getAttribute(Object key) {
        return attributes.get(key);
    }

    public void setAttribute(Object key, Object value) {
        attributes.put(key,value);
    }

    public Scheme getScheme(Object key) {
        return (Scheme)getAttribute(key);
    }

    public void setScheme(Object key, Scheme value) {
        setAttribute(key,(Scheme)value);
    }
    
    public void setScheme(Object key, String value) {
        setAttribute(key,new Scheme(this,value));
    }
    
    public boolean hasOption(Object key) {
        Object o = getAttribute(key);
        return (o instanceof Boolean) && ((Boolean)o).booleanValue();
    }

    public void setOption(Object key, boolean value) {
        setAttribute(key,new Boolean(value));
    }

	public List getTypeMappings() {
        return Collections.unmodifiableList(typeMappings);
    }

	public Map getDisallowedFields() {
        return Collections.unmodifiableMap(disallowedFields);
    }

    /**
     * Sets the map of disallowed Fields.
     * Unlike setAttributes(), this actually replaces the existing disallowed fields map.
     * It also craetes a map of alternate fieldnames, used in {@link #unmapField()}
     * @throw StorageException if the fieldmap contains duplicate alternate names 
     */
	synchronized protected void setDisallowedFields(Map disallowedFields) throws StorageException {
        // note: these maps need not be synchronised, as they cannot be changed concurrently 
        Map alternateFields = new HashMap();
        for (Iterator i = disallowedFields.entrySet().iterator(); i.hasNext();) {
            Map.Entry mapEntry = (Map.Entry)i.next();
            Object reverseKey = mapEntry.getValue();
            Object reverseValue = mapEntry.getKey();
            if (reverseKey != null) {
                if (alternateFields.containsKey(mapEntry.getValue())) {
                    throw new StorageException("The disallowed fields map contains duplicate alternate names");
                } else {
                    alternateFields.put(reverseKey, reverseValue);
                }
            }
        }
        this.disallowedFields = new HashMap(disallowedFields);
        this.alternateFields = alternateFields;
    }

	public String mapField(String name) throws StorageException {
        if (disallowedFields.containsKey(name)) {
            String alternateName = (String)disallowedFields.get(name);
            if (alternateName == null) {
                throw new StorageException("The name of the field '"+name+"' is disallowed, and no alternate value is available.");
            }
            return alternateName;
        } else {
            return name;
        }
    }
    
	public String unmapField(String name) throws StorageException {
        String disallowedName = (String)alternateFields.get(name);
        if (disallowedName != null) {
            return disallowedName;
        } else {
            return name;
        }
    }
    
    abstract public double getVersion();
    
	abstract public boolean supportsTransactions();
    
}
