package nl.didactor.education.builders;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;

public abstract class QuestionBuilder extends MMObjectBuilder {

    public abstract int getScore(MMObjectNode questionNode, MMObjectNode givenAnswer);
}
