package nl.didactor.education.builders;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;

public class TestBuilder extends nl.didactor.builders.DidactorBuilder {

    /**
     * Iterate over all givenanswers and their questions. Calculate the
     * score for all givenanswers.
     */
    public int getScore(MMObjectBuilder testNode, MMObjectNode madeTest) {
        return 1;
    }
}
