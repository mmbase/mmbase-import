package nl.didactor.functions;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.util.logging.*;
import org.mmbase.util.Casting;
import java.util.*;
import java.lang.reflect.*;
import javax.servlet.http.*;

/**
 * Some didactor specific Node functions (implemented as 'bean')
 * @author Michiel Meeuwissen
 * @version $Id: PeopleClassFunction.java,v 1.10 2008-12-04 15:25:11 michiel Exp $
 */
public class PeopleClassFunction {
    protected final static Logger log = Logging.getLoggerInstance(PeopleClassFunction.class);

    private Node node;

    public void setNode(Node n) {
        node = n;
    }

    private int e = -1;

    public void setEducation(int e) {
        this.e = e;
    }


    /**
     * Returns all classes asociated with a certain user.
     */
    public NodeList peopleClasses() {
        Cloud cloud = node.getCloud();
        if (e == -1) {
            HttpServletRequest req = (HttpServletRequest) cloud.getProperty(Cloud.PROP_REQUEST);
            e = Casting.toInt(req.getAttribute("education"));
            if (! cloud.hasNode(e)) {
                throw new IllegalStateException("No such education '" + e + "' (as found in request attribute 'education')");
            }
        } else {
            if (! cloud.hasNode(e)) {
                throw new IllegalStateException("No such education '" + e + "' (as found set with parameter)");
            }
        }
        Node education = cloud.getNode(e);
        NodeManager classes = cloud.getNodeManager("classes");
        NodeQuery query = Queries.createRelatedNodesQuery(node, classes, null, null);
        query.addSortOrder(query.createStepField((Step) query.getSteps().get(2), "number"), SortOrder.ORDER_DESCENDING);
        RelationStep step = query.addRelationStep(cloud.getNodeManager("educations"), null, null);
        Queries.addConstraint(query, query.createConstraint(query.createStepField(step.getNext(),"number"), education.getNumber()));
        NodeList foundClasses = classes.getList(query);
        log.debug("Classes " + foundClasses + " found with " + query.toSql());
        return foundClasses;
    }

    public Node peopleClass() {
        NodeList foundClasses = peopleClasses();
        Node claz;
        if (foundClasses.size() > 1) {
            log.debug("more classes related! for node " + node.getNumber());
            claz = null;
            Date now = org.mmbase.util.DynamicDate.eval("tohour");
            NodeIterator ni = foundClasses.nodeIterator();
            CLASS:
            while (ni.hasNext()) {
                claz = ni.nextNode();
                log.debug("considering " + claz);
                NodeList mmevents = claz.getRelatedNodes("mmevents");
                NodeIterator ei = mmevents.nodeIterator();
                if (ei.hasNext()) {
                    Node event = ei.nextNode();
                    if (event.getDateValue("start").before(now) && event.getDateValue("stop").after(now)) {
                        log.debug(" " + claz + " was started and not stopped so using this one");
                        break CLASS;
                    } else {
                        log.debug(event.getDateValue("start") + " is after " + now);
                    }
                } else {
                    log.debug("No mmevents coupled to " + claz);
                        // what does that mean?
                }
            }
        } else if (foundClasses.size() == 1) {
            claz = foundClasses.getNode(0);
        } else {
            claz = null;
        }
        return claz;
    }




}
