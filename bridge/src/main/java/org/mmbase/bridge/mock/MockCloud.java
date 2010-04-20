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
import org.mmbase.util.Casting;
import org.mmbase.util.logging.*;
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

    private static final Logger LOG = Logging.getLoggerInstance(MockCloud.class);

    final MockCloudContext cloudContext;

    MockCloud(String n, MockCloudContext cc, UserContext uc) {
        super(n, uc);
        cloudContext = cc;
    }


    Node getNode(MockCloudContext.NodeDescription nd, boolean n) {
        return new MockNode(nd, this, n);
    }

    @Override
    public Node getNode(int number) throws NotFoundException {
        MockCloudContext.NodeDescription nd = cloudContext.nodes.get(number);
        if (nd == null) {
            throw new NotFoundException("No node with number " + number + " found in " + this);
        }
        if (nd.type.equals("typedef")) {
            return  getNodeManager(org.mmbase.util.Casting.toString(nd.values.get("name")));
        } else {
            NodeManager nm = getNodeManager(nd.type);
            return getNode(nd, false);
        }
    }
    @Override
    public Node getNodeByAlias(String alias) throws NotFoundException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Finding node with alias '" + alias + "' in " + cloudContext.nodes);
        }
        for (Map.Entry<Integer, MockCloudContext.NodeDescription> nd : cloudContext.nodes.entrySet()) {
            if (nd.getValue().aliases.contains(alias)) {
                return getNode(nd.getKey());
            }
        }
        throw new NotFoundException("No node with alias '" + alias + "' found in " + this);
    }

    @Override
    public boolean hasNode(int number) {
        return cloudContext.nodes.containsKey(number);
    }

    @Override
    public NodeManagerList getNodeManagers() {
        List<NodeManager> list = new ArrayList<NodeManager>();
        for (String name : cloudContext.nodeManagers.keySet()) {
            list.add(getNodeManager(name));
        }
        return new BasicNodeManagerList(list, this);
    }

    @Override
    public NodeManager getNodeManager(String name) throws NotFoundException {
        NodeManagerDescription d = cloudContext.nodeManagers.get(name);
        if (d == null) {
            throw new NotFoundException("No such node manager '" + name + "' in " + cloudContext);
        }
        return new MockNodeManager(this, d);
    }


    @Override
    public MockCloudContext getCloudContext() {
        return cloudContext;
    }
    private final QueryHandler aggregatedQueryHandler = new AggregatedQueryHandler(this);
    private final NodeQueryHandler nodeQueryHandler = new NodeQueryHandler(this);
    private final MultilevelQueryHandler queryHandler = new MultilevelQueryHandler(this);

    @Override
    public NodeList getList(final Query query) {
        if (query.isAggregating()) {
            List<Map<String, Object>> aggregatedResult = aggregatedQueryHandler.getRecords(query);
            NodeManager tempNodemanager = new MapNodeManager(this, aggregatedResult.get(0));
            return new SimpleNodeList(aggregatedResult, tempNodemanager);
        }  else if (query instanceof NodeQuery) {
            List<Map<String, Object>> result = nodeQueryHandler.getRecords(query);
            final NodeManager nm =  ((NodeQuery) query).getNodeManager();
            return new SimpleNodeList(result, nm) {
                @Override
                protected Node convert(Object o) {
                    if (o == null) {
                        return null;
                    } else if (o instanceof Node) {
                        return (Node) o;
                    } else {
                        Map<String, Object> m = (Map<String, Object>) o;
                        if (nm.getName().equals("typedef")) {
                            return MockCloud.this.getNodeManager(Casting.toString(m.get("name")));
                        } else {
                            return MockCloud.this.getNode(Casting.toInt(m.get("number")));
                        }
                    }
                }
            };
        } else {
            List<Map<String, Object>> result = queryHandler.getRecords(query);
            NodeManager nm = new AbstractNodeManager(this) {
                    @Override
                    protected Map<String, Field> getFieldTypes() {
                        return Fields.getFieldTypes(query, this);
                    }
                };
            return new SimpleNodeList(result, nm);

        }
    }


    @Override
    protected Transaction newTransaction(String name) {
        return new MockTransaction(name, this);
    }

    @Override
    public String toString() {
        return "MockCloud:" + getName() + "#" + hashCode() + "@" + cloudContext;
    }

    @Override
    public RelationManager getRelationManager(NodeManager sourceManager, NodeManager destinationManager, String roleName) throws NotFoundException {
        return new MockRelationManager(this, roleName, sourceManager.getName(), destinationManager.getName());
    }


    @Override
    public RelationManager getRelationManager(String roleName) throws NotFoundException {
        return new MockRelationManager(this, roleName, "object", "object");
    }




}

