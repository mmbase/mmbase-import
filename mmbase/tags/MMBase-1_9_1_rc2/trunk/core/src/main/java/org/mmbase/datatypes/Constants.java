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
 * @version $Id$
 */

public class Constants {

    // DataTypes for base MMBase field types

    public static final BasicDataType DATATYPE_INTEGER  = DataTypes.getDataType(Field.TYPE_INTEGER);
    public static final BasicDataType DATATYPE_LONG     = DataTypes.getDataType(Field.TYPE_LONG);
    public static final BasicDataType DATATYPE_FLOAT    = DataTypes.getDataType(Field.TYPE_FLOAT);
    public static final BasicDataType DATATYPE_DOUBLE   = DataTypes.getDataType(Field.TYPE_DOUBLE);
    public static final BasicDataType DATATYPE_STRING   = DataTypes.getDataType(Field.TYPE_STRING);
    public static final BasicDataType DATATYPE_XML      = DataTypes.getDataType(Field.TYPE_XML);
    public static final BasicDataType DATATYPE_DATETIME = DataTypes.getDataType(Field.TYPE_DATETIME);
    public static final BasicDataType DATATYPE_BOOLEAN  = DataTypes.getDataType(Field.TYPE_BOOLEAN);
    public static final BasicDataType DATATYPE_BINARY   = DataTypes.getDataType(Field.TYPE_BINARY);
    public static final BasicDataType DATATYPE_NODE     = DataTypes.getDataType(Field.TYPE_NODE);
    public static final BasicDataType DATATYPE_UNKNOWN  = DataTypes.getDataType(Field.TYPE_UNKNOWN);

    /*
    public static final BasicDataType DATATYPE_LIST_UNKNOWN  = BasicDataTypes.getListDataType(Field.TYPE_UNKNOWN);
    public static final BasicDataType DATATYPE_LIST_INTEGER  = BasicDataTypes.getListDataType(Field.TYPE_INTEGER);
    public static final BasicDataType DATATYPE_LIST_LONG     = BasicDataTypes.getListDataType(Field.TYPE_LONG);
    public static final BasicDataType DATATYPE_LIST_FLOAT    = BasicDataTypes.getListDataType(Field.TYPE_FLOAT);
    public static final BasicDataType DATATYPE_LIST_DOUBLE   = BasicDataTypes.getListDataType(Field.TYPE_DOUBLE);
    public static final BasicDataType DATATYPE_LIST_STRING   = BasicDataTypes.getListDataType(Field.TYPE_STRING);
    public static final BasicDataType DATATYPE_LIST_XML      = BasicDataTypes.getListDataType(Field.TYPE_XML);
    public static final BasicDataType DATATYPE_LIST_DATETIME = BasicDataTypes.getListDataType(Field.TYPE_DATETIME);
    public static final BasicDataType DATATYPE_LIST_BOOLEAN  = BasicDataTypes.getListDataType(Field.TYPE_BOOLEAN);
    public static final BasicDataType DATATYPE_LIST_NODE     =
    BasicDataTypes.getListDataType(Field.TYPE_NODE);
    */
}
