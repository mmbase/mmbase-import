/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.richtext.processors;
import org.mmbase.datatypes.processors.Processor;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import org.mmbase.module.core.*;
import org.mmbase.core.event.*;
import org.mmbase.util.logging.*;
import java.util.*;


/**
 * This (get)processor can be used on a 'index' field. If the field is actually filled, it will
 * return that value. If the field is not filled, it will use the surrounding cloud, to return the
 * logical value. If e.g. the sibling node just 'before' this node has index '4' this one has '5'. Fields
 * using the processor implement automatic numbering of chapters and such.
 *
 * @author Michiel Meeuwissen
 * @version $Id: GetIndex.java,v 1.6 2008-03-11 16:53:22 michiel Exp $
 * @since MMBase-1.8
 */

public class GetIndex implements  Processor {
    private static final Logger log = Logging.getLoggerInstance(GetIndex.class);

    public static final String CLOUDPROP_INDEXROOT = "org.mmbase.index-root";

    private static final long serialVersionUID = 1L;

    private Map<String, String> cache = Collections.synchronizedMap(new HashMap<String, String>());
    private boolean cacheValid = true;

    private String indexField = "index";
    private String role       = "index";
    private int depth = 5;

    private NodeEventListener observer = new NodeEventListener() {
            public void notify(NodeEvent ne) {
                cacheValid = false;
                GetIndex.this.cache.clear();
            }
        };


    private String getKey(final Node root, final Node node, final Field field) {
        return "" + root.getNumber() + "/" + node.getNumber() + "/" + field.getName();
    }

    private String successor(String i) {
        if (i == null) return "1";
        return "" + (Integer.parseInt(i) + 1);
    }

    private Node findRoot(final Node relation) {
        Object root = relation.getCloud().getProperty(CLOUDPROP_INDEXROOT);
        if (root != null) {
            if (root instanceof Node) {
                return (Node) root;
            } else {
                return relation.getCloud().getNode("" + root);
            }
        } else {
            // not specified, search any.
            Node n = relation.getNodeValue("snumber");
            NodeList l = n.getRelatedNodes("object", role, "source");
            while (l.size() > 0) {
                n = l.getNode(0);
                l = n.getRelatedNodes("object", role, "source");
            }
            return n;
        }
    }

    private  void fillCache(final Node root, final Node node, final Field field) {
        // make sure the cache is watched.
        MMBase mmb = MMBase.getMMBase();
        mmb.getBuilder(node.getNodeManager().getName()).addEventListener(observer);

        log.info("Filling cache, initiated by node " + node.getNumber());
        Node n = root;

        synchronized(cache) {
            // 'n' now contains the 'topmost' object.
            // now iterate down again, while determining all indices.
            String[] index = new String[depth * 2 + 1];
            GrowingTreeList tree = new GrowingTreeList(Queries.createNodeQuery(n), depth, null, role, "destination");
            TreeIterator iterator = tree.treeIterator();
            while (iterator.hasNext()) {
                n = iterator.nextNode();
                String i = (String) n.getObjectValue(indexField);
                if (i == null || i.equals("")) i = successor(index[iterator.currentDepth() - 1]);
                index[iterator.currentDepth()] = i;
                cache.put(getKey(root, n, field), i);
            }
            log.info("Filled cached " + cache);
            cacheValid = true;
        }
    }

    public Object process(Node node, Field field, Object value) {
        Node root = findRoot(node);
        log.info("Found root-node " + root.getNumber());
        String key = getKey(root, node, field);
        String result = cache.get(key);
        if (result == null) {
            fillCache(root, node, field);
            result = cache.get(key);
        }
        log.info("Found index for " + key + " " + result);
        if (result == null) return "";
        return result;
    }

}
