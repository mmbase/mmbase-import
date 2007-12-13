package nl.didactor.builders;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.bridge.*;
import nl.didactor.component.Component;
import org.mmbase.applications.email.MailBox;
import java.util.*;

/**
 * Email related functions for people objects.
 *
 * @author Michiel Meeuwissen
 */
public class PeopleEmailFunctions {
    private static final Logger log = Logging.getLoggerInstance(PeopleEmailFunctions.class);

    protected Node node;

    public void setNode(Node n) {
        node = n;
    }
    public Node createInbox() {
        log.info("Creating inbox for " + node);
        NodeManager manager = node.getCloud().getNodeManager("mailboxes");

        Node mailbox = manager.createNode();
        mailbox.setStringValue("name", node.getStringValue("firstname") + " " + node.getStringValue("suffix") + " " + node.getStringValue("lastname"));
        mailbox.setIntValue("type", MailBox.INBOX);
        mailbox.commit();

        RelationManager rm = node.getCloud().getRelationManager(node.getNodeManager(), manager, "related");
        Relation r = rm.createRelation(node, mailbox);
        r.commit();
        return mailbox;
    }
    public String forwardEmail() {
        Component c = Component.getComponent("email");
        Object value = c.getUserSetting("mayforward", "" + node.getNumber(), node.getCloud());
        if (Boolean.TRUE.equals(value)) {
            return node.getStringValue("email");
        } else {
            return null;
        }

    }
}

