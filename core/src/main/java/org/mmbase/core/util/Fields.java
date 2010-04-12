/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.core.util;

import org.mmbase.bridge.*;
import org.mmbase.datatypes.*;
import org.mmbase.core.CoreField;
import java.util.*;

/**
 * Eĥoŝanĝo, ĉiuĵaŭde.
 * @since MMBase-1.8
 * @version $Id$
 */
public class Fields extends org.mmbase.bridge.Fields {

    /**
     * Returns an instance of a CoreField based on the type, with state 'SYSTEM', and a basic datatype assigned.
     * @param name The name of the field
     * @param type the MMBase basic field type, one of the {@link Field} TYPE constants. Specifying {@link Field#TYPE_LIST},
     *             may give unpredictable results.
     */
    public static CoreField createSystemField(String name, int type) {
        return createField(name, type, Field.TYPE_UNKNOWN, Field.STATE_SYSTEM, null);
    }
    /**
     * Defaulting version of {@link #createField(String, int int, int, DataType)} (no list item type,
     * because it is nearly always irrelevant).
     * @since MMBase-1.9
     */
    public static CoreField createField(String name, int type, int state, DataType dataType) {
        return createField(name, type, Field.TYPE_UNKNOWN, state, dataType);
    }


    /**
     * Returns an instance of a CoreField based on the type and state.
     * @param name The name of the field
     * @param type the MMBase basic field type, one of the {@link Field} TYPE constants.
     * @param listItemType the MMBase type for items of a list (if type is {@link Field#TYPE_LIST}).
     * @param state the MMBase field state, one of the {@link Field} STATE constants.
     * @param dataType the <em>unfinished</em> dataType to use for validating the field data. If <code>null</code>, a default datatype is assigned
     */
    public static CoreField createField(String name, int type, int listItemType, int state, DataType dataType) {
        if (dataType == null) {
            if (type == Field.TYPE_LIST) {
                dataType = (DataType)DataTypes.getListDataType(listItemType).clone();
            } else {
                dataType = (DataType)DataTypes.getDataType(type).clone();
            }
        }
        return new org.mmbase.module.corebuilders.FieldDefs(name, type, listItemType, state, dataType);
    }


}
