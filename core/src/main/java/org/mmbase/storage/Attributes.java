/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage;

/**
 * This class defines the attributes names used by the standard storage manager classes.
 * Specific storage managers may ignore or add their own attributes.
 *
 * @author Pierre van Rooden
 * @since MMBase-1.7
 * @version $Id$
 */
public final class Attributes {

    /**
     * Option: <code>disallowed-fields-case-sensitive</code>.
     * if <code>true</code>, matching MMBase fieldnames with the disallowed fieldnames list is case-sensitive.
     * By default, this option is <code>false</code>.
     * Note that you can specify this attribute seperately, but the "case-sensitive" attribute
     * of the "disallowedfields" tag overrides this attribute.
     */
    public static final String DISALLOWED_FIELD_CASE_SENSITIVE = "disallowed-fields-case-sensitive";

    /**
     * Option: <code>enforce.disallowed.fields</code>.
     * if <code>true</code>, the storage layer alwyas fails when encountering fieldnames that are reserved sql keywords,
     * and for which no alternate name is available.
     * If <code>false</code>, the layer will ignore the restriction and attempt to use the reserved word (leaving any
     * errors to the underlying implementation). <br />
     * By default, this option is <code>false</code>.
     * Note that you can specify this attribute seperately, but the "enforce" attribute
     * of the "disallowedfields" tag overrides this attribute.
     */
    public static final String ENFORCE_DISALLOWED_FIELDS = "enforce-disallowed-fields";

    /**
     * Attribute: <code>storage-identifier-case</code>.
     * if set, the storage identifiers for builders and fieldnames are converted to lowercase (if the value is 'lower') or
     * uppercase (if the value is 'upper') before they are passed to the storage manager.
     * If you specify another value this attribute is ignored.
     * This ensures that field or builder names that differ only by case will return the same storage identifier
     * (and thus point to the same storage element).
     * You may need to set this value for some specific storage implementations. I.e. some databases expect table or fieldname to be
     * case sensitive, or expect them to be uppercase.
     * By default, this option is not set.
     */
    public static final String STORAGE_IDENTIFIER_CASE = "storage-identifier-case";

    /**
     * Attribute: <code>default-storage-identifier-prefix</code>.
     * A default prefix to place in front of diallowed fieldnames to make them suitabel for use in a storage layer.
     * By default, this option is not set.
     */
    public static final String DEFAULT_STORAGE_IDENTIFIER_PREFIX = "default-storage-identifier-prefix";

    
    /**
     * This attribute can be used to specify a CharTransformer class of which an instance will be
     * used to filter strings which are set into the database.
     * @since MMBase-1.7.4
     */
    public static final String SET_SURROGATOR  = "storage-set-surrogator";

    /**
     * This attribute can be used to specify a CharTransformer class of which an instance will be
     * used to filter strings which are fetched from the database.
     * @since MMBase-1.7.4
     */
    public static final String GET_SURROGATOR  = "storage-get-surrogator";


    /**
     * This attribute can be used to specify the maximum identifier length for
     * table names and key names. 
     * @since MMBase-1.8.0
     */
    public static final String MAX_IDENTIFIER_LENGTH = "max-identifier-length";
}

