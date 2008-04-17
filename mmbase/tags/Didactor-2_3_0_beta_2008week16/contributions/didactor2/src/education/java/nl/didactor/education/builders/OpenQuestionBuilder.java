package nl.didactor.education.builders;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import java.util.Vector;

/**
 * This builder class can score the answer given to a Multiple-choice
 * question.
 */
public class OpenQuestionBuilder extends QuestionBuilder {
    /**
     * Get the score for the given answer to a question. 
     *
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
     * If there are related 'openanswers' objects, the given answer needs
     * to perfectly match one of these answers. Otherwise there is the option
     * to have the teacher score the answer, or to automatically score this
     * to '1'.
     */
    public int calculateScore(MMObjectNode questionNode, MMObjectNode givenAnswer) {
        Vector openAnswers = givenAnswer.getRelatedNodes("openanswers");
        if (openAnswers.size() == 0) {
            // TODO: get setting from MMBase whether or not the teacher should
            // be emailed
            // TODO: get setting from MMBase whether or not the teacher will 
            // evaluate the answer
            return 1;
        }

        String given = givenAnswer.getStringValue("text");
        
        for (int i=0; i<openAnswers.size(); i++) {
            String goodAnswer = ((MMObjectNode)openAnswers.get(i)).getStringValue("text");
            if (goodAnswer.equals(given)) {
                return 1;
            }
        }
        
        return 0;
    }
}

