package nl.didactor.component.assessment;

import org.mmbase.util.Casting;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.util.logging.*;


/**
 * Implementation of 'virtual' assessment field of learnblocks. The field is true if the relation
 * with the assessment.component exists.
 */
public class AssessmentField {
    private static final Logger log = Logging.getLoggerInstance(AssessmentField.class);


    protected static Node getNode(Node node) {
        return node.getCloud().getNode("component.assessment");
    }

    public static NodeQuery getRelationsQuery(Node node) {
        NodeQuery nq = Queries.createRelationNodesQuery(node, node.getCloud().getNodeManager("components"),  "related", "destination");
        nq.addNode(nq.getSteps().get(2), getNode(node));
        return nq;
    }


    public static class Get implements org.mmbase.datatypes.processors.Processor {
        public Object process(Node node, Field field, Object value) {
            Object result = Casting.toType(value == null ? Boolean.class : value.getClass(), Queries.count(getRelationsQuery(node)) != 0);
            log.debug("Processing " + value + " -> " + result);
            return result;
        }
    }

    public static class Set implements org.mmbase.datatypes.processors.Processor {
        public Object process(Node node, Field field, Object value) {
            NodeQuery q = getRelationsQuery(node);
            NodeList nl = q.getNodeManager().getList(q);
            if (value == null) value = false;
            boolean relate = Casting.toBoolean(value);
            if (relate) {
                if (nl.size() == 0) {
                    node.createRelation(getNode(node), node.getCloud().getRelationManager("learnblocks", "components", "related"));
                }
                return Casting.toType(value.getClass(), Boolean.TRUE);
            } else {
                for (Node n : nl) {
                    n.delete(true);
                }
                return Casting.toType(value.getClass(), Boolean.FALSE);
            }
        }
    }
}
