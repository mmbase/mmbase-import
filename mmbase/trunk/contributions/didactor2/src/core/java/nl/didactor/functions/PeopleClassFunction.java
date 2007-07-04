package nl.didactor.functions;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.util.logging.*;
import java.util.*;

/**
 * Some didactor specific Node functions (implemented as 'bean')
 * @author Michiel Meeuwissen
 * @version $Id: PeopleClassFunction.java,v 1.1 2007-07-04 13:57:38 michiel Exp $
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


}
