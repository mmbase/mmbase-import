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

import org.mmbase.util.ResourceLoader;
import org.mmbase.util.xml.DocumentReader;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Represents a xml document that can be used to determine the database configuration resource,
 * based on a database's metadata.
 *
 * @author Pierre van Rooden
 * @since MMBase-1.7
 * @version $Id$
 */
public class DatabaseStorageLookup extends DocumentReader {

    private static final Logger log = Logging.getLoggerInstance(DatabaseStorageLookup.class);

    private static String DATABASE_STORAGE_LOOKUP_RESOURCE_PATH_FALLBACK = "/org/mmbase/storage/implementation/database/resources/lookup.xml";
    private static String DATABASE_STORAGE_LOOKUP_RESOURCE_PATH     = "storage/databases/lookup.xml";

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
     * This method is called by EntityResolver.
     */
    static  {
        org.mmbase.util.xml.EntityResolver.registerPublicID(PUBLIC_ID_DATABASE_STORAGE_LOOKUP_1_0, DTD_DATABASE_STORAGE_LOOKUP_1_0, DatabaseStorageLookup.class);
    }

    /**
     * @since MMBase-1.8
     */
    private static InputSource getInputSource() {
        InputSource is = null;
        try {
            is = ResourceLoader.getConfigurationRoot().getInputSource(DATABASE_STORAGE_LOOKUP_RESOURCE_PATH);
        } catch (java.io.IOException ioe) {
            log.service(ioe);
        }
        if (is == null) { // 1.7 compatibility
            is = new InputSource(DatabaseStorageLookup.class.getResourceAsStream(DATABASE_STORAGE_LOOKUP_RESOURCE_PATH_FALLBACK));
            is.setSystemId(DATABASE_STORAGE_LOOKUP_RESOURCE_PATH_FALLBACK);
            return is;
        } else {
            return is;
        }
    }

    /**
     * Constructor, accesses the storage lookup xml resource
     */
    DatabaseStorageLookup() {
        super(getInputSource(), DocumentReader.validate(), DatabaseStorageLookup.class);
    }

    /**
     * Obtain an path to a database configuration resource
     * @param dmd the database meta data
     * @return The database configuration resource, or <code>null</code> if it cannot be determined
     */
    String getResourcePath(DatabaseMetaData dmd) throws SQLException, StorageConfigurationException {
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
     * Returns an given connection URL for a given Driver CLass. Or <code>null</code> if no such
     * thing was defined in lookup.xml. In that case the configured URL in MMBase can be used.
     * 
     * @since MMBase-1.8
     */
    String getMetaURL(Class clazz) {
        Element root = document.getDocumentElement();
        NodeList urlList = root.getElementsByTagName("url");
        for (int i = 0; i < urlList.getLength(); i++) {
            Element url = (Element) urlList.item(i);
            String driverClass = url.getAttribute("driver-class");
            if (clazz.getName().startsWith(driverClass)) {
                return getNodeTextValue(url);
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
