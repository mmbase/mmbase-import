/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage;

import java.util.*;

import org.w3c.dom.*;

import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.module.database.support.dTypeInfo;
import org.mmbase.module.database.support.dTypeInfos;
import org.mmbase.util.XMLBasicReader;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.xml.sax.InputSource;

/**
 * @author Pierre van Rooden
 */
public class StorageReader extends XMLBasicReader  {
    // logger
    private static Logger log = Logging.getLoggerInstance(StorageReader.class);

    /** Public ID of the Storage DTD version 1.0 */
    public static final String PUBLIC_ID_STORAGE_1_0 = "-//MMBase//DTD storage config 1.0//EN";
    /** DTD resource filename of the Database DTD version 1.0 */
    public static final String DTD_STORAGE_1_0 = "storage_1_0.dtd";

    /** Public ID of the most recent Database DTD */
    public static final String PUBLIC_ID_STORAGE = PUBLIC_ID_STORAGE_1_2;
    /** DTD resource filename of the most Database DTD */
    public static final String DTD_STORAGE = DTD_STORAGE_1_2;

    /**
     * Register the Public Ids for DTDs used by XMLDatabaseReader
     * This method is called by XMLEntityResolver.
     */
    public static void registerPublicIDs() {
        // various builder dtd versions
        XMLEntityResolver.registerPublicID(PUBLIC_ID_STORAGE_1_0, DTD_STORAGE_1_0, StorageReader.class);
    }

    /**
     * Constructor
     * @param path the filename
     */
    public StorageReader(String path) {
        super(path, StorageReader.class);
    }

    /**
     * Constructor.
     *
     * @param Inputsource to the xml document.
     * @since MMBase-1.7
     */
    public StorageReader(InputSource source) {
        super(source, StorageReader.class);
    }

    /**
     * Attempt to load a StorageManager class, using the classname as given in the configuration.
     * The method verifies whether the instantiated class is of the correct version.
     *
     * @param StorageManager
     * @return 
     */
    public String getStorageManagerClass(StorageManagerFactory factory) throws StorageConfigurationException {
        if (factory != null) {
            // verify if the storagemanagerfactory is of the correct class, and
            // of the correct version
            Element el = getElement("storagemanagerfactory");
            if (el != null ) { 
                try {
                    // obtain and check class
                    String factoryClassName = el.getAttribute("classname");
                    Class factoryClass = Class.forName(factoryClassName);
                    if (! factory instanceof factoryClass) {
                        throw new StorageFactoryException("StorageManager Configuration requires factory class '"+factoryClassName+"'.");
                    }
                    // obtain and check version
                    String storageManagerFactoryVersion = el.getAttribute("version");
                    if (storageManagerFactoryVersion != null) {
                        int version = Integer.parseInt(storageManagerFactoryVersion);
                        if (version > factory.getVersion()) {
                            throw new StorageFactoryException("StorageManager Configuration requires factory version '"+version+" or higher.");
                        }
                    }
                } catch (ParseException pe) {
                    throw new StorageFactoryException(pe);   // version is not an integer
                } catch (ClassNotFoundException cnfe) {
                    throw new StorageFactoryException(cnfe); // factory class not found
                }
            }
        }
        Element el = getElement("storagemanagerfactory");
        String managerClassName = el.getAttribute("classname");
        if (el != null) {
            // intantiate storage manager and check version
            ...
            
        } else {
            throw new StorageFactoryException("No StorageManager class specified.");
        }
    }

    /**
     * Returns the name of the database.
     * @return the name as a string
     */
    public String getStorageManagerClassName() {
        Element el = getElement("storagemanagerfactory");
        return el.getAttribute("classname");
    }

    /**
     * Returns the name of the database.
     * @return the name as a string
     */
    public String getName() {
        return getElementValue("database.name");
    }
    
    /**
     * Returns the name of the database.
     * @return the name as a string
     */
    public String getName() {
        return getElementValue("database.name");
    }

    /**
     * Returns the mmbase database driver class for this database.
     * @return the name of the class
     */
    public String getMMBaseDatabaseDriver() {
        return getElementValue("database.mmbasedriver");
    }

    /**
     * Returns the sql handler class to be used, this is a class implementing the
     * {@link org.mmbase.storage.search.implementation.database.SqlHandler
     * SqlHandler} interface.
     *
     * @return The name of the class
     * @since MMBase-1.7
     */
    public String getSqlHandler() {
        return getElementValue("database.sqlhandler");
    }

    /**
     * Returns names of the chained handlers to use, these are classes extending
     * {@link org.mmbase.storage.search.implementation.database.ChainedSqlHandler
     * ChainedSqlHandler}.
     *
     * @return List of names of the classes.
     * @since MMBase-1.7
     */
    public List getChainedSqlHandlers() {
        List result = new ArrayList();
        Enumeration eElements
            = getChildElements("database", "chainedsqlhandler");
        while (eElements.hasMoreElements()) {
            Element el = (Element) eElements.nextElement();
            result.add(getElementValue(el));
        }
        return result;
    }

