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
 * @version $Id: Attributes.java,v 1.1 2003-08-01 14:16:11 pierre Exp $
 */
public final class Attributes {

    /**
     * Attribute: <code>defaultStorageIdentifierPrefix</code>.
     * When the storage manager encounters a disallowed fieldname for which no replacement is created,
     * it attempts to create a replacement fieldname by prefixing the field with the value set in this property.
     * If this property is not set, the manager will instead issue a StoragException, explaining the fieldname is disallowed
     * By default this attribute is not set.
     */
    public static final String DISALLOWED_FIELD_PREFIX = "defaultStorageIdentifierPrefix";
    
    /**
     * Option: <code>disallowed-fields-case-sensitive</code>.
     * if <code>true</code>, matching MMBase fieldnames with the disallowed fieldnames list is case-sensitive.
     * By default, this option is <code>false</code>.
     * Note that you can specify this attribute sperately, but the "case-sensitive" attribute 
     * of the "disallowedfields" tag overrides this attribute.
     */
    public static final String DISALLOWED_FIELD_CASE_SENSITIVE = "disallowed-fields-case-sensitive";

}
