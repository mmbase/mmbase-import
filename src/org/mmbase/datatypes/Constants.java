/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.datatypes;

import org.mmbase.bridge.Field;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @since  MMBase-1.8
 * @version $Id: Constants.java,v 1.1 2005-08-16 14:05:17 pierre Exp $
 */

public class Constants {

    // DataTypes for base MMBase field types
    public static final DataType DATATYPE_INTEGER  = DataTypes.getDataType(Field.TYPE_INTEGER);
    public static final DataType DATATYPE_LONG     = DataTypes.getDataType(Field.TYPE_LONG);
    public static final DataType DATATYPE_FLOAT    = DataTypes.getDataType(Field.TYPE_FLOAT);
    public static final DataType DATATYPE_DOUBLE   = DataTypes.getDataType(Field.TYPE_DOUBLE);
    public static final DataType DATATYPE_STRING   = DataTypes.getDataType(Field.TYPE_STRING);
    public static final DataType DATATYPE_XML      = DataTypes.getDataType(Field.TYPE_XML);
    public static final DataType DATATYPE_DATETIME = DataTypes.getDataType(Field.TYPE_DATETIME);
    public static final DataType DATATYPE_BOOLEAN  = DataTypes.getDataType(Field.TYPE_BOOLEAN);
    public static final DataType DATATYPE_BINARY   = DataTypes.getDataType(Field.TYPE_BINARY);
    public static final DataType DATATYPE_NODE     = DataTypes.getDataType(Field.TYPE_NODE);
    public static final DataType DATATYPE_UNKNOWN  = DataTypes.getDataType(Field.TYPE_UNKNOWN);

    public static final DataType DATATYPE_LIST_UNKNOWN = DataTypes.getListDataType(Field.TYPE_UNKNOWN);
    public static final DataType DATATYPE_LIST_INTEGER = DataTypes.getListDataType(Field.TYPE_INTEGER);
    public static final DataType DATATYPE_LIST_LONG = DataTypes.getListDataType(Field.TYPE_LONG);
    public static final DataType DATATYPE_LIST_FLOAT = DataTypes.getListDataType(Field.TYPE_FLOAT);
    public static final DataType DATATYPE_LIST_DOUBLE = DataTypes.getListDataType(Field.TYPE_DOUBLE);
    public static final DataType DATATYPE_LIST_STRING = DataTypes.getListDataType(Field.TYPE_STRING);
    public static final DataType DATATYPE_LIST_XML = DataTypes.getListDataType(Field.TYPE_XML);
    public static final DataType DATATYPE_LIST_DATETIME = DataTypes.getListDataType(Field.TYPE_DATETIME);
    public static final DataType DATATYPE_LIST_BOOLEAN = DataTypes.getListDataType(Field.TYPE_BOOLEAN);
    public static final DataType DATATYPE_LIST_NODE = DataTypes.getListDataType(Field.TYPE_NODE);
}
