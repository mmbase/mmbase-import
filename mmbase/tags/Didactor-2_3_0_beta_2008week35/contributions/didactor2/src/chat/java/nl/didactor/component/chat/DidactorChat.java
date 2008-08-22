package nl.didactor.component.chat;
import nl.didactor.builders.DidactorBuilder;
import nl.didactor.component.Component;
import nl.didactor.component.core.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.SearchQueryException;
import org.mmbase.storage.search.implementation.NodeSearchQuery;
import java.util.List;
import java.util.Map;

public class DidactorChat extends Component {
    /**
     * Returns the version of the component
     */
    public String getVersion() {
        return "2.0";
    } /**
     * Returns the name of the component
     */
    public String getName() {
        return "chat";
    }

    /**
     * Returns an array of components this component depends on.
     */
    public Component[] dependsOn() {
        Component[] components = new Component[0];
        return components;
    }

    public void init() {
        super.init();
        MMBase mmbase = MMBase.getMMBase();
        DidactorBuilder classes = (DidactorBuilder)mmbase.getBuilder("classes");
        classes.registerPostInsertComponent(this, 10);
    }

    public void install() {
        MMBase mmbase = MMBase.getMMBase();
        DidactorBuilder classes = (DidactorBuilder)mmbase.getBuilder("classes");
        try {
            List nodes = classes.getNodes(new NodeSearchQuery(classes));
            for (int i=0; i<nodes.size(); i++) {
                postInsert((MMObjectNode)nodes.get(i));
            }
        } catch (SearchQueryException e) {
        }
    }


    /**
     * This method is called when a new object is added to Didactor. If the component
     * needs to insert objects for this object, it can do so. 
     */
    public boolean postCommit(MMObjectNode node) {
        if (node.getBuilder().getTableName().equals("classes")) {
            return createClass(node);
        }

        return true;
    }

    /**
     * Create a chatchannel for the class.
     */
    private boolean createClass(MMObjectNode cls) {
        MMBase mmb = cls.getBuilder().getMMBase();
        String classname = cls.getStringValue("name");
        String username = "system";
        MMObjectBuilder chatchannels = mmb.getBuilder("chatchannels");
        InsRel insrel = mmb.getInsRel();
        int related = mmb.getRelDef().getNumberByName("related");

        MMObjectNode chatchannel = chatchannels.getNewNode(username);
	String chatchannelName = classname.replaceAll(" ","-").toLowerCase();
        chatchannel.setValue("name", chatchannelName);
        chatchannels.insert(username, chatchannel);
        MMObjectNode relation = insrel.getNewNode(username);
        relation.setValue("snumber", cls.getNumber());
        relation.setValue("dnumber", chatchannel.getNumber());
        relation.setValue("rnumber", related);
        insrel.insert(username, relation);

        return true;
    }
}
