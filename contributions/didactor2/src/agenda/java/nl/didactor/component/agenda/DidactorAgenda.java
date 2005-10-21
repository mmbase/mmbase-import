package nl.didactor.component.agenda;
import nl.didactor.component.Component;
import nl.didactor.component.core.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import java.util.Map;

public class DidactorAgenda extends Component {
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
        return "DidactorAgenda";
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
        
        if (node.getBuilder().getTableName().equals("people"))
            return createUser(node);

        if (node.getBuilder().getTableName().equals("classes"))
            return createClass(node);

        if (node.getBuilder().getTableName().equals("workgroups")) {
            return createWorkgroup(node);
        }
        
        return true;
    }

    /**
     * Create a personal agenda for the user.
     */
    private boolean createUser(MMObjectNode user) {
        MMBase mmb = user.getBuilder().getMMBase();
        String username = user.getStringValue("username");
        MMObjectBuilder agendas = mmb.getBuilder("agendas");
        InsRel insrel = mmb.getInsRel();
        int related = mmb.getRelDef().getNumberByName("related");

        MMObjectNode agenda = agendas.getNewNode(username);
        agenda.setValue("name", "Persoonlijke Agenda van " + username);
        agendas.insert(username, agenda);
        MMObjectNode relation = insrel.getNewNode(username);
        relation.setValue("snumber", user.getNumber());
        relation.setValue("dnumber", agenda.getNumber());
        relation.setValue("rnumber", related);
        insrel.insert(username, relation);

        return true;
    }

    /**
     * Create a class agenda.
     */
    private boolean createClass(MMObjectNode cls) {
        MMBase mmb = cls.getBuilder().getMMBase();
        String username = "system";
        MMObjectBuilder agendas = mmb.getBuilder("agendas");
        InsRel insrel = mmb.getInsRel();
        int related = mmb.getRelDef().getNumberByName("related");

        MMObjectNode agenda = agendas.getNewNode(username);
        agenda.setValue("name", "Agenda van klas '" + cls.getStringValue("name") + "'");
        agendas.insert(username, agenda);
        MMObjectNode relation = insrel.getNewNode(username);
        relation.setValue("snumber", cls.getNumber());
        relation.setValue("dnumber", agenda.getNumber());
        relation.setValue("rnumber", related);
        insrel.insert(username, relation);

        return true;
    }

    /**
     * Create a workgroup agenda.
     */
    private boolean createWorkgroup(MMObjectNode workgroup) {
        MMBase mmb = workgroup.getBuilder().getMMBase();
        String username = "system";
        MMObjectBuilder agendas = mmb.getBuilder("agendas");
        InsRel insrel = mmb.getInsRel();
        int related = mmb.getRelDef().getNumberByName("related");

        MMObjectNode agenda = agendas.getNewNode(username);
        agenda.setValue("name", "Agenda van werkgroep '" + workgroup.getStringValue("name") + "'");
        agendas.insert(username, agenda);
        MMObjectNode relation = insrel.getNewNode(username);
        relation.setValue("snumber", workgroup.getNumber());
        relation.setValue("dnumber", agenda.getNumber());
        relation.setValue("rnumber", related);
        insrel.insert(username, relation);

        return true;
    }
}
