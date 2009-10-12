/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.mock;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;

/**

 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.9.2
 * @todo    EXPERIMENTAL
 */

public class MockNode extends MapNode  {

    private final Map<String, Object> originalMap;
    private final MockCloudContext cloudContext;

    MockNode(Map<String, Object> map, MockCloudContext cc, NodeManager nm) {
        super(new HashMap<String, Object>(map), nm);
        originalMap = map;
        cloudContext = cc;
    }
    @Override
    public  void commit() {
        Collection<String> errors = validate();
        if (errors.size() > 0) {
            throw new IllegalArgumentException("node " + getNumber() + getChanged() + ", builder '" + nodeManager.getName() + "' " + errors);
        }
        if (! originalMap.containsKey("number")) {
            // This is a new node, so generate a number first
            int number = cloudContext.addNode(getNodeManager().getName(), values);
            values.put("number", number);
        }
        originalMap.putAll(values);
    }
    @Override
    public Object getValueWithoutProcess(String fieldName) {
        if (!getNodeManager().hasField(fieldName))  {
            throw new NotFoundException("No field '" + fieldName + "' in " + getNodeManager());
        }
        return super.getValueWithoutProcess(fieldName);
    }

    @Override
    public String toString() {
        return getNodeManager().toString() + values;
    }
}
