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
public class StorageReader extends DocumentReader  {
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
        super(path, DocumentReader.VALIDATE, StorageReader.class);
    }

    /**
     * Constructor.
     *
     * @param Inputsource to the xml document.
     * @since MMBase-1.7
     */
    public StorageReader(InputSource source) {
        super(source, DocumentReader.VALIDATE, StorageReader.class);
    }

    /**
     * Attempt to load a StorageManager class, using the classname as given in the configuration.
     * The method verifies whether the instantiated class is of the correct version.
     * @param, factory the factory used to load the manager, used to verify whether the factory class and version are compatible
     * @return the storage manager Class
     */
    public Class getStorageManagerClass(StorageManagerFactory factory) throws StorageConfigurationException {
        if (factory != null) {
            // verify if the storagemanagerfactory is of the correct class, and
            // of the correct version
            element root = document.getDocumentElement();
            NodeList nl = root.getElementsByTagName("storagemanagerfactory");
            if (nl.getLength()>0) {
                Element el = (Element)nl.item(0);
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
                        double version = Double.parseDouble(storageManagerFactoryVersion);
                        if (version > factory.getVersion()) {
                            throw new StorageFactoryException("StorageManager Configuration requires factory version '"+version+", found "+factory.getVersion()+".");
                        }
                    }
                } catch (ParseException pe) {
                    throw new StorageFactoryException(pe);   // version is not an integer
                } catch (ClassNotFoundException cnfe) {
                    throw new StorageFactoryException(cnfe); // factory class not found
                }
            }
        }
        nl = root.getElementsByTagName("storagemanager");
        if (nl.getLength()>0) {
            Element el = (Element)nl.item(0);
            String managerClassName = el.getAttribute("classname");
            // intantiate storage manager and check version
            try {
                Class managerClass = Class.forName(managerClassName);
                StorageManager manager = (StorageManager)managerClass.newInstance();
                // obtain and check version
                String storageManagerVersion = el.getAttribute("version");
                if (storageManagerVersion != null) {
                    double version = Double.parseDouble(storageManagerVersion);
                    if (version > manager.getVersion()) {
                        throw new StorageFactoryException("StorageManager Configuration requires storage manager version '"+version+", found "+manager.getVersion()+".");
                    }
                }
            } catch (ClassNotFoundException cnfe) {
                throw new StorageFactoryException(cnfe);
            } catch (IllegalAccessException iae) {
                throw new StorageFactoryException(iae);
            } catch (InstantiationException ie) {
                throw new StorageFactoryException(ie);
            }
        } else {
            throw new StorageFactoryException("No StorageManager class specified.");
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
