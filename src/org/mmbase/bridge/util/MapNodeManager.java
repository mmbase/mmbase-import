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
 * @version $Id: MapNodeManager.java,v 1.1 2006-09-06 13:49:36 michiel Exp $
 * @since   MMBase-1.9
 */

public class MapNodeManager extends AbstractNodeManager  {

    protected final Map map;
    private Map fieldTypes = null;
    public MapNodeManager(Cloud cloud, Map m) {
        super(cloud);
        map = m;
    }
    protected void check() {
        if (fieldTypes == null) {
            fieldTypes = new HashMap();
            Iterator i = map.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry entry = (Map.Entry) i.next();
                String fieldName = (String) entry.getKey();
                Object value = entry.getValue();
                CoreField fd = Fields.createField(fieldName, Fields.classToType(value == null ? Object.class : value.getClass()),
                                                  Field.TYPE_UNKNOWN, Field.STATE_VIRTUAL, null);
                Field ft = new BasicField(fd, this);
                fieldTypes.put(fieldName, ft);
            }
        }
    }
    protected Map getFieldTypes() {
        check();
        return fieldTypes;
    }

}