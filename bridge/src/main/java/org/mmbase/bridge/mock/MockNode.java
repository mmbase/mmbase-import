/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.mock;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.implementation.*;
import org.mmbase.bridge.util.*;

/**
 * MockNodes belong to a {@link MockCloud}. They represent {@linkplain
 * MockCloudContext#NodeDescription data} in memory of a {@link MockCloudContext}. An even simpler
 * Node mocker is {@link MapNode}. This one is a bit more sophisticated because it does actually
 * implement methods {@link #commit} and {@link #isNew} too.
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.9.2
 */

public class MockNode extends MapNode<Object> implements Node  {
    private final MockCloudContext.NodeDescription nodeDescription;


    protected final MockCloud cloud;
    private String context = "default";
    private boolean isNew;

    MockNode(MockCloudContext.NodeDescription nodeDescription, MockCloud cloud, boolean isNew) {
        super(new HashMap<String, Object>(nodeDescription.values), cloud.getNodeManager(nodeDescription.type));
        this.nodeDescription = nodeDescription;
        this.cloud = cloud;
        this.isNew = isNew;
    }

    @Override
    public  void commit() {
        Collection<String> errors = validate();
        if (errors.size() > 0) {
            throw new IllegalArgumentException("node " + getNumber() + getChanged() + ", builder '" + nodeManager.getName() + "' " + errors);
        }
        if (! nodeDescription.values.containsKey("number")) {
            // This is a new node, so generate a number first
            int number = cloud.getCloudContext().addNode(nodeDescription);
            values.put("number", number);
        }
        nodeDescription.values.putAll(values);
        cloud.getCloudContext().setNodeType(getNumber(), getNodeManager().getName());
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
        StringList sl = cloud.getCloudContext().createStringList();
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

    @Override
    public StringList getAliases() {
        return new BasicStringList(nodeDescription.aliases);
    }

    @Override
    public void createAlias(String aliasName) {
        nodeDescription.aliases.add(aliasName);
    }

    @Override
    public void deleteAlias(String aliasName) {
        nodeDescription.aliases.remove(aliasName);
    }

}
