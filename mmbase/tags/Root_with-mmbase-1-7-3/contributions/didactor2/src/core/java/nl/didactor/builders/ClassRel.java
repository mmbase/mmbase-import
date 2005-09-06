package nl.didactor.builders;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.util.*;
import java.util.Date;
import java.util.Vector;

/**
 * This builder will create a copybook as soon as a new classrel
 * is created. This functionality cannot be made in the editwizards,
 * hence this code.
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class ClassRel extends InsRel {
    private static Logger log = Logging.getLoggerInstance(ClassRel.class.getName());

    /**
     * Initialize the builder
     * @return Boolean indication whether or not the call succeeded.
     */
    public boolean init() {
        return super.init();
    }

    /**
     * When inserting a new classrel, we need to add a copybook.
     * @param owner The owner of the object
     * @param node The new object
     * @return The unique number of the new object
     */
    public int insert(String owner, MMObjectNode node) {
        int number = super.insert(owner, node);
        if (number > 0) {
            // now add the copybook
            MMObjectNode copybook = MMBase.getMMBase().getBuilder("copybooks").getNewNode(owner);
            copybook.insert(owner);
            MMObjectNode relnode = MMBase.getMMBase().getInsRel().getNewNode(owner);
            int rnumber = MMBase.getMMBase().getRelDef().getNumberByName("related");
            relnode.setValue("snumber", number);
            relnode.setValue("dnumber", copybook.getNumber());
            relnode.setValue("rnumber", rnumber);
            relnode.insert(owner);
        }
        return number;
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
        Vector copybooks = node.getRelatedNodes("copybooks");
        for (int i=0; i<copybooks.size(); i++) {
            MMObjectNode copybook = (MMObjectNode)copybooks.get(i);
            Vector madetests = copybook.getRelatedNodes("madetests");
            for (int j=0; j<madetests.size(); j++) {
                MMObjectNode madetest = (MMObjectNode)madetests.get(j);
                Vector givenanswers = madetest.getRelatedNodes("givenanswers");
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
