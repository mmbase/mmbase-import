package nl.didactor.chat.repository;
import nl.eo.chat.repository.*;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Logger of chat channels that logs into MMBase
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class DidactorChannelLogger implements ChannelLogger {
    Channel channel;
    Node lognode;
    private Date logdate;
    Node channelNode;
    Cloud cloud;
    private Logger log = Logging.getLoggerInstance(DidactorChannelLogger.class.getName());

    /**
     * Constructor, create a new logger instance based on a channel
     */
    public DidactorChannelLogger(DidactorChannel channel) {
        this.channel = channel;
        channelNode = channel.getChatchannelsNode();
        cloud = channelNode.getCloud();
        log.debug("Created logger ...");
        lognode = getLognode();
    }

    /**
     * Log a message to MMBase.
     * TODO: make this configurable.
     */
    public void log(nl.eo.chat.repository.User user, String text) {
        Node lognode = getLognode();
        String ntext = lognode.getStringValue("text");
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String newline = "[" + df.format(new Date()) + "] <" + user.getNick() + "> " + text + "\n";
        log.debug("Chat log: " + newline);
        lognode.setStringValue("text", ntext + newline);
        lognode.commit();
    }

    public Channel getChannel() {
        return channel;
    }

    /**
     * Return a lognode for the channel log. This will create a new lognode if
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

        NodeList nl = cloud.getList("" + channelNode.getNumber(), "chatchannels,chatlogs",
                            "chatlogs.number,chatlogs.date", "chatlogs.date = '" + formatNow + "'",
                            null, null, null, true);
        if (nl.size() == 0) {
            // No lognodes found, create a new instance
            NodeManager chatlogs = cloud.getNodeManager("chatlogs");
            lognode = chatlogs.createNode();
            lognode.setStringValue("date", formatNow);
            lognode.commit();

            log.debug("Created lognode with number " + lognode.getNumber());

            RelationManager relman = cloud.getRelationManager("chatchannels", "chatlogs", "related");
            Relation rel = relman.createRelation(channelNode, lognode);
            rel.commit();
        } else if (nl.size() == 1) {
            lognode = cloud.getNode(nl.getNode(0).getIntValue("chatlogs.number"));
            return lognode;
        } else {
            log.error("There are more than 1 chatlogs for this date! Not logging anything now");
        }
        return null;
    }
}
