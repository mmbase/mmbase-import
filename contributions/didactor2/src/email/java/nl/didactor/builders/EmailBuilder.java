package nl.didactor.builders;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.module.core.*;
import org.mmbase.module.*;
import org.mmbase.util.*;
import nl.didactor.mail.ExtendedJMSendMail;
import java.util.Date;

/**
 * This class handles objects of type 'emails'. When new emails are created,
 * it checks whether this email should be sent using the 'sendmail' module.
 *
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class EmailBuilder extends MMObjectBuilder {
    private static Logger log=Logging.getLoggerInstance(EmailBuilder.class.getName());
    private ExtendedJMSendMail sendmail;

    /**
     * Initialize the builder
     */
    public boolean init() {
        sendmail = (ExtendedJMSendMail)Module.getModule("sendmail");
        sendmail.startModule();
        return super.init();
    }

    /**
     * Test whether or not this email object should be sent using the
     * sendmail module. If so, make sure that the 'type' value is set
     * back to '0'. 
     * This method will only email nodes that are already committed to
     * the database. Otherwise it's a pain to find out which objects
     * are related to it (attachments that need to be sent with it)
     */
    public MMObjectNode preCommit(MMObjectNode node) {
        if (log.isDebugEnabled())
            log.debug("preCommit(" + node + ")");

        if (node.getNumber() == -1)
            return node;

        if (node.getIntValue("type") == 1) {

            // This goes wrong if the node is new, because that it cannot be gotten with
            // cloud.getNode yet....

            log.debug("Trying to send mail ...");
            org.mmbase.bridge.Cloud cloud = org.mmbase.bridge.LocalContext.getCloudContext().getCloud("mmbase");
            org.mmbase.bridge.Node n = cloud.getNode(node.getNumber());

            if (sendmail.sendMail(n)) {
                log.debug("Succeeded!");
                node.setValue("type", 0);
            } else {
                log.error("Cannot send mail '" + node + "'");
                // TODO: we have to notify the user that something went wrong. Ideally, a bounce message
            }
        }
        return node;
    }

    /**
     * Insert a new object into the cloud. This will trigger
     * a mail-send if the 'type' field of the node is 1.
     */
    public int insert(String owner, MMObjectNode node) {
        int nr = super.insert(owner, node);

        if (node.getIntValue("type") == 1) {
            preCommit(node);
        }

        return nr;
    }
}

