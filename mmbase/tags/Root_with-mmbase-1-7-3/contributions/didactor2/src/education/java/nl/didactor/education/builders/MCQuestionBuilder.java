package nl.didactor.education.builders;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import java.util.Vector;

/**
 * This builder class can score the answer given to a Multiple-choice
 * question.
 */
public class MCQuestionBuilder extends QuestionBuilder {

    /**
     * Get the score for the given answer to a question. This method only
     * delegates: if a score wsa already present in MMBase then that score is
     * returned. Otherwise either the 'GetScoreSingle' or 'GetScoreMultiple' method is
     * called.
     */
    public int getScore(MMObjectNode questionNode, MMObjectNode givenAnswer) {
        int score = givenAnswer.getIntValue("score");
        if (score != -1) {
            return score;
        }

        int type = questionNode.getIntValue("type");
        switch(type) {
            case 0:
                // only 1 answer selected
                score = getScoreSingle(questionNode, givenAnswer);            
                givenAnswer.setValue("score", score);
                givenAnswer.commit();
                return score;
            case 1:
                //  multiple answers selected
                score = getScoreMultiple(questionNode, givenAnswer);
                givenAnswer.setValue("score", score);
                givenAnswer.commit();
                return score;
            default:
                break;
        }
        return 1;
    }

    /**
     * Return true if the related 'mcanswers' object has a 'correct' field set to '1'.
     */
    private int getScoreSingle(MMObjectNode questionNode, MMObjectNode givenAnswer) {
        Vector relatedAnswers = givenAnswer.getRelatedNodes("mcanswers");
        if (relatedAnswers.size() != 1) {
            return 0;
        }

        MMObjectNode mcanswer = (MMObjectNode)relatedAnswers.get(0);
        if (mcanswer.getIntValue("correct") == 1) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Return true if all related 'mcanswers' objects have a 'correct' field set to '1',
     * and all these possible mcanswers with correct=1 have been chosen.
     */
    private int getScoreMultiple(MMObjectNode questionNode, MMObjectNode givenAnswer) {
        Vector givenAnswers = givenAnswer.getRelatedNodes("mcanswers");
        Vector goodAnswers = questionNode.getRelatedNodes("mcanswers");

        // First check if all the given answers are correct
        for (int i=0; i<givenAnswers.size(); i++) {
            if (((MMObjectNode)givenAnswers.get(i)).getIntValue("correct") != 1) {
                return 0;
            }
        }

        // Secondly check if all the correct answers are given
        for (int i=0; i<goodAnswers.size(); i++) {
            if (((MMObjectNode)goodAnswers.get(i)).getIntValue("correct") == 1) {
                if (!givenAnswers.contains(goodAnswers.get(i))) {
                    return 0;
                }
            }
        }

        // ALl tests succeeded: answer is correct
        return 1;
    }
}

