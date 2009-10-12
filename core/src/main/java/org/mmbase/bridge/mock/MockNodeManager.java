/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.mock;

import static org.mmbase.datatypes.Constants.*;
import org.mmbase.bridge.util.*;
import java.util.*;
import org.mmbase.bridge.*;

/**
 * Straight-forward implementation of NodeManager based on a Map with DataType's.
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.9.2
 */

public class MockNodeManager extends AbstractNodeManager  {

    protected final Map<String, Field> map = new LinkedHashMap<String, Field>();
    protected final String name;
    protected final String parent;
    protected final MockCloud vcloud;
    protected final int oType;

    public MockNodeManager(MockCloud cloud, MockCloudContext.NodeManagerDescription desc) {
        super(cloud);
        this.vcloud = cloud;
        this.name = desc.name;
        for (Map.Entry<String, Field> entry : desc.fields.entrySet()) {
            map.put(entry.getKey(), new MockField(this, entry.getValue()));
        }
        map.put("_number", new DataTypeField("_number", this, DATATYPE_INTEGER));
        map.put("_exists", new DataTypeField("_exists", this, DATATYPE_STRING));
        this.oType = desc.oType;
        String e = desc.reader != null ? desc.reader.getExtends() : null;
        if (e != null && e.length() == 0) e = null;
        this.parent = e;
    }

    public MockNodeManager(MockCloud cloud, MockBuilderReader reader) {
        super(cloud);
        this.vcloud = cloud;
        this.name = reader.getName();
        for (Field f : reader.getFields()) {
            map.put(f.getName(), f);
        }
        oType = -1;
        this.parent = reader.getExtends();
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public Node createNode() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("otype", oType);
        return vcloud.getNode(map, this);
    }


    @Override
    public Map<String, String> getProperties() {
        return vcloud.cloudContext.nodeManagers.get(getName()).properties;
    }

    @Override
    protected Map<String, Field> getFieldTypes() {
        return Collections.unmodifiableMap(map);
    }



    @Override
    public NodeManagerList getDescendants() {
        NodeManagerList descendants = vcloud.createNodeManagerList();
        for (String nodeManagerName  : vcloud.cloudContext.nodeManagers.keySet()) {
            NodeManager nm = vcloud.getNodeManager(nodeManagerName);
            try {
                NodeManager parent = nm.getParent();
                if (parent != null && name.equals(parent.getName())) {
                    if (! descendants.contains(nm)) {
                        descendants.add(nm);
                        for (NodeManager sub : nm.getDescendants()) {
                            descendants.add(sub);
                        }
                    }
                }
            } catch (NotFoundException nfe) {
                // never mind, getParent may do that, it simply means that it is object or so.
            }
        }
        return descendants;
    }


    @Override
    public NodeManager getParent() throws NotFoundException {
        if (parent == null) {
            throw new NotFoundException();
        } else {
            return vcloud.getNodeManager(parent);
        }
    }


    @Override
    public String toString() {
        return getName() + " " +  map.keySet();
    }

}
