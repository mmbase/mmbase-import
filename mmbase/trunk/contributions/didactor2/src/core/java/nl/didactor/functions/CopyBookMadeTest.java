package nl.didactor.functions;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.util.logging.*;
import java.util.*;
import java.lang.reflect.*;

/**
 * Retrieves a 'madetests' object for a certain tests and copybook objects.
 *
 * @author Michiel Meeuwissen
 * @version $Id: CopyBookMadeTest.java,v 1.2 2008-01-18 12:22:58 michiel Exp $
 */
public class CopyBookMadeTest {
    protected final static Logger log = Logging.getLoggerInstance(CopyBookMadeTest.class);

    private Node node;

    public void setNode(Node n) {
        node = n;
    }

    private Node test;

    public void setTest(Node t) {
        test = t;
    }

    private boolean clear = false;

    public void setClear(boolean c) {
        clear = c;
    }


    public Node madetest() {
        Cloud cloud = node.getCloud();
        NodeManager madeTests = cloud.getNodeManager("madetests");
        NodeQuery query = Queries.createRelatedNodesQuery(node, madeTests, "related", "destination");
        Step testStep = query.addRelationStep(cloud.getNodeManager("tests"), "related", "source").getNext();
        Queries.addConstraint(query, query.createConstraint(query.createStepField(testStep, "number"), test));

        NodeList found = madeTests.getList(query);

        if (found.size() > 0) {
            if (clear) {

                // todo....
            } else {
                return found.getNode(0);
            }
        }
        Node madeTest = madeTests.createNode();
        madeTest.commit();

        RelationManager rm = cloud.getRelationManager(node.getNodeManager(), madeTests, "related");
        log.info("" + node.getCloud().equals(cloud) + " " + madeTest.getCloud().equals(cloud) + " " + node.getCloud() + " " + madeTest.getCloud() + " " + cloud + " rm:" + rm.getCloud());
        Relation rel1 = rm.createRelation(node, madeTest);
        rel1.commit();
        RelationManager rm2 = cloud.getRelationManager(node.getNodeManager(), madeTests, "related");
        Relation rel2 = rm2.createRelation(test, madeTest);
        rel2.commit();
        return madeTest;
    }


}
