package nl.didactor.chat.repository;
import nl.eo.chat.repository.*;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Logger of private channels that logs into MMBase
 */

public class DidactorPrivateLogger implements PrivateLogger {
    DidactorUser user;
    Node lognode;
    private Date logdate;
    Node userNode;
    Cloud cloud;
    private Logger log = Logging.getLoggerInstance(DidactorPrivateLogger.class.getName());

    /**
     * Constructor, create a new logger instance based on a channel
     */
    public DidactorPrivateLogger(DidactorUser user) {
        this.user = user;
        userNode = user.getUsersNode();
        cloud = userNode.getCloud();
        log.debug("Created logger ...");
        lognode = getLognode();
    }

    /**
     * Log a message to MMBase.
     * TODO: make this configurable.
     */
    public void log(nl.eo.chat.repository.User user, nl.eo.chat.repository.User target, String text) {
        Node lognode = getLognode(); 
        String ntext = lognode.getStringValue("text");
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String newline = "[" + df.format(new Date()) + "] <" + user.getNick() + "> -> <"+target.getNick()+"> " + text + "\n";
        log.debug("Chat log: " + newline);
        lognode.setStringValue("text", ntext + newline);
        lognode.commit();
    }

    /**
     * Return a lognode for the log. This will create a new lognode if
     * there is no lognode for the current day.
     */
    public synchronized Node getLognode() {
        Date now = new Date();
        if (logdate == null) {
            logdate = now;
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");        
        String formatNow = df.format(now);
        String formatLogdate = df.format(logdate);

        if (formatNow.equals(formatLogdate)) {
            if (lognode != null)
                return lognode;
        }

        logdate = now;

        NodeList nl = cloud.getList("" + userNode.getNumber(), "people,chatlogs", 
                            "chatlogs.number,chatlogs.date", "date = '" + formatNow + "'",
                            null, null, null, true);
        if (nl.size() == 0) {
            // No lognodes found, create a new instance
            NodeManager chatlogs = cloud.getNodeManager("chatlogs");
            lognode = chatlogs.createNode();
            lognode.setStringValue("date", formatNow);
            lognode.commit();

            log.debug("Created lognode with number " + lognode.getNumber());

            RelationManager relman = cloud.getRelationManager("people", "chatlogs", "related");
            Relation rel = relman.createRelation(userNode, lognode);
            rel.commit();
        } else if (nl.size() == 1) {
            lognode = cloud.getNode(nl.getNode(0).getIntValue("chatlogs.number"));

	    log.debug("Found existing lognode with number " + lognode.getNumber());
            return lognode;
        } else {
            log.error("There are more than 1 chatlogs for this date! Not logging anything now");
        }
        return null;
    } 
}

