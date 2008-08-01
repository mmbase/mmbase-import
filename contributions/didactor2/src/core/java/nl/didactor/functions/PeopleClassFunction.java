package nl.didactor.functions;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.util.logging.*;
import java.util.*;
import java.lang.reflect.*;

/**
 * Some didactor specific Node functions (implemented as 'bean')
 * @author Michiel Meeuwissen
 * @version $Id: PeopleClassFunction.java,v 1.6 2008-08-01 15:59:23 michiel Exp $
 */
public class PeopleClassFunction {
    protected final static Logger log = Logging.getLoggerInstance(PeopleClassFunction.class);

    private Node node;

    public void setNode(Node n) {
        node = n;
    }

    private int e;

    public void setEducation(int e) {
        this.e = e;
    }


    public Node peopleClass() {
        Cloud cloud = node.getCloud();
        Node education = cloud.getNode(e);
        NodeManager classes = cloud.getNodeManager("classes");
        NodeQuery query = Queries.createRelatedNodesQuery(node, classes, null, null);
        query.addSortOrder(query.createStepField((Step) query.getSteps().get(2), "number"), SortOrder.ORDER_DESCENDING);
        RelationStep step = query.addRelationStep(cloud.getNodeManager("educations"), null, null);
        Queries.addConstraint(query, query.createConstraint(query.createStepField(step.getNext(),"number"), education.getNumber()));
        NodeList foundClasses = classes.getList(query);
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


    public Set<Node> blockedLearnBlocks() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException{
        // A user can have access to only "opened" top learnblocks (lession)
        try {
            Class classLessonChecker = Class.forName("nl.didactor.component.assessment.education_menu.utils.LessonChecker");
            Method method = classLessonChecker.getMethod("getBlockedLearnblocksForThisUser", Node.class, Node.class);
            return (Set<Node>) method.invoke(null, node.getCloud().getNode(e), node);
        } catch (ClassNotFoundException cnfe) {
            log.debug(cnfe);
            // if assessment not installed, then no learnblocks are blocked.
            return new HashSet<Node>();
        }
    }



}
