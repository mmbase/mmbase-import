package nl.didactor;
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

