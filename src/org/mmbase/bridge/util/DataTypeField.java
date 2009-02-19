/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import java.util.*;
import org.mmbase.util.LocalizedString;
import org.mmbase.bridge.*;
import org.mmbase.datatypes.DataType;


/**
 * Wraps a DataType object into a (virtual) Field object, with a Virtual NodeManager with only one field
 * (itself). This also associates a Cloud object with the DataType.
 *
 * @author  Michiel Meeuwissen
 * @version $Id: DataTypeField.java,v 1.1.2.1 2008-08-19 12:29:07 michiel Exp $
 * @since   MMBase-1.8.7
 */

public  class DataTypeField extends org.mmbase.core.AbstractField {
    protected final NodeManager nodeManager;
    protected final Field field;
    public DataTypeField(final Cloud cloud, final DataType dataType)  {
        super(dataType.getName(), dataType.getBaseType(), TYPE_UNKNOWN, Field.STATE_VIRTUAL, dataType);
        nodeManager = new AbstractNodeManager(cloud) {
                private final Map fieldTypes = new HashMap();
                {
                    fieldTypes.put(dataType.getName(), DataTypeField.this);
                }
                protected Map getFieldTypes() {
                    return Collections.unmodifiableMap(fieldTypes);
                }
            };
        field = null;
    }
    /**
     * This constructor only wraps the given field to have another datatype.
     * @since MMBase-1.9
     */
    public DataTypeField(final Field field, final DataType dataType)  {
        super(field.getName(), dataType.getBaseType(), field.getType(), field.getState(), dataType);
        nodeManager = field.getNodeManager();
        this.field = field;
    }
    public NodeManager getNodeManager() {
        return nodeManager;
    }


    public int getSearchPosition() {
        return field == null ? -1 : field.getSearchPosition();
    }


    public int getListPosition() {
        return field == null ? -1 : field.getListPosition();
    }


    public int getEditPosition() {
        return field == null ? 1 : field.getEditPosition();
    }


    public int getStoragePosition() {
        return field == null ? -1 : field.getStoragePosition();
    }


    public int getMaxLength() {
        return field == null ? Integer.MAX_VALUE : // not stored, so no such restriction
            field.getMaxLength();
    }

    public boolean isReadOnly() {
        return field == null ? true : field.isReadOnly();
    }


    public String getGUIType() {
        return dataType.getName();
    }
    public Collection validate(Object value) {
        Collection errors = dataType.validate(value, null, this);
        return LocalizedString.toStrings(errors, nodeManager.getCloud().getLocale());
    }


}
