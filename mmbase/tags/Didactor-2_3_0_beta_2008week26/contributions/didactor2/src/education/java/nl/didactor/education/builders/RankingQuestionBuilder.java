package nl.didactor.education.builders;
import org.mmbase.module.core.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.module.corebuilders.RelDef;
import java.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This builder class can score the answer given to a Ranking
 * question.
 */
public class RankingQuestionBuilder extends QuestionBuilder {

    private static final Logger log = Logging.getLoggerInstance(RankingQuestionBuilder.class);
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
     * Calculate the score for the given answer to a Ranking question. Return '1'
     * if the given answers are in the same order as the defined answers. '0' otherwise.
     */
    private int calculateScore(MMObjectNode questionNode, MMObjectNode givenAnswer) {
        ClusterBuilder cb = mmb.getClusterBuilder();
        RelDef rdef = mmb.getRelDef();
        BasicStep bns;
        BasicRelationStep bsr;
        BasicStep nodeStep;
        BasicStepField posfield;
        int posrel = rdef.getNumberByName("posrel");

        // Builde the query for the given answers
        BasicSearchQuery givenQuery = new BasicSearchQuery();
        bns = givenQuery.addStep(mmb.getBuilder("givenanswers"));
        bsr = givenQuery.addRelationStep(rdef.getBuilder(posrel), mmb.getBuilder("rankinganswers"));
        bsr.setRole(new Integer(posrel));

        nodeStep = (BasicStep) givenQuery.getSteps().get(0);
        nodeStep.addNode(givenAnswer.getNumber());

        posfield = givenQuery.addField(bsr, rdef.getBuilder(posrel).getField("pos"));
        givenQuery.addSortOrder(posfield);
        for (int i=0; i<givenQuery.getSteps().size(); i++) {
            BasicStep bs = (BasicStep)givenQuery.getSteps().get(i);
            givenQuery.addFields(bs);
        }

        // Build the query for the good answers
        BasicSearchQuery goodQuery = new BasicSearchQuery();
        bns = goodQuery.addStep(mmb.getBuilder("rankingquestions"));
        bsr = goodQuery.addRelationStep(rdef.getBuilder(posrel), mmb.getBuilder("rankinganswers"));
        bsr.setRole(new Integer(posrel));

        nodeStep = (BasicStep) goodQuery.getSteps().get(0);
        nodeStep.addNode(questionNode.getNumber());

        posfield = goodQuery.addField(bsr, rdef.getBuilder(posrel).getField("pos"));
        goodQuery.addSortOrder(posfield);
        for (int i=0; i<goodQuery.getSteps().size(); i++) {
            BasicStep bs = (BasicStep)goodQuery.getSteps().get(i);
            goodQuery.addFields(bs);
        }

        try {
            List givenAnswers = cb.getClusterNodes(givenQuery);
            List goodAnswers = cb.getClusterNodes(goodQuery);

            if (givenAnswers.size() != goodAnswers.size()) {
                System.err.println("goodAnswers.size() = " + goodAnswers.size() + ", givenAnswers.size() = " + givenAnswers.size());
                return 0;
            }

            for (int i=0; i<givenAnswers.size(); i++) {
                MMObjectNode theGivenAnswer = (MMObjectNode)givenAnswers.get(i);
                MMObjectNode goodAnswer = (MMObjectNode)goodAnswers.get(i);
                if (theGivenAnswer.getIntValue("rankinganswers.number") != goodAnswer.getIntValue("rankinganswers.number")) {
                    System.err.println(" " + givenAnswers.get(i) + " != " + goodAnswers.get(i));
                    return 0;
                }
            }
        } catch (Throwable e) { // only needed in mmbase 1.8
            log.error(e.getMessage(), e);
        }

        return 1;
    }
}
