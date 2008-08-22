package nl.didactor.education.functions;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.util.logging.*;
import java.util.*;

/**
 * Determines the questions for a certains test.
 * @author Michiel Meeuwissen
 * @version $Id: Questions.java,v 1.2 2007-09-28 15:59:03 michiel Exp $
 */
public class Questions {
    protected final static Logger log = Logging.getLoggerInstance(Questions.class);

    private Node node;

    public void setNode(Node n) {
        node = n;
    }

    private int page = 0;
    /**
     * Which page of questions to return. On page -1 all questions are available.
     */
    public void setPage(int p) {
        page = p;
    }
    public int getPage() {
        return page;
    }
    /**
     * If the test specifies a 'question amount > 0 ' then that many questions are randomly
     * selected, using this seed. Especially needed when you use paging.
     */
    public long seed = System.currentTimeMillis();
    public void setSeed(long s) {
        seed = s;
    }


    /**
     * Used on nodes of type 'tests'
     */
    public List<Node> questions() {
        int questionAmount = node.getIntValue("questionamount");
        int questionsPerPage = node.getIntValue("questionsperpage");
        NodeManager questionsManager = node.getCloud().getNodeManager("questions");
        NodeQuery query = Queries.createRelatedNodesQuery(node, questionsManager, "posrel", "destination");
        if (questionAmount < 1) {
            Step posrel = (Step) query.getSteps().get(1);
            Step questions = (Step) query.getSteps().get(2);
            query.addSortOrder(query.createStepField(posrel, "pos"), SortOrder.ORDER_ASCENDING, false);
            query.addSortOrder(query.createStepField(questions, "title"), SortOrder.ORDER_ASCENDING, false);
            NodeList result = questionsManager.getList(query);
            if (questionsPerPage < 1 || result.size() < questionsPerPage || page == -1) {
                return result;
            } else {
                return result.subNodeList(page * questionsPerPage, Math.min((page + 1) * questionsPerPage, result.size()));
            }
        } else {
            NodeList nl = questionsManager.getList(query);
            List<Node> result = new ArrayList<Node>();
            Random rnd = new Random(seed);
            while (result.size() < questionAmount && result.size() < nl.size()) {
                Node next = nl.getNode((rnd.nextInt(nl.size())));
                if (! result.contains(next)) {
                    result.add(next);
                }
            }

            if (questionsPerPage < 1 || result.size() < questionsPerPage || page == -1) {
                return result;
            } else {
                return result.subList(page * questionsPerPage, Math.min((page + 1) * questionsPerPage, result.size()));
            }
        }
    }


}
