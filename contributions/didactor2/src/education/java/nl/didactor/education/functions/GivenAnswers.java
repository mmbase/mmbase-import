package nl.didactor.education.functions;

import org.mmbase.bridge.*;
import org.mmbase.cache.Cache;
import org.mmbase.storage.search.*;
import org.mmbase.bridge.util.*;
import org.mmbase.util.logging.*;
import java.util.*;

/**
 * Givenanswers for a certain question.
 * @author Michiel Meeuwissen
 * @version $Id: GivenAnswers.java,v 1.1 2008-11-17 17:09:59 michiel Exp $
 */
public class GivenAnswers {
    protected final static Logger log = Logging.getLoggerInstance(GivenAnswers.class);

    private Node question;

    public void setNode(Node n) {
        question = n;
    }

    private Node copybook;

    public void setCopybook(Node n) {
        copybook = n;
    }

    private Node block;

    public void setBlock(Node n) {
        block = n;
    }


    /**
     */
    public NodeList  givenanswers() {
        Cloud cloud = question.getCloud();
        NodeManager givenanswers = cloud.getNodeManager("givenanswers");
        NodeManager madetests    = cloud.getNodeManager("madetests");
        NodeManager copybooks    = cloud.getNodeManager("copybooks");

        NodeList mt = null;
        if (block != null) {
            NodeQuery sq = Queries.createRelatedNodesQuery(block, madetests, "related", "destination");
            RelationStep srs = sq.addRelationStep(copybooks, "related", "source");
            sq.addNode(srs.getNext(), copybook.getNumber());
            mt = madetests.getList(sq);
        }



        NodeQuery q = Queries.createRelatedNodesQuery(question, givenanswers, "related", "source");
        Queries.addSortOrders(q, "insrel.number", "DOWN");

        RelationStep rs1 = q.addRelationStep(madetests, "related", "destination");
        if (mt != null) {
            for (Node n : mt) {
                q.addNode(rs1.getNext(), n.getNumber());
            }
        }


        RelationStep rs = q.addRelationStep(copybooks, "related",  "source");
        q.addNode(rs.getNext(), copybook.getNumber());

        return givenanswers.getList(q);
    }


}
