/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;

import org.w3c.dom.*;

import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.module.database.support.dTypeInfo;
import org.mmbase.module.database.support.dTypeInfos;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @author Case Roole
 * @author Rico Jansen
 * @author Pierre van Rooden
 */
public class XMLDatabaseReader extends XMLBasicReader  {
    // logger
    private static Logger log = Logging.getLoggerInstance(XMLDatabaseReader.class.getName());

    /**
     * Constructor
     * @param path the filename
     */
    public XMLDatabaseReader(String path) {
        super(path);
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
     * defined in {@link FieldDefs}.The value are {@link dtTypeInfos} objects.
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
