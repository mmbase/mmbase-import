package org.mmbase.util.functions;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.storage.search.SortOrder;
import org.mmbase.util.logging.*;

/**
 * Example builder implementation implementing functions. Lots of people are sooner or earlier
 * trying to make their own builder implementation. Especially whith the advent the 'function' tags in
 * 1.7 it would be nice that people could seen an example of how that could be done.
 *
 * To try it out, take a builder xml and add
 * <code>&lt;classfile&gt;org.mmbase.util.functions.ExampleBuilder&lt;/classfile&gt; </code>
 * and e.g. a jsp like this:
 * <code>
 * <pre>
 * &lt;mm:listnodes type="pools" max="1"&gt;
 *  &lt; mm:import id="max"&gt;100&lt;/mm:import&gt;
 *   &lt;mm:nodelistfunction referids="max" name="function1"&gt;
 *    -- &lt;mm:field name="number" /&gt;&lt;br /&gt;
 *   &lt/mm:nodelistfunction&gt;
 * &lt;/mm:listnodes&gt;
 * </pre>
 * </code>
 *
 * @author Michiel Meeuwissen
 * @version $Id: ExampleBuilder.java,v 1.4 2004-12-06 15:25:19 pierre Exp $
 * @since MMBase-1.7
 */
public final class ExampleBuilder extends MMObjectBuilder { // final to avoid that people actually use this to extend their stuff from or so.
    private static final Logger log = Logging.getLoggerInstance(ExampleBuilder.class);

    public final static Parameter[] FUNCTION1_PARAMETERS = {
        new Parameter("max", Integer.class, new Integer(10)), /* name, type, default value */
        new Parameter(Parameter.CLOUD, true)                  /* true: required! */
    };

    /**
     * A 'function' implementation which ignores the 'node' and does something with a 'Cloud' object.
     * @todo this might be interpreted as a function on the builder, somehow!
     */
    private NodeList function1Implementation(MMObjectNode node, Parameters p) {
        Integer max = (Integer) p.get("max");
        Cloud cloud = (Cloud) p.get(Parameter.CLOUD);
        // Node n = cloud.getNode(node.getNumber());
        NodeManager thisManager = cloud.getNodeManager(getTableName());
        NodeQuery q = thisManager.createQuery();
        q.setMaxNumber(max.intValue());
        q.addSortOrder(q.getStepField(thisManager.getField("number")), SortOrder.ORDER_DESCENDING);
        return thisManager.getList(q);

    }

    // overridden from MMObjectBuilder
    protected Object executeFunction(MMObjectNode node, String function, List args) {
        if (log.isDebugEnabled()) {
            log.trace("executefunction of example builder " + function + " " + args);
        }
        if (function.equals("info")) {
            List empty = new ArrayList();
            Map info = (Map) super.executeFunction(node, function, empty);
            info.put("function1",     "" + FUNCTION1_PARAMETERS + " Example function returning a node-list (bridge implementation) (newest 'max' nodes, ignoring the node)");
            if (args == null || args.size() == 0) {
                return info;
            } else {
                return info.get(args.get(0));
            }
        } else if (function.equals("function1")) {
            Parameters p = Functions.buildParameters(FUNCTION1_PARAMETERS, args);
            return function1Implementation(node, p);

            // more examples should be implemented here.
        } else {
            return super.executeFunction(node, function, args);
        }
    }


}
