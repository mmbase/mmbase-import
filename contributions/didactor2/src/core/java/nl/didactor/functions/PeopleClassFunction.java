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
 * @version $Id: PeopleClassFunction.java,v 1.2 2007-07-11 12:42:02 michiel Exp $
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
        RelationStep step = query.addRelationStep(cloud.getNodeManager("educations"), null, null);
        Queries.addConstraint(query, query.createConstraint(query.createStepField(step.getNext(),"number"), education.getNumber()));
        NodeList foundClasses = classes.getList(query);
        Node claz;
        if (foundClasses.size() > 1) {
            log.warn("more classes related!");
            claz = foundClasses.getNode(0);
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
