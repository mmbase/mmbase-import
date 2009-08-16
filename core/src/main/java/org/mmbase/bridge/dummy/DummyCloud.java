/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.dummy;

import java.util.*;
import java.util.concurrent.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import org.mmbase.bridge.implementation.*;
import org.mmbase.security.*;
import org.mmbase.datatypes.DataType;
import org.mmbase.util.*;
import org.mmbase.util.functions.*;

/**
 * Straight forward (partial) implementation of Cloud, which maintains everything in memory.
 *
 * @author  Michiel Meeuwissen
 * @version $Id: MapNode.java 36154 2009-06-18 22:04:40Z michiel $
 * @since   MMBase-1.9.2
 * @todo    EXPERIMENTAL
 */

public class DummyCloud extends AbstractCloud {

    private final DummyCloudContext cloudContext;

    DummyCloud(String n, DummyCloudContext cc, UserContext uc) {
        super(n, uc);
        cloudContext = cc;
    }


    Node getNode(final Map<String, Object> m, final NodeManager nm) {
        return new DummyNode(m, cloudContext, nm);
    }

    public Node getNode(int number) throws NotFoundException {
        Map<String, Object> n = cloudContext.nodes.get(number);
        if (n == null) throw new NotFoundException();
        NodeManager nm = getNodeManager(cloudContext.nodeTypes.get(number));
        return getNode(n, nm);
    }

    public boolean hasNode(int number) {
        return cloudContext.nodes.containsKey(number);
    }

    public NodeManagerList getNodeManagers() {
        List<NodeManager> list = new ArrayList<NodeManager>();
        for (String name : cloudContext.nodeManagers.keySet()) {
            list.add(getNodeManager(name));
        }
        return new BasicNodeManagerList(list, this);
    }

    public NodeManager getNodeManager(String name) throws NotFoundException {
        Map<String, Field> nm = cloudContext.nodeManagers.get(name);
        if (nm == null) throw new NotFoundException(name);
        return new DummyNodeManager(this, name, nm);
    }


    public boolean hasNodeManager(String name) {
        return cloudContext.nodeManagers.containsKey(name);
    }

    public boolean hasRole(String roleName) {
        return roleName.equals("related") || roleName.equals("posrel");
    }

    public RelationManager getRelationManager(String roleName) throws NotFoundException {
        throw new UnsupportedOperationException();
    }

    public boolean hasRelationManager(String roleName) {
        return roleName.equals("related") || roleName.equals("posrel");
    }
    public boolean hasRelationManager(NodeManager sourceManager, NodeManager destinationManager, String roleName) {
        return hasRelationManager(roleName);
    }
    public RelationManager getRelationManager(NodeManager sourceManager, NodeManager destinationManager, String roleName) {
        throw new UnsupportedOperationException();
    }



    public RelationManagerList getRelationManagers() {
        throw new UnsupportedOperationException();
    }


    public RelationManagerList getRelationManagers(NodeManager sourceManager, NodeManager destinationManager,
                                                   String roleName) throws NotFoundException {
        throw new UnsupportedOperationException();
    }

    public CloudContext getCloudContext() {
        return cloudContext;
    }


}

