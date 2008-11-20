package nl.didactor.education.functions;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;
import java.util.*;

/**
 * Determines the questions for a certains test.
 * @author Michiel Meeuwissen
 * @version $Id: LearnObjectQuestions.java,v 1.1 2008-11-20 16:57:44 michiel Exp $
 */
public class LearnObjectQuestions {
    protected final static Logger log = Logging.getLoggerInstance(LearnObjectQuestions.class);

    private Node node;

    public void setNode(Node n) {
        node = n;
    }

    public List<Node> questions() {
        return new ArrayList<Node>();
    }


}
