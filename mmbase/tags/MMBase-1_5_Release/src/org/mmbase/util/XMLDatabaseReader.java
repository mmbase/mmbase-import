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
     * getBlobDataDir
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
     * Retrieve a list of fieldnames that are disallowed for this database.
     * Eacht entry in the hashtable has as its key the disallowed name, and as
     * its value the name with which it needs to be replaced.
     * @return the hashtable with the disallowed fields
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
     *
     */
    public String getCreateScheme() {
        return getElementValue("database.scheme.create");
    }

    /**
     *
     */
    public String getPrimaryKeyScheme() {
        return getElementValue("database.scheme.primary-key");
    }

    /**
     *
     */
    public String getKeyScheme() {
        return getElementValue("database.scheme.key");
    }


    /**
     *
     */
    public String getNotNullScheme() {
        return getElementValue("database.scheme.not-null");
    }

    /**
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
