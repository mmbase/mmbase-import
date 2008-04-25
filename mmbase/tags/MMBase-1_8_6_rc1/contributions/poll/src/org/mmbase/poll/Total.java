package org.mmbase.poll;

import org.mmbase.bridge.*;
import java.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A Node-function on the 'poll' builder, which simply calculates the total number of given answers.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Total.java,v 1.2 2007-01-02 15:40:48 michiel Exp $
 * @since MMBase-1.8
 */
public final class Total {

    private static final Logger log = Logging.getLoggerInstance(Total.class);

    private Node node;
    private boolean calculate = false;

    /**
     * Makes this bean useable as a Node function.
     */
    public void setNode(Node node) {
        this.node = node;
    }

    public void setCalculate(Boolean b) {
        calculate = b;
    }
    public boolean getCalculate() {
        return calculate;
    }


    public int total() {
        if (node == null) throw new IllegalArgumentException("total is a node-function");
        if (calculate || ! node.getNodeManager().hasField("total")) {
            int tot = 0;
            NodeList answers = node.getRelatedNodes("answer");
            NodeIterator i = answers.nodeIterator();
            while(i.hasNext()) {
                int t = i.nextNode().getIntValue("total_answers");
                if (t > 0) tot += t;
            }
            return tot;
        } else {
            return node.getIntValue("total");
        }
    }
}
