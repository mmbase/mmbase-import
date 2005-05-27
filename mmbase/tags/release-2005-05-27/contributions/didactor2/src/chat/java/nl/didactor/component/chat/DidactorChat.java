package nl.didactor.component.chat;
import nl.didactor.component.Component;
import nl.didactor.component.core.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
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
        return "DidactorChat";
    }

    /**
     * Returns an array of components this component depends on.
     */
    public Component[] dependsOn() {
        Component[] components = new Component[0];
        return components;
    }

    /**
     * Permission framework: indicate whether or not a given operation may be done, with the
     * given arguments. The return value is a list of 2 booleans; the first boolean indicates
     * whether or not the operation is allowed, the second boolean indicates whether or not
     * this result may be cached.
     */
    public boolean[] may (String operation, Cloud cloud, Map context, String[] arguments) {
        return new boolean[]{true, true};
    }

    public String getSetting(String setting, Cloud cloud, Map context, String[] arguments) {
        throw new IllegalArgumentException("Unknown setting '" + setting + "'");
    }

    /**
     * This method is called when a new object is added to Didactor. If the component
     * needs to insert objects for this object, it can do so. 
     */
    public boolean notifyCreate(MMObjectNode node) {
        if (node.getBuilder().getTableName().equals("classes"))
            return createClass(node);

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
