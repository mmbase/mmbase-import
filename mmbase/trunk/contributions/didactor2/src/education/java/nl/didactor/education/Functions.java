package nl.didactor.education;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.util.logging.*;
import java.util.*;

/**
 * Several functions on mmbase nodes which are used by didactor.
 * @author Michiel Meeuwissen
 * @version $Id: Functions.java,v 1.10 2009-01-07 17:45:44 michiel Exp $
 */
public class Functions {
    protected final static Logger log = Logging.getLoggerInstance(Functions.class);

    private Node node;

    public void setNode(Node n) {
        node = n;
    }


    /**
     * returns a list of nodenumbers which follows the path in the cloud up, via learnobjects and posrel. This
     * would mean that the last node in this path is the learobject directly related to the education node.
     */
    public List<Integer> path() {
        List<Integer> result = new ArrayList<Integer>();
        result.add(node.getNumber());
        NodeList parents = node.getRelatedNodes("learnobjects", "posrel", "source");
        while (parents.size() > 0) {
            if (parents.size() > 1) {
                log.warn("Node " + node.getNumber() + " has more than 1 posrel parents: " + parents);
            }
            Node parent = parents.getNode(0);
            if (result.contains(parent.getNumber())) break;
            result.add(parent.getNumber());
            parents = parent.getRelatedNodes("learnobjects", "posrel", "source");
        }
        return result;
    }


    /**
     * Returns the education associated with the current learnobject
     */
    public Node education() {
        Node parent = node;
        NodeList parents = node.getRelatedNodes("learnobjects", "posrel", "source");
        while (parents.size() > 0) {
            if (parents.size() > 1) {
                log.warn("Node " + node.getNumber() + " has more than 1 posrel parents: " + parents);
            }
            parent = parents.getNode(0);
            parents = parent.getRelatedNodes("learnobjects", "posrel", "source");
        }
        if (parent != null) {
            NodeList educations = parent.getRelatedNodes("educations", "posrel", "source");
            if (educations.size() > 0) {
                return educations.getNode(0);
            } else {
                log.warn("No education found for " + node);
                return null;
            }
        } else {
            log.warn("No parents found for " + node + " don't know the education");
            return null;
        }
    }

    /**
     * The sequence number in the tree of learnobjects in the current education.
     */

    public Integer sequence() {
        Node education = education();
        if (education != null) {
            NodeList tree = (NodeList) education.getFunctionValue("tree", null).get();
            int seq = 0;
            for (Node leaf : tree) {
                if (leaf.getNumber() == node.getNumber()) {
                    return seq;
                }
                seq++;
            }
        }
        log.warn("Sequence number not found for " + node);
        return null;
    }

    /*
    public String url() {
        List<Integer> result = new ArrayList<Integer>();
        result.add(node.getNumber());
        NodeList parents = node.getRelatedNodes("learnobjects", "posrel", "source");
        while (parents.size() > 0) {
            assert parents.size() == 1;
            Node parent = parents.getNode(0);
            if (result.contains(parent.getNumber())) break;
            result.add(parent.getNumber());
            parents = parent.getRelatedNodes("learnobjects", "posrel", "source");
        }
        return result;
    }

    */

    /**
     * Used on nodes of type 'tests'
     */
    public boolean online() {
        if(node.getBooleanValue("always_online")) return true;
        Date online = node.getDateValue("online_date");
        Date offline = node.getDateValue("offline_date");
        Date now = new Date();
        return now.after(online) && now.before(offline);
    }


    /**
     * Attach the made test object to the nearest object which' node manager has the
     * 'madetestholder' property.
     */
    public Node  madetestholder() {
        Cloud cloud = node.getCloud();
        for (int n : path()) {
            Node node = cloud.getNode(n);
            NodeManager nm = node.getNodeManager();
            if ("true".equals(nm.getProperty("madetestholder"))) {
                return node;
            }
        }
        return  null;
    }


}
