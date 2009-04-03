package nl.didactor.education.builders;
import org.mmbase.module.core.*;
import java.util.Vector;

/**
 * This builder class can score the answer given to a Value 
 * question.
 */
public class ValueQuestionBuilder extends QuestionBuilder {

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
     * Calculate the score for the given answer to a Value question. This score is
     * the sum of all the chosen value-answers.
     * TODO: verify that the given valueanswers are valid for this questionNode.
     */
    private int calculateScore(MMObjectNode questionNode, MMObjectNode givenAnswer) {
        // TODO: implement this to sum over all the chosen answers.
        Vector answers = givenAnswer.getRelatedNodes("valueanswers");
        int score = 0;
        for (int i=0; i<answers.size(); i++) {
            MMObjectNode node = (MMObjectNode)answers.get(i);
            score += node.getIntValue("value");
        }
        return score;
    }
}
