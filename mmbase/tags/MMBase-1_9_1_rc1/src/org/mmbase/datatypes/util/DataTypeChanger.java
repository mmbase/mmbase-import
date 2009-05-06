/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.datatypes.util;


import org.mmbase.datatypes.*;
import org.mmbase.bridge.Field;


/**
 * This utility class makes it easy to configure a DataType on the fly in java.
 *
 * E.g. this code could be used at EO:
 <pre>
   new FieldType("initials",  new DataTypeChanger("line", "Voornaam").required(true).maxLength(10).finish(), 1, FieldType.UNCHANGEABLE)
 </pre>
 *
 * @author Michiel Meeuwisen
 * @since  MMBase-1.9.1
 * @version $Id$
 */

public class DataTypeChanger {


    private final DataType<?> dataType;

    public DataTypeChanger(DataType<?> dt) {
        dataType = (DataType<?>) dt.clone();
    }

    public DataTypeChanger(String dt, String guiName) {
        dataType = (DataType<?>) DataTypes.getDataType(dt).clone(); // casting for 1.8 compatibility only
        dataType.setGUIName(guiName);
    }

    public DataTypeChanger(Field field) {
        dataType = (DataType<?>) field.getDataType().clone();
    }


    public DataTypeChanger required(boolean required) {
        dataType.setRequired(required);
        return this;
    }

    public DataTypeChanger maxLength(long maxLength) {
        if (dataType instanceof LengthDataType<?>) {
            ((LengthDataType<?>) dataType).setMaxLength(maxLength);
        }
        return this;
    }

    public DataTypeChanger guiName(String guiName) {
        dataType.setGUIName(guiName);
        return this;
    }

    public DataType<?> finish() {
        dataType.finish(this);
        return dataType;
    }

}
