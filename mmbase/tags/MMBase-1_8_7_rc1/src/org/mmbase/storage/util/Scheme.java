/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.util;

import java.text.MessageFormat;

import org.mmbase.storage.*;

/**
 * This is a specialised version of the MessageFormat class, with some awareness of
 * MMBase objects. You can pass MMBase objects to Scheme when formatting a pattern.
 * The Scheme automatically resolves the object to a value it can use in the pattern.
 * Schemes are used by the storage to create configurable storage instructions (specifically database SQL code).
 *
 * @author Pierre van Rooden
 * @version $Id$
 * @since MMBase-1.7
 */
public final class Scheme extends MessageFormat {

    /**
     * The factory this scheme belongs to.
     */
    private StorageManagerFactory factory;

    private String orgpattern;

    /**
     * Instantiate the Scheme
     * @param factory The factory this scheme belongs to.
     * @param pattern The pattern to use for the scheme
     */
    public Scheme (StorageManagerFactory factory, String pattern) {
        super(pattern);
        orgpattern = pattern;
        this.factory = factory;
    }

    /**
     * Resolves an object (passed as a parameter) to a value that can be applied in a pattern.
     * It returns:
     * <ul>
     *  <li>For MMBase: the object storage element identifier as a String (fully expanded table name)</li>
     *  <li>For MMObjectBuilder: the builder storage element identifier as a String (fully expanded table name)</li>
     *  <li>For MMObjectNode: the object number as an Integer</li>
     *  <li>For CoreField: a storage-compatible field name as a String (if no such name exists a StorageException is thrown)</li>
     * </ul>
     * Other object types are returned as is, leaving them to be handled by MessageFormat's formatting code.
     *
     * @todo MMBase, MMObjectNode, and MMObjectBuilder should be enriched with a Storable interface, which
     *       can be used instead
     * @param param the object to resolve
     * @return the resolved value
     * @throws StorageException if the object cannot be resolved
     */
    protected Object resolveParameter(Object param) throws StorageException {
        if (param == null || param instanceof String || param instanceof Number) {
            return param;
        } else if (param instanceof Storable) {
            return ((Storable)param).getStorageIdentifier();
        } else {
            return factory.getStorageIdentifier(param);
        }
    }

    /**
     * Applies the parameters to the scheme's pattern.
     * @param params an array of parameters to apply to the pattern
     * @return the result scheme as a String
     * @throws StorageException if one of the passed parameters cannot be resolved
     */
    public String format(Object[] params) throws StorageException {
        for (int i = 0; i < params.length; i++) {
            params[i] = resolveParameter(params[i]);
        }
        return super.format(params);
    }

    public String toString() {
        return orgpattern;
    }

}

