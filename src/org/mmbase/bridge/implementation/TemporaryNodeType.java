/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;

/**
 * This interface represents a node's type information object - what used to be the 'builder'.
 * It contains all the field and attribuut information, as well as GUI data for editors and
 * some information on deribed and deriving types.
 * Since node types are normally maintained through use of config files (and not in the database),
 * as wel as for security issues, the data of a nodetype cannot be changed except through
 * the use of an administration module (whcih is why we do not include setXXX methods here).
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 */
public class TemporaryNodeType extends BasicNodeType {

    TemporaryNodeType(MMObjectNode node, Cloud cloud) {
        this.cloud=cloud;
        this.builder=node.parent;
        // determine fields and field types
        for (Enumeration e = node.values.keys(); e.hasMoreElements(); ) {
            String fieldName=(String)e.nextElement();
            Object value = node.values.get(fieldName);
            int fieldType = FieldType.FIELDTYPE_UNKNOWN;
            if (value instanceof String) {
                fieldType = FieldType.FIELDTYPE_STRING;
            }
            if (value instanceof Integer) {
                fieldType = FieldType.FIELDTYPE_INTEGER;
            }
            if (value instanceof  byte[]) {
                fieldType = FieldType.FIELDTYPE_BYTE;
            }
            if (value instanceof  Float) {
                fieldType = FieldType.FIELDTYPE_FLOAT;
            }
            if (value instanceof  Double) {
                fieldType = FieldType.FIELDTYPE_DOUBLE;
            }
            if (value instanceof  Long) {
                fieldType = FieldType.FIELDTYPE_LONG;
            }
            FieldDefs fd= new FieldDefs(fieldName, "field", -1, -1, fieldName, fieldType,-1, FieldType. FIELDSTATE_VIRTUAL);
            FieldType ft = new BasicFieldType(fd,this);
            fieldTypes.put(fieldName,ft);
        }
    }

    /**
     * Gets a new (initialized) node.
     * Throws an exception since this type is virtual, and creating nodes is not allowed.
     */
    public Node createNode() {
        throw new SecurityException("Cannot create a node from a temporary node type");
    }

	/**
     * search nodes of this type
     * @param where the contraint
     * @param order the field on which you want to sort
     * @param direction true=UP false=DOWN
     */
    public List search(String where, String sorted, boolean direction) {
        throw new SecurityException("Cannot perform search on a temporary node type");
    }
}
