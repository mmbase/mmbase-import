/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.database;

/**
 * This class defines the attributes names used by the default database storage manager classes.
 * Specific storage managers may ignore or add their own attributes.
 *
 * @author Pierre van Rooden
 * @since MMBase-1.7
 * @version $Id: Attributes.java,v 1.1 2003-07-31 09:53:36 pierre Exp $
 */
public final class Attributes {

    /**
     * Option: <code>database.supportsInheritance</code>.
     * When true, the database supports inheritance (you can extend tables). This option influences what fields 
     * MMBase will add to a newly created table.
     * Note that you should specify this attribute if you have set up the schemes 
     * {@link Schemes.CREATE_ROW_TYPE_SCHEME} and/or 
     * {@link Schemes.CREATE_TABLE_SCHEME} to create tables that support inheritance.
     * The default is <code>false</code>
     */
    public static final String SUPPORTS_INHERITANCE = "database.supportsInheritance";

    /**
     * Option: <code>database.storeBinaryAsFile</code>.
     * When true, binary data is stored on disk, rather than in the database.
     * If you set this option ou should also set the attribute {@link #BINARY_FILE_PATH}
     * The default is <code>false</code>
     */
    public static final String STORE_BINARY_AS_FILE = "database.storeBinaryAsFile";

    /**
     * Option: <code>database.supportsBlob</code>.
     * When true, the driver/database used supports the JDBC getBlob() method.
     * The default is <code>false</code>
     */
    public static final String SUPPORTS_BLOB = "database.supportsBlob";

}
