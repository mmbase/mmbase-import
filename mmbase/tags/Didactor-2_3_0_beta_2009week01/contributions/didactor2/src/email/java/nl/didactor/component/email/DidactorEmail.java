package nl.didactor.component.email;

import nl.didactor.builders.DidactorBuilder;
import nl.didactor.component.Component;
import nl.didactor.component.core.*;
import org.mmbase.util.functions.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import org.mmbase.applications.email.MailBox;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.RelationStep;
import org.mmbase.storage.search.SearchQueryException;
import org.mmbase.storage.search.implementation.NodeSearchQuery;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import java.util.*;

public class DidactorEmail extends Component {
    private static final Logger log = Logging.getLoggerInstance(DidactorEmail.class);

    /**
     * Returns the version of the component
     */
    public String getVersion() {
        return "2.0";
    }
    private static final Parameter[] MAILBOX_PARAMS = new Parameter[] { new Parameter("create", Boolean.class, Boolean.TRUE) };

    public void init() {
        super.init();
        MMBase mmbase = MMBase.getMMBase();
        final DidactorBuilder people = (DidactorBuilder) mmbase.getBuilder("people");
        people.registerPostInsertComponent(this, 10);
        people.registerPreDeleteComponent(this, 10);
        people.addFunction(new NodeFunction/*<Node>*/("in_mailbox", MAILBOX_PARAMS, ReturnType.NODE) {
                public Node getFunctionValue(Node node, Parameters parameters) {
                    boolean create = Boolean.TRUE.equals(parameters.get("create"));
                    return DidactorEmail.this.getMailBox(node, MailBox.Type.INBOX, create);
                }
            });
        people.addFunction(new NodeFunction/*<Node>*/("trash_mailbox", MAILBOX_PARAMS, ReturnType.NODE) {
                public Node getFunctionValue(Node node, Parameters parameters) {
                    boolean create = Boolean.TRUE.equals(parameters.get("create"));
                    return DidactorEmail.this.getMailBox(node, MailBox.Type.TRASH, create);
                }
            });
        people.addFunction(new NodeFunction/*<Node>*/("drafts_mailbox", MAILBOX_PARAMS, ReturnType.NODE) {
                public Node getFunctionValue(Node node, Parameters parameters) {
                    boolean create = Boolean.TRUE.equals(parameters.get("create"));
                    return DidactorEmail.this.getMailBox(node, MailBox.Type.DRAFTS, create);
                }
            });
        people.addFunction(new NodeFunction/*<Node>*/("sent_mailbox", MAILBOX_PARAMS, ReturnType.NODE) {
                public Node getFunctionValue(Node node, Parameters parameters) {
                    boolean create = Boolean.TRUE.equals(parameters.get("create"));
                    return DidactorEmail.this.getMailBox(node, MailBox.Type.SENT, create);
                }
            });
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
        return "email";
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

    protected Node getMailBox(Node user, MailBox.Type type, boolean create) {
        NodeManager mailboxes = user.getCloud().getNodeManager("mailboxes");
        NodeQuery q = Queries.createRelatedNodesQuery(user, mailboxes, "related", "destination");
        Queries.addConstraint(q, Queries.createConstraint(q, "type", Queries.getOperator("="), Integer.valueOf(type.getValue())));
        NodeList boxes = mailboxes.getList(q);
        if (boxes.size() > 1 ) {
            log.warn("Found more than one mailbox " + type + " for user " + user);
        }
        Node mailbox;
        if (boxes.size() == 0) {
            mailbox = mailboxes.createNode();
            mailbox.setValue("type", type.getValue());
            mailbox.setValue("name", type.getName(user.getCloud().getLocale()));
            mailbox.commit();
            RelationManager rm = user.getCloud().getRelationManager("related");
            Relation r = rm.createRelation(user, mailbox);
            r.commit();
        } else {
            mailbox = boxes.getNode(0);
        }
        return mailbox;
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

        // Do not accidentely remove a user, it will cause a terrible mess.

        List mailboxes = user.getRelatedNodes("mailboxes", "related", RelationStep.DIRECTIONS_DESTINATION);

        // Iterate the mailboxes, to remove them all
        for (int i = 0; i < mailboxes.size(); i++) {
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
