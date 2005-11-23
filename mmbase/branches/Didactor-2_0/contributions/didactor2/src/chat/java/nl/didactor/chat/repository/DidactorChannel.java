package nl.didactor.chat.repository;

import java.net.Socket;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.StringTokenizer;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.Relation;
import org.mmbase.bridge.RelationManager;

import nl.eo.chat.repository.*;
import nl.eo.chat.repository.irc.*;

/**
 * This implementation of a channel uses MMBase to retrieve and store
 * information about a channel. Extended by Johannes Verelst
 * to follow the specific objectmodel of Didactor.
 *
 * @author Jaco de Groot
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class DidactorChannel extends IrcChannel {
    Cloud cloud;
    Node chatchannelsNode;
    int lastOn = -1;
    ChannelLogger logger;
    
    /**
     * Initialize a new DidactorChannel with the given name.
     */
    protected DidactorChannel (String name, Cloud cloud, Node chatchannelsNode) {
        super(name);
        this.cloud = cloud;
        this.chatchannelsNode = chatchannelsNode;
        super.setTopicProtection(true);
        super.setModerated(false);
        super.setNoOutsideMessages(true);
        this.logger = new DidactorChannelLogger(this);

        super.setTopic(chatchannelsNode.getStringValue("topic"));
    }

    /**
     * Check if a given user is operator on the channel. Administrators are 
     * operator by default, teachers are operator only if they are directly
     * related to the education.
     * TODO: rewrite to use a new 'operator' role
     */
    public boolean isOperator(User user) {
        Node n = cloud.getNode(((DidactorUser)user).getUsersNode().getNumber());

        return false;
    }

    public void addUser(User user) {
        DidactorUser mmbaseUser = (DidactorUser)user;
        if (mmbaseUser.isOperator()) {
            operators.add(user);
        }
        super.addUser(user);
    }

    public void addBan(String ban) {
        super.addBan(ban);
        saveBanList();
    }

    public void removeBan(String ban) {
        super.removeBan(ban);
        saveBanList();
    }

    private void saveBanList() {
        StringBuffer sb = new StringBuffer();
        Iterator iterator = bans.iterator();
        while (iterator.hasNext()) {
            String ban = (String)iterator.next();
            sb.append(ban + "\n");
        }
        chatchannelsNode.setStringValue("banlist", sb.toString());
        chatchannelsNode.commit();
    }

    public void setOperator(User user, boolean operator) {
        super.setOperator(user, operator);
    }
    
    public int moderatedChange(Date currentDate) {
        return MODERATED_MODE_UNCHANGED;
    }

    public void setTopic(String topic) {
        chatchannelsNode.setStringValue("topic", topic);
        super.setTopic(topic);
    }

    public void setTopicProtection(boolean settable) {
        super.setTopicProtection(settable);
    }

    public void setModerated(boolean moderated) {
        super.setModerated(moderated);
    }

    public void setNoOutsideMessages(boolean noOutsideMessages) {
        super.setNoOutsideMessages(noOutsideMessages);
    }

    public void setUserLimit(int maximum) {
        super.setUserLimit(maximum);
    }

    protected Node getChatchannelsNode() {
        return chatchannelsNode;
    }

    public ChannelLogger getLogger() {
        return logger;
    }
}

