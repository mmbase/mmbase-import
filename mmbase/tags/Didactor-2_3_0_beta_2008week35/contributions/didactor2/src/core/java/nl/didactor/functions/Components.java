package nl.didactor.functions;

import nl.didactor.component.Component;
import org.mmbase.bridge.*;

import org.mmbase.bridge.util.Queries;
import org.mmbase.util.logging.*;
import java.util.*;

/**
 * Returns the componetnbs related to an object.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Components.java,v 1.2 2008-08-01 15:58:44 michiel Exp $
 */
public class Components {
    protected final static Logger log = Logging.getLoggerInstance(Components.class);

    private Node node;

    public void setNode(Node n) {
        node = n;
    }
    private String bar;
    public void setBar(String b) {
        bar = b;
    }


    public Collection<Component> components() {
        SortedMap<Integer, Component> map = new TreeMap<Integer, Component>();
        Cloud cloud = node.getCloud();
        NodeManager components = cloud.getNodeManager("components");
        NodeQuery query = Queries.createRelatedNodesQuery(node, components, "settingrel", "destination");
        NodeIterator ne = components.getList(query).nodeIterator();
        while (ne.hasNext()) {
            Node component = ne.nextNode();
            String name = component.getStringValue("name");
            Component c = Component.getComponent(name);
            if (c == null) {
                log.warn("No such component '" + name + "'");
                continue;
            }
            if (bar != null && ! bar.equals(c.getTemplateBar())) {
                log.debug("Found component for differnent bar");
                continue;
            }
            int pos = c.getBarPosition();
            while (map.containsKey(pos)) {
                pos++;
            }
            map.put(pos, c);
        }
        return map.values();
    }


}
