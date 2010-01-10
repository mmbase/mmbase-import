/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import org.mmbase.core.event.NodeEvent;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.functions.*;
import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.bridge.util.Queries;

/**
 * The properties builder can contain key/value pairs for any other node in MMBase.
 *
 * When using bridge, properties can be set and get using the 'nodemanager' function 'get' and 'set'
 * on this builder.
 *
 * In core, the (legacy?) methods on MMObjectNode should still work.
 *
 * Example code for bridge:
 <pre>
      &lt;mm:cloud rank="administrator"&gt;
        &lt;div&gt;
          &lt;mm:import id="key"&gt;test&lt;/mm:import&gt;
          &lt;mm:import id="value"&gt;&lt;mm:time time="now" /&gt;&lt;/mm:import&gt;
          &lt;mm:listnodes type="news" max="1"&gt;
            &lt;p&gt;TEST: &lt;mm:function nodemanager="properties" name="get" referids="_node@node,key" /&gt;&lt;/p&gt;
            &lt;p&gt;TEST: &lt;mm:function nodemanager="properties" name="set" referids="_node@node,key,value" /&gt;&lt;/p&gt;
            &lt;p&gt;TEST: &lt;mm:function nodemanager="properties" name="get" referids="_node@node,key" /&gt;&lt;/p&gt;
          &lt;/mm:listnodes&gt;
        &lt;/div&gt;
      &lt;/mm:cloud&gt;
  </pre>
 *
 * @version $Id$
 */
public class Properties extends MMObjectBuilder {

    private static final Logger log = Logging.getLoggerInstance(Properties.class);

    public String getGUIIndicator(MMObjectNode node) {
        String str = node.getStringValue("key");
        if (str.length() > 15) {
            return str.substring(0, 12) + "...";
        } else {
            return str;
        }
    }

    protected final static Parameter NODE  = new Parameter("node", Node.class, true);
    protected final static Parameter KEY   = new Parameter("key", String.class, true);
    protected final static Parameter VALUE = new Parameter("value", Object.class);
    protected final static Parameter DEFAULT = new Parameter("default", Object.class);
    protected final static Parameter[] GET_PARAMETERS = { NODE, KEY, DEFAULT };
    protected final static Parameter[] SET_PARAMETERS = { new Parameter.Wrapper(GET_PARAMETERS), VALUE };

    protected List getValueNode(Node node, String key) {
        NodeQuery q = node.getCloud().getNodeManager(Properties.this.getTableName()).createQuery();
        Queries.addConstraint(q, Queries.createConstraint(q, "parent", FieldCompareConstraint.EQUAL, node));
        Queries.addConstraint(q, Queries.createConstraint(q, "key", FieldCompareConstraint.EQUAL, key));
        return q.getNodeManager().getList(q);
    }
    protected Object getValue(List prop) {
        if (prop.size() == 0) {
            return null;
        } else if (prop.size() == 1) {
            return ((Node) prop.get(0)).getValue("value");
        } else {
            List result = new ArrayList();
            for (Iterator i = prop.iterator(); i.hasNext();) {
                Node p = (Node) i.next();
                result.add(p.getValue("value"));
            }
            return result;
        }
    }
    protected Object getValue(Node node, String key) {
        return getValue(getValueNode(node, key));

    }

    {
        addFunction(new AbstractFunction("get", GET_PARAMETERS, ReturnType.UNKNOWN) {
                public Object getFunctionValue(Parameters parameters) {
                    Object v = Properties.this.getValue((Node) parameters.get(NODE), (String) parameters.get(KEY));
                    if (v == null) return parameters.get(DEFAULT);
                    return v;
                }
            });
        addFunction(new AbstractFunction("set", SET_PARAMETERS, ReturnType.UNKNOWN) {
                public Object getFunctionValue(Parameters parameters) {
                    Node node =  (Node) parameters.get(NODE);
                    String key = (String) parameters.get(KEY);
                    List list = new ArrayList(Properties.this.getValueNode(node, key));
                    Object orgValue = getValue(list);
                    Object newValue = parameters.get(VALUE);
                    if (newValue == null) {
                        for (Iterator i = list.iterator(); i.hasNext();) {
                            Node n = (Node) i.next();
                            n.delete(true);
                        }
                    } else if (newValue instanceof Collection) {
                        Collection c = (Collection) newValue;
                        while (list.size() > c.size()) {
                            ((Node) list.remove(0)).delete(true);
                        }
                        while (list.size() < c.size()) {
                            Node p = node.getCloud().getNodeManager(Properties.this.getTableName()).createNode();
                            p.setStringValue("key", key);
                            p.setNodeValue("parent", node);
                            list.add(p);
                        }
                        int i = 0;
                        for (Iterator j = c.iterator(); j.hasNext();) {
                            Object v = j.next();
                            Node n = (Node) list.get(i++);
                            n.setValue("value", v);
                            n.commit();
                        }

                    } else {
                        while (list.size() > 1) {
                            ((Node) list.remove(0)).delete(true);
                        }
                        while (list.size() < 1) {
                            Node p = node.getCloud().getNodeManager(Properties.this.getTableName()).createNode();
                            p.setStringValue("key", key);
                            p.setNodeValue("parent", node);
                            list.add(p);
                        }
                        Node n = (Node) list.get(0);
                        n.setValue("value", newValue);
                        n.commit();
                    }
                    if (orgValue == null) return parameters.get(DEFAULT);
                    return orgValue;
                }
            });
    }

    /* (non-Javadoc)
     * @see org.mmbase.module.core.MMObjectBuilder#notify(org.mmbase.core.event.NodeEvent)
     */
    public void notify(NodeEvent event) {
        if (event.getBuilderName().equals(this.getTableName())) {
            if (log.isDebugEnabled()) {
                log.debug("nodeChanged(): Property change ! "+ event.getMachine() + " " + event.getNodeNumber() +
                          " " + event.getBuilderName() + " "+ NodeEvent.newTypeToOldType(event.getType()));
            }
            if (event.getType() == NodeEvent.TYPE_CHANGE || event.getType() == NodeEvent.TYPE_NEW ) {
                // The passed node number is node of prop node
                int parent = getNode(event.getNodeNumber()).getIntValue("parent");
                if (isNodeCached(new Integer(parent))) {
                    log.debug("nodeChanged(): Zapping node properties cache for " + parent);
                    MMObjectNode pnode = getNode(parent);
                    if (pnode != null) pnode.delPropertiesCache();
                }
            }
        }
        super.notify(event);
    }
}