    /**
     * Retrieves the file path where binary objects are to be stored.
     * If this value is set, binary objects are stored as files, not in the databse.
     * @return the path, or the empty string if not specified
     */
    public String getBlobDataDir() {
        return getElementValue("database.blobdatadir");
    }

    /**
     * Get the max drop size for this database.
     * The max drop size determines whether a table can be dropped as part of
     * a table alteration command.
     * @return the max drop size in number of records
     */
    public int getMaxDropSize() {
        String s= getElementValue("database.maxdropsize");
        try {
            return Integer.parseInt(s);
        } catch(Exception e) {
            return 0;
        }
    }

    /**
     * Retrieve a map of fieldnames that are disallowed (i.e reserved words) for this database.
     * Each entry in the map has as its key the disallowed name, and as
     * its value the name with which it needs to be replaced.
     * @return the map with the disallowed fields
     */
    public Hashtable getDisallowedFields() {
        Hashtable results=new Hashtable();
        Element n2=getElementByPath("database.disallowed");
        if (n2!=null) {
            for(Enumeration ns=getChildElements(n2,"field"); ns.hasMoreElements();) {
                Node n3=(Node)ns.nextElement();
                NamedNodeMap nm=n3.getAttributes();
                if (nm!=null) {
                    Node n5=nm.getNamedItem("name");
                    String name=n5.getNodeValue();
                    Node n6=nm.getNamedItem("replacement");
                    String replacement=n6.getNodeValue();
                    results.put(name,replacement);
                }
            }
        }
        return results;
    }

    /**
     * Retrieves the create scheme
     * @return the create scheme, or the empty string if not available
     */
    public String getCreateScheme() {
        return getElementValue("database.scheme.create");
    }

    /**
     * Retrieves the create scheme for extending tables.
     * @since MMBase 1.6
     * @return the create extended scheme, or the empty string if not available
     */
    public String getCreateExtendedScheme() {
        return getElementValue("database.scheme.create-extended");
    }

    /**
     * Retrieves the primary key scheme.
     * @return the primary key scheme, or the empty string if not available
     */
    public String getPrimaryKeyScheme() {
        return getElementValue("database.scheme.primary-key");
    }

    /**
     * Retrieves the foreign key scheme.
     * @since MMBase 1.6
     * @return the foreign key scheme, or the empty string if not available
     */
    public String getForeignKeyScheme() {
        return getElementValue("database.scheme.foreign-key");
    }

    /**
     * Retrieves the key scheme.
     * @return the key scheme, or the empty string if not available
     */
    public String getKeyScheme() {
        return getElementValue("database.scheme.key");
    }

    /**
     * Retrieves the not null scheme.
     * @return the not null scheme, or the empty string if not available
     */
    public String getNotNullScheme() {
        return getElementValue("database.scheme.not-null");
    }

    /**
     * Obtains the type map.
     * The type map is used to convert MMBase types to database types (needed for creating tables).
     * The kyes in the map are Integer object whos evalues match the MMBase object types as
     * defined in {@link FieldDefs}.The value are {@link dTypeInfos} objects.
     * @return a Map of MMBase types and their database type.
     */
    public Hashtable getTypeMapping() {
        Hashtable results=new Hashtable();
        Element n2=getElementByPath("database.mapping");
        if (n2!=null) {
            for(Enumeration ns=getChildElements(n2,"type-mapping"); ns.hasMoreElements();) {
                Element n3=(Element)ns.nextElement();
                int mmbasetype=FieldDefs.getDBTypeId(getElementAttributeValue(n3,"mmbase-type"));
                String dbtype=getElementValue(n3);
                dTypeInfos dtis=(dTypeInfos)results.get(new Integer(mmbasetype));
                if (dtis==null) {
                    dtis=new dTypeInfos();
                    results.put(new Integer(mmbasetype),dtis);
                }
                dTypeInfo dti=new dTypeInfo();
                dti.mmbaseType=mmbasetype;
                dti.dbType=dbtype;
                // does it also have a min size ?
                String tmp=getElementAttributeValue(n3,"min-size");
                if (tmp.length()>0) {
                    try {
                        int size=Integer.parseInt(tmp);
                        dti.minSize=size;
                    } catch(Exception e) {}
                }
                // does it also have a min size ?
                tmp=getElementAttributeValue(n3,"max-size");
                if (tmp.length()>0) {
                    try {
                        int size=Integer.parseInt(tmp);
                        dti.maxSize=size;
                    } catch(Exception e) {}
                }
                dtis.maps.addElement(dti);
            }
        }
        return results;
    }

}
