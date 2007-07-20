package nl.didactor.education;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.util.logging.*;
import java.util.*;

/**
 * @author Michiel Meeuwissen
 * @version $Id: Functions.java,v 1.2 2007-07-20 09:52:21 michiel Exp $
 */
public class Functions {
    protected final static Logger log = Logging.getLoggerInstance(Functions.class);
    
    private Node node;

    public void setNode(Node n) {
        node = n;
    }


    public List<Integer> path() {
        List<Integer> result = new ArrayList<Integer>();
        result.add(node.getNumber());
        NodeList parents = node.getRelatedNodes("learnobjects", "posrel", "source");
        while (parents.size() > 0) {            
            assert parents.size() == 1;
            Node parent = parents.getNode(0);
            if (result.contains(parent.getNumber())) break;
            result.add(parent.getNumber());
            parents = parent.getRelatedNodes("learnobjects", "posrel", "source");
        }
        return result;
    }

    /**
     * Used on nodes of type 'tests'
     */
    public List<Node> questions() {
        int questionType = node.getIntValue("questionamount");
        NodeManager questionsManager = node.getCloud().getNodeManager("questions");
        NodeQuery query = Queries.createRelatedNodesQuery(node, questionsManager, "posrel", "destination");        
        if (questionType < 1) {
            Step posrel = (Step) query.getSteps().get(1);
            Step questions = (Step) query.getSteps().get(2);            
            query.addSortOrder(query.createStepField(posrel, "pos"), SortOrder.ORDER_ASCENDING, false);
            query.addSortOrder(query.createStepField(questions, "title"), SortOrder.ORDER_ASCENDING, false);
            return questionsManager.getList(query);
        } else {
            List<Node> result = new ArrayList<Node>();
            result.addAll(questionsManager.getList(query));
            Collections.shuffle(result);
            return result;
        }
    }

    /**
     * Used on nodes of type 'tests'
     */
    public boolean online() {
        if(node.getBooleanValue("always_online")) return true;
        Date online = node.getDateValue("online_date");
        Date offline = node.getDateValue("offline_date");
        Date now = new Date();
        return now.after(online) && now.before(offline);
    }
    

}
