package org.mmbase.bridge.util;

import org.mmbase.bridge.*;
/**
 * @since MMBase-2.0
 */
public class VirtualNodeManagerField extends FieldWrapper {


    private final String name;
    private final NodeManager nodeManager;

    public VirtualNodeManagerField(NodeManager nm, Field field, String name)  {
        super(field);
        this.name = name;
        this.nodeManager = nm;
    }
    @Override
    public NodeManager getNodeManager() {
        return nodeManager;
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public int getState() {
        return Field.STATE_VIRTUAL;
    }

    @Override
    public int compareTo(Field o) {
        return name.compareTo(o.getName());
    }
}

