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
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.FieldDefs;

import org.mmbase.storage.util.*;

/**
 * An abstract implementation of the StorageManagerFactory implements ways for setting and retrieving attributes.
 *
 * @author Pierre van Rooden
 * @since MMBase-1.7
 * @version $Id: AbstractStorageManagerFactory.java,v 1.12 2003-07-31 09:53:36 pierre Exp $
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
    
    /**
     * The ChangeManager object, used to register/broadcast changes to a node or set of nodes.
     */
    protected ChangeManager changeManager;    

    // The map with disallowed fieldnames and (if given) alternates
    private Map disallowedFields;
    
    /**
     * Stores the MMBase reference, and initializes the attribute map.
     * Opens and reads the StorageReader for this factory.
     * @see load()
     */
    public final void init(MMBase mmbase) throws StorageError {
        this.mmbase = mmbase;
        attributes = Collections.synchronizedMap(new HashMap());
        disallowedFields = new HashMap();
        typeMappings = Collections.synchronizedList(new ArrayList());
        changeManager = new ChangeManager(mmbase);
        try {
            load();
        } catch (StorageException se) {
            // pass exceptions as a StorageError to signal a serious (unrecoverable) error condition
            throw new StorageError(se);
        }
    }

    public MMBase getMMBase() {
        return mmbase;
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

    public Scheme getScheme(Object key, String defaultPattern) {
        Scheme scheme = getScheme(key);
        if (scheme == null) {
            scheme = new Scheme(this,defaultPattern);
            setAttribute(key,scheme);
        }
        return scheme;
    }

    public void setScheme(Object key, String pattern) {
        setAttribute(key,new Scheme(this,pattern));
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
     */
	protected void setDisallowedFields(Map disallowedFields) {
        this.disallowedFields = new HashMap(disallowedFields);
    }

    /**
     * Obtains the identifier for the basic storage element.
     * @return the storage-specific identifier
     * @throws StorageException if the object cannot be given a valid identifier
     */
    public Object getStorageIdentifier() throws StorageException {
        return getStorageIdentifier(mmbase);
    }
    
    /**
     * Obtains a identifier for an MMBase object.
     * The default implementation returns the following type of identifiers:
     * <ul>
     *  <li>For MMBase: the String '[basename]_object'</li>
     *  <li>For MMObjectBuilder: the String '[basename]_[builder name]'</li>
     *  <li>For MMObjectNode: the object number as a Integer</li>
     *  <li>For FieldDefs: the field name, or the replacement fieldfname (from the disallowedfields map)</li>
     * </ul>
     * Note that 'basename' is a property from the mmbase module, set in mmabseroot.xml.<br />
     * You can override this method if you intend to use different ids.
     *
     * @see Storable
     * @param mmobject the MMBase objecty
     * @return the storage-specific identifier
     * @throws StorageException if the object cannot be given a valid identifier
     */
    public Object getStorageIdentifier(Object mmobject) throws StorageException {
        if (mmobject == mmbase) {
            return mmbase.getBaseName()+"_object";
        } else if (mmobject instanceof MMObjectBuilder) {
            return mmbase.getBaseName()+"_"+((MMObjectBuilder)mmobject).getTableName();
        } else if (mmobject instanceof MMObjectNode) {
            return ((MMObjectNode)mmobject).getIntegerValue("number");
        } else if (mmobject instanceof FieldDefs) {
            String id = ((FieldDefs)mmobject).getDBName();
            if (disallowedFields.containsKey(id)) {
                id = (String)disallowedFields.get(id);
                if (id == null) {
                    throw new StorageException("The name of the field '"+((FieldDefs)mmobject).getDBName()+"' is disallowed, and no alternate value is available.");
                }
            }
            return id;
        } else {
            throw new StorageException("Cannot obtain identifier for param of type '"+mmobject.getClass().getName()+".");
        }
    }
 
    public ChangeManager getChangeManager() {
        return changeManager;
    }
  
    abstract public double getVersion();
    
	abstract public boolean supportsTransactions();
    
}
