/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.util;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.storage.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This is a specialised version of the MessageFormat class, with some awareness of 
 * MMBase objects. You can pass MMBase objects to Scheme when formatting a pattern.
 * The Scheme automatically resolves the object to a value it can use in the pattern.
 * Schemes are used by the storage to create configurable storage instructions (specifically database SQL code).
 *
 * @author Pierre van Rooden
 * @version $Id: Scheme.java,v 1.1 2003-07-25 12:42:07 pierre Exp $
 */
public final class Scheme extends MessageFormat {

    // logger
    private static Logger log = Logging.getLoggerInstance(Scheme.class);
    
    /**
     * The factory this scheme belongs to.
     */
    protected StorageManagerFactory factory;
    
    /**
     * Instantiate the Scheme
     * @param factory The factory this scheme belongs to.
     * @param pattern The pattern to use for the scheme
     */
    public Scheme (StorageManagerFactory factory, String pattern) {
        super(pattern);
        this.factory = factory;
    }

    /**
     * Resolves an object (passed as a parameter) to a value that can be applied in a pattern.
     * It returns:
     * <ul>
     *  <li>For MMObjectBuilder: the builder storage element identifier as a String (fully expanded table name)</li>
     *  <li>For MMObjectNode: the object number as an Integer</li>
     *  <li>For FieldDefs: a storage-compatible field name as a String (if no such name exists a StorageException is thrown)</li>
     * </ul>
     * Other object types are returned as is, leaving them to be handled by MessageFormat's formatting code.
     *
     * @param param the object to resolve
     * @return the resolved value
     * @throws StorageException if the object cannot be resolved
     */
    protected Object resolveParameter(Object param) throws StorageException {
        if (param instanceof MMObjectBuilder) {
            return ((MMObjectBuilder)param).getFullTableName();
        } else if (param instanceof MMObjectNode) {
            return ((MMObjectNode)param).getIntegerValue("number");
        } else if (param instanceof FieldDefs) {
            String name = ((FieldDefs)param).getDBName();
            Map disallowedFields = factory.getDisallowedFields();
            if (disallowedFields.containsKey(name)) {
                String newName = (String)disallowedFields.get(name);
                if (newName == null) {
                    throw new StorageException("The name of the field '"+name+"' is disallowed, and no alternate value is available.");
                }
                return newName;
            } else {
                return name;
            }
        } else {
            return param;
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

}
