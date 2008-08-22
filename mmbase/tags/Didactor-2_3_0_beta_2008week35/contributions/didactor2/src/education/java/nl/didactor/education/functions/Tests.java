package nl.didactor.education.functions;

import org.mmbase.bridge.*;
import org.mmbase.cache.Cache;
import org.mmbase.storage.search.*;
import org.mmbase.bridge.util.*;
import org.mmbase.util.logging.*;
import java.util.*;

/**
 * Returns all tests of a certain education.
 * @author Michiel Meeuwissen
 * @version $Id: Tests.java,v 1.1 2007-10-18 14:39:30 michiel Exp $
 */
public class Tests {
    protected final static Logger log = Logging.getLoggerInstance(Tests.class);

    private Node node;

    public void setNode(Node n) {
        node = n;
    }


    /**
     */
    public List<Node> tests() {
        List<Node> result = new ArrayList<Node>();
        Cloud cloud = node.getCloud();
        NodeManager learnobjects = cloud.getNodeManager("learnobjects");
        NodeManager tests = cloud.getNodeManager("tests");
        NodeQuery q = Queries.createRelatedNodesQuery(node, learnobjects, "posrel", null);
        Queries.addSortOrders(q, "posrel.pos", "UP");
        GrowingTreeList tree = new GrowingTreeList(q, 10, learnobjects, "posrel", "destination");
        Queries.addSortOrders(tree.getTemplate(), "posrel.pos", "UP");
        NodeIterator iterator = tree.nodeIterator();
        while (iterator.hasNext()) {
            Node node = iterator.nextNode();
            NodeManager nm = node.getNodeManager();
            if (nm.equals(tests) || tests.getDescendants().contains(nm)) {
                result.add(node);
            }
        }
        return result;
    }


}
