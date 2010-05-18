/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.core.util;

import org.mmbase.bridge.*;
import org.mmbase.datatypes.*;
import org.mmbase.core.AbstractField;



/**
 * Wraps a Field. It can and is extended to make some other modifications to the field once the datatype is determined.
 * @since MMBase-1.9.4
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class  DataTypeSetter  {
    private int type = -1;
    private int listItemType = -1;
    protected AbstractField field;
    public DataTypeSetter(AbstractField field) {
        this.field = field;
    }
    public void set(DataType dt) {
        field.setDataType(dt);
    }
    public AbstractField getField() {
        return field;
    }
    public int getType() {
        return type == -1 ? field.getType() : type;
    }
    public int getListItemType() {
        return listItemType == -1 ? field.getListItemType() : listItemType;
    }
    public void setType(int t) {
        type = t;
    }
    public void setListItemType(int t) {
        listItemType = t;
    }

}


