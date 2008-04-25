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
 * @version $Id: DataTypeField.java,v 1.1 2006-07-18 15:17:00 michiel Exp $
 * @since   MMBase-1.8.2
 */

public  class DataTypeField extends org.mmbase.core.AbstractField {
    protected final NodeManager nodeManager;
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
    }
    public NodeManager getNodeManager() {
        return nodeManager;
    }

    public int getSearchPosition() {
        return -1; // irrelevant, you cannot search
    }

    public int getListPosition() {
        return -1; // irrelevant, you cannot do listings
    }

    public int getEditPosition() {
        return 1;
    }

    public int getStoragePosition() {
        return -1; // irrelevant, not stored
    }

    public int getMaxLength() {
        return Integer.MAX_VALUE; // not stored, so no such restriction
    }

    public String getGUIType() {
        return dataType.getName();
    }
    public Collection validate(Object value) {
        Collection errors = dataType.validate(value, null, this);
        return LocalizedString.toStrings(errors, nodeManager.getCloud().getLocale());
    }


}
