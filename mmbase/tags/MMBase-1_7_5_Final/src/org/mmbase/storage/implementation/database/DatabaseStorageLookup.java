/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.implementation.database;

import java.sql.*;

import org.w3c.dom.*;
import org.xml.sax.InputSource;

import org.mmbase.storage.*;

import org.mmbase.util.xml.DocumentReader;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Represents a xml document that can be used to determine the database configuration resource,
 * based on a database's metadata.
 *
 * @author Pierre van Rooden
 * @since MMBase-1.7
 * @version $Id: DatabaseStorageLookup.java,v 1.4 2004-02-05 08:23:58 pierre Exp $
 */
public class DatabaseStorageLookup extends DocumentReader {

    private static final Logger log = Logging.getLoggerInstance(DatabaseStorageLookup.class);

    private static String DATABASE_STORAGE_LOOKUP_RESOURCE_PATH = "/org/mmbase/storage/implementation/database/resources/lookup.xml";

    /** Public ID of the Storage DTD version 1.0 */
    public static final String PUBLIC_ID_DATABASE_STORAGE_LOOKUP_1_0 = "-//MMBase//DTD storage config 1.0//EN";
    /** DTD resource filename of the Database DTD version 1.0 */
    public static final String DTD_DATABASE_STORAGE_LOOKUP_1_0 = "storage_1_0.dtd";

    /** Public ID of the most recent Database DTD */
    public static final String PUBLIC_ID_DATABASE_STORAGE_LOOKUP = PUBLIC_ID_DATABASE_STORAGE_LOOKUP_1_0;
    /** DTD resource filename of the most Database DTD */
    public static final String DTD_DATABASE_STORAGE_LOOKUP = DTD_DATABASE_STORAGE_LOOKUP_1_0;

    /**
     * Register the Public Ids for DTDs used by StorageReader
     * This method is called by XMLEntityResolver.
     */
    public static void registerPublicIDs() {
        org.mmbase.util.XMLEntityResolver.registerPublicID(PUBLIC_ID_DATABASE_STORAGE_LOOKUP_1_0, DTD_DATABASE_STORAGE_LOOKUP_1_0, DatabaseStorageLookup.class);
    }

    /**
     * Constructor, accesses the storage lookup xml resource
     */
    protected DatabaseStorageLookup() {
        super(new InputSource(DatabaseStorageLookup.class.getResourceAsStream(DATABASE_STORAGE_LOOKUP_RESOURCE_PATH)),
              DocumentReader.validate(), DatabaseStorageLookup.class);
    }

    /**
     * Obtain an path to a database configuration resource
     * @param dmd the database meta data
     * @return The database configuration resource, or <code>null</code> if it cannot be determined
     */
    protected String getResourcePath(DatabaseMetaData dmd) throws SQLException, StorageConfigurationException {
        Element root = document.getDocumentElement();
        NodeList filterList = root.getElementsByTagName("filter");
        for (int i = 0; i < filterList.getLength(); i++) {
            Element filter = (Element)filterList.item(i);
            String resourcePath = filter.getAttribute("resource");
            if (match(filter, dmd)) {
                log.service("Auto detection selected '" + resourcePath + "' for the current database.");
                return resourcePath;
            }
        }
        // not found, return null
        return null;
    }

    /**
     * Tests if an given filterset applies
     * @param filterNode The element containing all filters
     * @param dmd the database meta data
     * @return <code>true</code> when true, otherwise <code>false</code>
     */
    private boolean match(Element filterNode, DatabaseMetaData dmd) throws SQLException, StorageConfigurationException {
        NodeList conditionList = filterNode.getElementsByTagName("*");
        boolean match = true;
        for (int i = 0; match && i < conditionList.getLength(); i++) {
            Element condition = (Element)conditionList.item(i);
            String conditionName = condition.getTagName();
            if (conditionName.equals("driver-class")) {
                match = startMatch(condition, dmd.getConnection().getClass().getName());
            } else if(conditionName.equals("driver-name")) {
                match = match(condition, dmd.getDriverName());
            } else if(conditionName.equals("driver-version")) {
                match = match(condition, dmd.getDriverVersion());
            } else if(conditionName.equals("database-product-name")) {
                match = match(condition, dmd.getDatabaseProductName());
            } else if(conditionName.equals("database-product-version")) {
                match = match(condition, dmd.getDatabaseProductVersion());
            } else if(conditionName.equals("driver-major-version")) {
                match = match(condition, dmd.getDriverMajorVersion());
            } else if(conditionName.equals("driver-minor-version")) {
                match = match(condition, dmd.getDriverMinorVersion());
            } else {
                throw new StorageConfigurationException("tag with name:'"+conditionName+"' unknown.");
            }
        }
        return match;
    }

    /**
     * Tests if an element value matches a value specified
     * @param node the Element of which the body value has to be checked
     * @param value the Value which has to be compared
     * @return <code>true</code> when true, otherwise <code>false</code>
     */
    private boolean match(Element node, String value) {
        return value.equals(getNodeTextValue(node));
    }

    /**
     * Tests if an string starts with the value of the node
     * @param node the Element of which the body value has to be checked
     * @param value the Value which has to be compared
     * @return <code>true</code> when true, otherwise <code>false</code>
     */
    private boolean startMatch(Element node, String value) {
        return value.startsWith(getNodeTextValue(node));
    }

    /**
     * Tests a condition from an attibute of the Element applies to the
     * value of the element with the given int value
     * @param node the Element of which the body value has to be checked
     * @param value the Value which has to be compared
     * @return <code>true</code> when true, otherwise <code>false</code>
     */
    private boolean match(Element node, int value) throws StorageConfigurationException {
        int foundValue = Integer.parseInt(getNodeTextValue(node));
        String condition = node.getAttribute("condition");
        if ((condition == null) || condition.equals("equals")) {
            return foundValue == value;
        } else if (condition.equals("from")) {
            return foundValue <=  value;
        } else if(condition.equals("until")) {
            return foundValue >  value;
        } else {
            throw new StorageConfigurationException("condition: '" + condition + "' unknown");
        }
    }
}
