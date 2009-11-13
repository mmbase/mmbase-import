package org.mmbase.util.functions;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
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
 *   &lt;mm:nodelistfunction referids="max" name="latest"&gt;
 *    -- &lt;mm:field name="number" /&gt;&lt;br /&gt;
 *   &lt/mm:nodelistfunction&gt;
 * &lt;/mm:listnodes&gt;
 * </pre>
 * </code>
 *
 * This is done in the MyNews examples (on the news builder), and example JSP's can be found on /mmexamples/taglib/functions.jsp.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @see   ExampleBean For examples on hot to add functions to a builder without extending it.
 * @since MMBase-1.7
 */
public class ExampleBuilder extends MMObjectBuilder { 
    private static final Logger log = Logging.getLoggerInstance(ExampleBuilder.class);


    /**
     * Parameter constant for use bij the 'latest' function. This constant must be protected,
     * otherwise it is pickup up by the automatich function detection.
     */
    protected final static Parameter[] LISTLATEST_PARAMETERS = {
        new Parameter<Integer>("max", Integer.class, 10), /* name, type, default value */
        new Parameter<Cloud>(Parameter.CLOUD, true)                  /* true: required! */
    };

    protected final static Parameter[] SUMFIELDS_PARAMETERS = {
        new Parameter("fields", List.class, Arrays.asList(new String[] {"otype", "number"})) /* name, type, default value */
    };

    /**
     * Implementation of 'builder function', which can be compared with a static method in java.
     */
    protected final Function<NodeList> listLatestFunction = new AbstractFunction<NodeList>("latest", LISTLATEST_PARAMETERS) {
            {
                setDescription("This (rather silly) function returns the latest instances of this builder.");
            }
            public NodeList getFunctionValue(Parameters parameters) {
                Integer max = (Integer) parameters.get("max");
                Cloud cloud = parameters.get(Parameter.CLOUD);
                NodeManager thisManager = cloud.getNodeManager(getTableName());
                NodeQuery q = thisManager.createQuery();
                q.setMaxNumber(max.intValue());
                q.addSortOrder(q.getStepField(thisManager.getField("number")), SortOrder.ORDER_DESCENDING);
                return thisManager.getList(q);
            }
    };
    {
        // functions must be registered.
        addFunction(listLatestFunction);
    }

    /**
     * Implementation of 'node function', which can be compared with a instance method in java.
     */
    protected final Function<Integer> sumFieldsFunction = new NodeFunction<Integer>("sumfields", SUMFIELDS_PARAMETERS) {
            {
                setDescription("This (rather silly) function returns the sum of the given fields of a certain node");
            }
            public Integer getFunctionValue(Node node, Parameters parameters) {
                List fields = (List) parameters.get("fields");
                int result = 0;
                Iterator i = fields.iterator();
                while (i.hasNext()) {
                    result += node.getIntValue((String)i.next());
                }
                return result;
            }
    };
    {
        // node-function are registered in the same way.
        addFunction(sumFieldsFunction);
    }


    {

        // you can of course even implement it anonymously.
        addFunction(new AbstractFunction<List<String>>("showparameter",
                                         new Parameter<Collection>("collectionparam", Collection.class),
                                         new Parameter<Map>("mapparam", Map.class),
                                         new Parameter<Integer>("integerparam", Integer.class),
                                         new Parameter<Number>("numberparam", Number.class)
                                         ) {
                {
                    setDescription("With this function one can demonstrate how to create parameters of several types, and in what excactly that results");
                }
                public List<String> getFunctionValue(Parameters parameters) {
                    List<String>  result = new ArrayList<String>();
                    Parameter[] def = parameters.getDefinition();
                    for (int i = 0 ; i < def.length; i++) {
                        Object value = parameters.get(i);
                        if(value != null) {
                            result.add(def[i].toString() + " ->" + value.getClass().getName() + " " + value);
                        }
                    }
                    return result;
                }
            });
    }

    {
        // an example of a function which is both node-function and builder-function
        // it also demonstrate that you may use bridge to implement a node-function.

        addFunction(new NodeFunction<Node>("predecessor", Parameter.CLOUD // makes it possible to implement by bridge.
                                           ) {
                        {
                    setDescription("Returns the node older then the current node, or null if this node is the oldest (if called on node), or the newest node of this type, or null of there are no nodes of this type (if called on builder)");
                }
                protected Node getFunctionValue(Node node, Parameters parameters) {
                    NodeManager nm = node.getNodeManager();
                    NodeQuery q = nm.createQuery();
                    StepField field = q.getStepField(nm.getField("number"));
                    q.setConstraint(q.createConstraint(field, FieldCompareConstraint.LESS, Integer.valueOf(node.getNumber())));
                    q.addSortOrder(field, SortOrder.ORDER_DESCENDING);
                    q.setMaxNumber(1);
                    NodeIterator i = nm.getList(q).nodeIterator();
                    return i.hasNext() ? i.nextNode() : null;
                }

                public Node getFunctionValue(Parameters parameters) {
                    Cloud cloud = parameters.get(Parameter.CLOUD);
                    NodeManager nm = cloud.getNodeManager(ExampleBuilder.this.getTableName());
                    NodeQuery q = nm.createQuery();
                    StepField field = q.getStepField(nm.getField("number"));
                    q.addSortOrder(field, SortOrder.ORDER_DESCENDING);
                    q.setMaxNumber(1);
                    NodeIterator i = nm.getList(q).nodeIterator();
                    return i.hasNext() ? i.nextNode() : null;
                }
            });
    }

}
