/**
 * Component description interface.
 */
package nl.didactor.component.email;
import nl.didactor.component.Component;
import nl.didactor.component.core.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import java.util.Map;

public class DidactorEmail extends Component {
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
        return "DidactorEmail";
    }

    /**
     * Returns an array of components this component depends on.
     * This should always contain at least one component: 'DidactorCore'
     */
    public Component[] dependsOn() {
        Component[] components = new Component[1];
        components[0] = new DidactorCore();
        return components;
    }

    /**
     * Permission framework: indicate whether or not a given operation may be done, with the
     * given arguments. The return value is a list of 2 booleans; the first boolean indicates
     * whether or not the operation is allowed, the second boolean indicates whether or not
     * this result may be cached.
     */
    public boolean[] may (String operation, Cloud cloud, Map context, String[] arguments) {
        if ("maynot".equals(operation)) {
            return new boolean[] {false, false};
        } else {
            return new boolean[]{true, true};
        }
    }

    public String getSetting(String setting, Cloud cloud, Map context, String[] arguments) {
        if ("mayforward".equals(setting)) {
            return getUserSetting(setting, "" + context.get("user"), cloud, arguments);
        } else if ("bladiebla".equals(setting)) {
            return "2";
        } else { 
            throw new IllegalArgumentException("Unknown setting '" + setting + "'");
        }
    }

    /**
     * This method is called when a new object is added to Didactor. If the component
     * needs to insert objects for this object, it can do so. 
     */
    public boolean notifyCreate(MMObjectNode node) {
        if (node.getBuilder().getTableName().equals("people"))
            return createUser(node);

        return true;
    }

    /**
     * Create all the mailboxes for this user
     */
    private boolean createUser(MMObjectNode user) {
        MMBase mmb = user.getBuilder().getMMBase();
        String username = user.getStringValue("username");
        MMObjectBuilder mailboxes = mmb.getBuilder("mailboxes");
        InsRel insrel = mmb.getInsRel();
        int related = mmb.getRelDef().getNumberByName("related");

        MMObjectNode mailbox = mailboxes.getNewNode(username);
        mailbox.setValue("type", 0);
        mailbox.setValue("name", "Postvak in");
        mailboxes.insert(username, mailbox);
        MMObjectNode relation = insrel.getNewNode(username);
        relation.setValue("snumber", user.getNumber());
        relation.setValue("dnumber", mailbox.getNumber());
        relation.setValue("rnumber", related);
        insrel.insert(username, relation);

        mailbox = mailboxes.getNewNode(username);
        mailbox.setValue("type", 1);
        mailbox.setValue("name", "Verzonden items");
        mailboxes.insert(username, mailbox);
        relation = insrel.getNewNode(username);
        relation.setValue("snumber", user.getNumber());
        relation.setValue("dnumber", mailbox.getNumber());
        relation.setValue("rnumber", related);
        insrel.insert(username, relation);

        mailbox = mailboxes.getNewNode(username);
        mailbox.setValue("type", 2);
        mailbox.setValue("name", "Verwijderde items");
        mailboxes.insert(username, mailbox);
        relation = insrel.getNewNode(username);
        relation.setValue("snumber", user.getNumber());
        relation.setValue("dnumber", mailbox.getNumber());
        relation.setValue("rnumber", related);
        insrel.insert(username, relation);

        mailbox = mailboxes.getNewNode(username);
        mailbox.setValue("type", 3);
        mailbox.setValue("name", "Persoonlijke map");
        mailboxes.insert(username, mailbox);
        relation = insrel.getNewNode(username);
        relation.setValue("snumber", user.getNumber());
        relation.setValue("dnumber", mailbox.getNumber());
        relation.setValue("rnumber", related);
        insrel.insert(username, relation);

        return true;
    }
}
