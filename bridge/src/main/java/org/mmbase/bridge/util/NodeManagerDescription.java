/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.util.xml.ParentBuilderReader;

/**
 * This container class contains the information for instantation of an actual {@link NodeManager}
 * object. It can be stored in {@link CloudContext} implementation, and be used to instantiate
 * actual NodeManagers from a Cloud. NodeManagers do contain a Cloud {@link NodeManager#getCloud}
 * and hence cannot themselves be used to store in the CloudContext.
 *
 * @author  Michiel Meeuwissen
 * @version $Id: MockCloudContext.java 39095 2009-10-13 09:14:33Z michiel $
 * @since   MMBase-1.9.2
 * @todo    EXPERIMENTAL
 */


public class NodeManagerDescription {
    public final String name;
    public final Map<String, Field> fields;
    public final ParentBuilderReader reader;
    public final Map<String, String> properties = new HashMap<String, String>();
    public final int oType;

    public NodeManagerDescription(ParentBuilderReader r, int oType) {
        name = r.getName();
        reader = r;
        fields = new HashMap<String, Field>();
        for (Field f : reader.getFields()) {
            fields.put(f.getName(), f);
        }
        this.oType = oType;
    }
    public NodeManagerDescription(String n, Map<String, Field> map, int oType) {
        name = n;
        fields = map;
        reader = null;
        this.oType = oType;
    }

    @Override
    public String toString() {
        return name + ":" + fields;
    }
}

