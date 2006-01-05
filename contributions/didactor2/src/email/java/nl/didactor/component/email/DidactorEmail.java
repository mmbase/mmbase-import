package nl.didactor.component.email;

import nl.didactor.builders.DidactorBuilder;
import nl.didactor.component.Component;
import nl.didactor.component.core.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.RelationStep;
import org.mmbase.storage.search.SearchQueryException;
import org.mmbase.storage.search.implementation.NodeSearchQuery;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class DidactorEmail extends Component {
    /**
     * Returns the version of the component
     */
    public String getVersion() {
        return "2.0";
    }

    public void init() {
        MMBase mmbase = MMBase.getMMBase();
        DidactorBuilder people = (DidactorBuilder)mmbase.getBuilder("people");
        people.registerPostInsertComponent(this, 10);
        people.registerPreDeleteComponent(this, 10);
    }

    public void install() {
        MMBase mmbase = MMBase.getMMBase();
        DidactorBuilder people = (DidactorBuilder)mmbase.getBuilder("people");
        try {
            List nodes = people.getNodes(new NodeSearchQuery(people));
            for (int i=0; i<nodes.size(); i++) {
                postInsert((MMObjectNode)nodes.get(i));
            }
        } catch (SearchQueryException e) {
        }
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
        } else { 
            throw new IllegalArgumentException("Unknown setting '" + setting + "'");
        }
    }

    /**
     * This method is called when a new object is added to Didactor. If the component
     * needs to insert objects for this object, it can do so. 
     */
    public boolean postInsert(MMObjectNode node) {
        if (node.getBuilder().getTableName().equals("people")) {
            return createUser(node);
        }

        return true;
    }

    public boolean preDelete(MMObjectNode node) {
        if (node.getBuilder().getTableName().equals("people")) {
            return deleteUser(node);
        }
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

        mailbox = mailboxes.getNewNode(username);
        mailbox.setValue("type", 11);
        mailbox.setValue("name", "Drafts");
        mailboxes.insert(username, mailbox);
        relation = insrel.getNewNode(username);
        relation.setValue("snumber", user.getNumber());
        relation.setValue("dnumber", mailbox.getNumber());
        relation.setValue("rnumber", related);
        insrel.insert(username, relation);

        return true;
    }

    /**
     * Delete the mailboxes and all emails (with related attachments) of this user.
     */
    private boolean deleteUser(MMObjectNode user) {
        Vector mailboxes = user.getRelatedNodes("mailboxes", "related", RelationStep.DIRECTIONS_DESTINATION);

        // Iterate the mailboxes, to remove them all
        for (int i=0; i<mailboxes.size(); i++) {
            MMObjectNode mailbox = (MMObjectNode)mailboxes.get(i);
            Vector emails = mailbox.getRelatedNodes("emails", "related", RelationStep.DIRECTIONS_DESTINATION);

            // Iterate the contacts, to remove them all
            for (int j=0; j<emails.size(); j++) {
                MMObjectNode email = (MMObjectNode)emails.get(j);
                Vector attachments = email.getRelatedNodes("attachments", "related", RelationStep.DIRECTIONS_DESTINATION);

                // Iterate the attachments, to remove them all
                for (int k=0; k<attachments.size(); k++) {
                    MMObjectNode attachment = (MMObjectNode)attachments.get(k);
                    attachment.getBuilder().removeRelations(attachment);
                    attachment.getBuilder().removeNode(attachment);
                }
                email.getBuilder().removeRelations(email);
                email.getBuilder().removeNode(email);
            }

            // Mail-rules are relations from mailbox to mailbox: they are
            // automatically removed here
            mailbox.getBuilder().removeRelations(mailbox);
            mailbox.getBuilder().removeNode(mailbox);
        }
        return true;
    }
}
