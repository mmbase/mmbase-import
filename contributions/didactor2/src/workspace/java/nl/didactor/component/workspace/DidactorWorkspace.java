package nl.didactor.component.workspace;
import nl.didactor.builders.DidactorBuilder;
import nl.didactor.component.Component;
import nl.didactor.component.core.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import java.util.Map;

public class DidactorWorkspace extends Component {
    /**
     * Returns the version of the component
     */
    public String getVersion() {
        return "2.0";
    }

    /**
     * Returns the name of the component
     */
    public String getName() {
        return "DidactorWorkspace";
    }

    public void init() {
        MMBase mmbase = MMBase.getMMBase();
        DidactorBuilder people = (DidactorBuilder)mmbase.getBuilder("people");
        people.registerPostInsertComponent(this, 10);
        DidactorBuilder classes = (DidactorBuilder)mmbase.getBuilder("classes");
        classes.registerPostInsertComponent(this, 10);
        DidactorBuilder workgroups = (DidactorBuilder)mmbase.getBuilder("workgroups");
        workgroups.registerPostInsertComponent(this, 10);
    }

    /**
     * Returns an array of components this component depends on.
     * This should always contain at least one component: 'DidactorCore'
     */
    public Component[] dependsOn() {
        Component[] components = new Component[0];
        return components;
    }

    /**
     * This method is called when a new object is added to Didactor. If the component
     * needs to insert objects for this object, it can do so. 
     */
    public boolean postInsert(MMObjectNode node) {
        if (node.getBuilder().getTableName().equals("people"))
            return createUser(node);

        if (node.getBuilder().getTableName().equals("classes"))
            return createClass(node);

        if (node.getBuilder().getTableName().equals("workgroups"))
            return createWorkgroup(node);

        return true;
    }

    /**
     * Create a personal workspace for the user.
     */
    private boolean createUser(MMObjectNode user) {
        MMBase mmb = user.getBuilder().getMMBase();
        String username = user.getStringValue("username");
        MMObjectBuilder workspaces = mmb.getBuilder("workspaces");
        InsRel insrel = mmb.getInsRel();
        int related = mmb.getRelDef().getNumberByName("related");

        MMObjectNode workspace = workspaces.getNewNode(username);
        workspace.setValue("name", "Persoonlijke werkruimte van " + username);
        workspaces.insert(username, workspace);
        MMObjectNode relation = insrel.getNewNode(username);
        relation.setValue("snumber", user.getNumber());
        relation.setValue("dnumber", workspace.getNumber());
        relation.setValue("rnumber", related);
        insrel.insert(username, relation);

        return true;
    }

    /**
     * Create a class workspace.
     */
    private boolean createClass(MMObjectNode cls) {
        MMBase mmb = cls.getBuilder().getMMBase();
        String username = "system";
        MMObjectBuilder workspaces = mmb.getBuilder("workspaces");
        InsRel insrel = mmb.getInsRel();
        int related = mmb.getRelDef().getNumberByName("related");

        MMObjectNode workspace = workspaces.getNewNode(username);
        workspace.setValue("name", "Werkruimte van klas '" + cls.getStringValue("name") + "'");
        workspaces.insert(username, workspace);
        MMObjectNode relation = insrel.getNewNode(username);
        relation.setValue("snumber", cls.getNumber());
        relation.setValue("dnumber", workspace.getNumber());
        relation.setValue("rnumber", related);
        insrel.insert(username, relation);

        return true;
    }

    /**
     * Create a workgroup workspace.
     */
    private boolean createWorkgroup(MMObjectNode cls) {
        MMBase mmb = cls.getBuilder().getMMBase();
        String username = "system";
        MMObjectBuilder workspaces = mmb.getBuilder("workspaces");
        InsRel insrel = mmb.getInsRel();
        int related = mmb.getRelDef().getNumberByName("related");

        MMObjectNode workspace = workspaces.getNewNode(username);
        workspace.setValue("name", "Werkruimte van werkgroep '" + cls.getStringValue("name") + "'");
        workspaces.insert(username, workspace);
        MMObjectNode relation = insrel.getNewNode(username);
        relation.setValue("snumber", cls.getNumber());
        relation.setValue("dnumber", workspace.getNumber());
        relation.setValue("rnumber", related);
        insrel.insert(username, relation);

        return true;
    }
}
