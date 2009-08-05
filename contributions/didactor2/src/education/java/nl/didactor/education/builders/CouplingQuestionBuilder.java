package nl.didactor.education.builders;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.RelDef;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import java.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This builder class can score the answer given to a Coupling
 * question.
 * @version $Id: CouplingQuestionBuilder.java,v 1.3 2008-06-20 12:43:45 michiel Exp $
 */
public class CouplingQuestionBuilder extends QuestionBuilder {

    private static final Logger log = Logging.getLoggerInstance(CouplingQuestionBuilder.class);
    /**
     * Get the score for the given answer to a question.
     */
    public int getScore(MMObjectNode questionNode, MMObjectNode givenAnswer) {
        int score = givenAnswer.getIntValue("score");
        if (score != -1) {
            return score;
        }
        score = calculateScore(questionNode, givenAnswer);
        givenAnswer.setValue("score", score);
        givenAnswer.commit();
        return score;
    }

    /**
     * Calculate the score for the given answer to a Coupling question. Iterate over
     * all related 'couplinganswers' nodes with both the 'leftanswer' and 'rightanswer'
     * role. If these are the same (same order), it is correct.
     */
    private int calculateScore(MMObjectNode questionNode, MMObjectNode givenAnswer) {
        ClusterBuilder cb = mmb.getClusterBuilder();
        RelDef rdef = mmb.getRelDef();
        BasicStep bns;
        BasicRelationStep bsr;
        BasicStep nodeStep;
        BasicStepField posfield;


        // Builde the query for the left answers
        int leftanswer = rdef.getNumberByName("leftanswer");
        BasicSearchQuery leftQuery = new BasicSearchQuery();
        bns = leftQuery.addStep(mmb.getBuilder("givenanswers"));
        bsr = leftQuery.addRelationStep(rdef.getBuilder(leftanswer), mmb.getBuilder("couplinganswers"));
        bsr.setRole(new Integer(leftanswer));
        nodeStep = (BasicStep) leftQuery.getSteps().get(0);
        nodeStep.addNode(givenAnswer.getNumber());
        //leftQuery.addFields(bsr);
        posfield = leftQuery.addField(bsr, rdef.getBuilder(leftanswer).getField("pos"));
        leftQuery.addSortOrder(posfield);
        for (int i=0; i<leftQuery.getSteps().size(); i++) {
            BasicStep bs = (BasicStep)leftQuery.getSteps().get(i);
            leftQuery.addFields(bs);
        }

        // Build the query for the right answers
        int rightanswer = rdef.getNumberByName("rightanswer");
        BasicSearchQuery rightQuery = new BasicSearchQuery();
        bns = rightQuery.addStep(mmb.getBuilder("givenanswers"));
        bsr = rightQuery.addRelationStep(rdef.getBuilder(rightanswer), mmb.getBuilder("couplinganswers"));
        bsr.setRole(new Integer(rightanswer));
        nodeStep = (BasicStep) rightQuery.getSteps().get(0);
        nodeStep.addNode(givenAnswer.getNumber());
        //rightQuery.addFields(bsr);
        posfield = rightQuery.addField(bsr, rdef.getBuilder(rightanswer).getField("pos"));
        rightQuery.addSortOrder(posfield);
        for (int i=0; i<rightQuery.getSteps().size(); i++) {
            BasicStep bs = (BasicStep)rightQuery.getSteps().get(i);
            rightQuery.addFields(bs);
        }

        try {
            List leftAnswers = cb.getClusterNodes(leftQuery);
            List rightAnswers = cb.getClusterNodes(rightQuery);

            // There should be as much left answers as right answers
            if (leftAnswers.size() != rightAnswers.size())
                return 0;

            // All the leftanswers and rightanswers should point to the same objects
            for (int i=0; i<leftAnswers.size(); i++) {
                MMObjectNode leftNode = (MMObjectNode)leftAnswers.get(i);
                MMObjectNode rightNode = (MMObjectNode)rightAnswers.get(i);
                if (leftNode.getIntValue("posrel.pos") != rightNode.getIntValue("posrel.pos"))
                    return 0;
                if (leftNode.getIntValue("posrel.pos") == -1)
                    return 0;
                if (leftNode.getIntValue("couplinganswers.number") != rightNode.getIntValue("couplinganswers.number"))
                    return 0;
                if (leftNode.getIntValue("couplinganswers.number") == -1)
                    return 0;
            }

            // All the possible answers must be given
            Vector allAnswers = questionNode.getRelatedNodes("couplinganswers");
            if (allAnswers.size() != leftAnswers.size())
                return 0;

            for (int i=0; i<allAnswers.size(); i++) {
                boolean found = false;
                MMObjectNode realAnswer = (MMObjectNode)allAnswers.get(i);
                int realNumber = realAnswer.getIntValue("number");
                for (int j=0; j<leftAnswers.size(); j++) {
                    MMObjectNode leftAnswer = (MMObjectNode)leftAnswers.get(j);
                    if (realAnswer.getIntValue("number") == realNumber) {
                        found = true;
                        break;
                    }
                }
                if (!found)
                    return 0;
            }

            // All tests OK: this is a correct answer
            return 1;

        } catch (Throwable e) { // only needed in mmbase 1.8
            log.error(e.getMessage(), e);
        }
        return 1;
    }
}
