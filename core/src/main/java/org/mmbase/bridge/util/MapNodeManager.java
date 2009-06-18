/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import org.mmbase.core.CoreField;
import org.mmbase.core.util.Fields;
import org.mmbase.bridge.implementation.BasicField;
import java.util.*;
import org.mmbase.bridge.*;

/**
 * A bridge NodeManager based on a Map of node values. The type of the values in the mapped is used to infer the 'fieldTypes'.
 *
 * This happens lazily.
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.9
 */

public class MapNodeManager extends AbstractNodeManager  {

    protected final Map<String, Object> map;
    private final Map<String, Field> fieldTypes = new HashMap<String, Field>();
    private boolean checked = false;
    private final boolean implicitCreate;

    public MapNodeManager(Cloud cloud, Map<String, Object> m, boolean create) {
        super(cloud);
        map = m;
        implicitCreate = create;
    }

    public MapNodeManager(Cloud cloud, Map<String, Object> m) {
        this(cloud, m, false);
    }
    protected void check() {
        if (! checked) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String fieldName = entry.getKey();
                Object value = entry.getValue();
                mapField(fieldName, value);
            }
            checked = true;
        }
    }
    private Field mapField(String fieldName, Object value) {
        Field field = fieldTypes.get(fieldName);
        if (field == null) {
            CoreField fd = Fields.createField(fieldName, Fields.classToType(value == null ? Object.class : value.getClass()),
                                              Field.TYPE_UNKNOWN, Field.STATE_VIRTUAL, null);
            field = new BasicField(fd, this);
            fieldTypes.put(fieldName, field);
        }
        return field;
    }
    @Override
    protected Map<String, Field> getFieldTypes() {
        check();
        return fieldTypes;
    }
    // override for performance
    @Override
    public boolean hasField(String fieldName) {
        if (implicitCreate) {
            return true;
        } else {
            return map.containsKey(fieldName);
        }
    }
    // override for performance
    @Override
    public Field getField(String fieldName) throws NotFoundException {
        Field f = fieldTypes.get(fieldName);
        if (f == null) {
            if (map.containsKey(fieldName)) {
                f = mapField(fieldName, map.get(fieldName));
            }
        }
        if (f == null) {
            if (implicitCreate) {
                return mapField(fieldName, null);
            } else {
                throw new NotFoundException("Field '" + fieldName + "' does not exist in NodeManager '" + getName() + "'.(" + getFieldTypes() + ")");
            }
        }
        return f;
    }

}
