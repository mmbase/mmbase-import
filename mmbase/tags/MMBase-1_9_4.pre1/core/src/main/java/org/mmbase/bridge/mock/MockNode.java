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
 * MockNodes belong to a {@link MockCloud}. They represent {@linkplain
 * MockCloudContext#NodeDescription data} in memory of a {@link MockCloudContext}. An even simpler
 * Node mocker is {@link MapNode}. This one ia a bit more sophisticated because it does actually
 * implements {@link #commit} too.
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.9.2
 */

public class MockNode extends MapNode  {

    private final Map<String, Object> originalMap;
    private final MockCloudContext cloudContext;
    private String context = "default";
    private boolean isNew;

    MockNode(Map<String, Object> map, MockCloudContext cc, NodeManager nm, boolean isNew) {
        super(new HashMap<String, Object>(map), nm);
        originalMap = map;
        cloudContext = cc;
        this.isNew = isNew;
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
        cloudContext.setNodeType(getNumber(), getNodeManager().getName());
        isNew = false;
    }
    @Override
    public Object getValueWithoutProcess(String fieldName) {
        if (!getNodeManager().hasField(fieldName))  {
            throw new NotFoundException("No field '" + fieldName + "' in " + getNodeManager());
        }
        return super.getValueWithoutProcess(fieldName);
    }


    @Override
    public String getContext() {
        return context;
    }
    @Override
    public void setContext(String c) {
        context = c;
    }

    @Override
    public StringList getPossibleContexts() {
        StringList sl = cloudContext.createStringList();
        sl.add(context);
        if (!sl.contains("default")) {
            sl.add("default");
        }
        return sl;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }





    @Override
    public String toString() {
        return getNodeManager().toString() + values;
    }
}
