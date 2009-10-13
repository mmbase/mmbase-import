/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.mock;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import org.mmbase.bridge.implementation.*;
import org.mmbase.security.*;
import java.util.*;

/**
 * Straight forward (partial) implementation of Cloud, which maintains everything in memory.
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.9.2
 * @todo    EXPERIMENTAL
 */

public class MockCloud extends AbstractCloud {

    final MockCloudContext cloudContext;

    MockCloud(String n, MockCloudContext cc, UserContext uc) {
        super(n, uc);
        cloudContext = cc;
    }


    Node getNode(final Map<String, Object> m, final NodeManager nm) {
        return new MockNode(m, cloudContext, nm);
    }

    public Node getNode(int number) throws NotFoundException {
        MockCloudContext.NodeDescription nd = cloudContext.nodes.get(number);
        if (nd == null) throw new NotFoundException();
        NodeManager nm = getNodeManager(nd.type);
        return getNode(nd.values, nm);
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
        MockCloudContext.NodeManagerDescription d = cloudContext.nodeManagers.get(name);
        if (d == null) {
            throw new NotFoundException("No such node manager '" + name + "'");
        }
        return new MockNodeManager(this, d);
    }


    public boolean hasNodeManager(String name) {
        return cloudContext.nodeManagers.containsKey(name);
    }

    public boolean hasRole(String roleName) {
        return roleName.equals("related") || roleName.equals("posrel");
    }

    public boolean hasRelationManager(String roleName) {
        return roleName.equals("related") || roleName.equals("posrel");
    }
    public boolean hasRelationManager(NodeManager sourceManager, NodeManager destinationManager, String roleName) {
        return hasRelationManager(roleName);
    }

    public MockCloudContext getCloudContext() {
        return cloudContext;
    }
    private final QueryHandler aggregatedQueryHandler = new AggregatedQueryHandler(this);
    private final NodeQueryHandler nodeQueryHandler = new NodeQueryHandler(this);

    public NodeList getList(final Query query) {
        if (query.isAggregating()) {
            List<Map<String, Object>> aggregatedResult = aggregatedQueryHandler.getRecords(query);
            NodeManager tempNodemanager = new MapNodeManager(this, aggregatedResult.get(0));
            return new BasicNodeList(aggregatedResult, tempNodemanager);
        }  else if (query instanceof NodeQuery) {
            List<Map<String, Object>> result = nodeQueryHandler.getRecords(query);
            return new BasicNodeList(result, ((NodeQuery) query).getNodeManager());
        } else {
            throw new UnsupportedOperationException();
        }
    }


}

