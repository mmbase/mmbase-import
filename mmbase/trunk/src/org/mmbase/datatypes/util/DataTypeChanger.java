/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.datatypes.util;

import java.util.*;

import org.mmbase.datatypes.*;

import org.mmbase.util.logging.*;

/**
 * This utility class makes it easy to configure a DataType on the fly in java.
 *
 * E.g. this code could be used at EO:
 <pre>
   new FieldType("initials",  new DataTypeChanger("line").setGUIName("Voornaam").setRequired(true).setMaxLength(10).finish(), 1, FieldType.UNCHANGEABLE)
 </pre>
 *
 * @author Michiel Meeuwisen
 * @since  MMBase-1.9.1
 * @version $Id: DataTypeChanger.java,v 1.1 2009-04-06 15:29:13 michiel Exp $
 */

public class DataTypeChanger {


    private final DataType dataType;

    public DataTypeChanger(DataType dt) {
        dataType = dt.clone();
    }

    public DataTypeChanger(String dt) {
        dataType = DataTypes.getDataType(dt).clone();
    }


    public DataTypeChanger setRequired(boolean required) {
        dataType.setRequired(required);
        return this;
    }

    public DataTypeChanger setMaxLength(long maxLength) {
        if (dataType instanceof LengthDataType) {
            ((LengthDataType) dataType).setMaxLength(maxLength);
        }
        return this;
    }

    public DataTypeChanger setGUIName(String guiName) {
        dataType.setGUIName(guiName);
        return this;
    }

    public DataType finish() {
        dataType.finish(this);
        return dataType;
    }

}
