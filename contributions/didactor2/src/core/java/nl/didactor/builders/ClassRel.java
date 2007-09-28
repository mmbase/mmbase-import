package nl.didactor.builders;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import java.util.Date;
import java.util.List;

/**
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class ClassRel extends DidactorRel {
    private static final Logger log = Logging.getLoggerInstance(ClassRel.class);

    /**
     * Initialize the builder
     * @return Boolean indication whether or not the call succeeded.
     */
    public boolean init() {
        return super.init();
    }

    /**
     * Set default values for new nodes. In our case, set the 'logincount' to '0' instead of '-1'
     * @param mmObjectNode The node to set the new value of
     */
    public void setDefaults(MMObjectNode mmObjectNode) {
        super.setDefaults(mmObjectNode);
        mmObjectNode.setValue("logincount", 0);
    }

    /**
     * Remove a node from the cloud.
     * Removing a 'classrel' means that the copybook that is related to this node
     * has to be removed too.
     */
    public void removeNode(MMObjectNode node) {
        List copybooks = node.getRelatedNodes("copybooks");
        for (int i=0; i<copybooks.size(); i++) {
            MMObjectNode copybook = (MMObjectNode)copybooks.get(i);
            List madetests = copybook.getRelatedNodes("madetests");
            for (int j=0; j<madetests.size(); j++) {
                MMObjectNode madetest = (MMObjectNode)madetests.get(j);
                List givenanswers = madetest.getRelatedNodes("givenanswers");
                for (int k=0; k<givenanswers.size(); k++) {
                    MMObjectNode givenanswer = (MMObjectNode)givenanswers.get(k);
                    givenanswer.removeRelations();
                    givenanswer.getBuilder().removeNode(givenanswer);
                }
                madetest.removeRelations();
                madetest.getBuilder().removeNode(madetest);
            }
            copybook.removeRelations();
            copybook.getBuilder().removeNode(copybook);
        }
        super.removeNode(node);
    }
}
