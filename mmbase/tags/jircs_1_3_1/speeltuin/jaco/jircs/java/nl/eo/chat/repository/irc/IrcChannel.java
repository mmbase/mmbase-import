/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.eo.chat.repository.irc;

import java.net.Socket;
import java.util.Date;
import java.util.Iterator;
import java.util.Collection;
import java.util.Vector;

import nl.eo.chat.repository.*;

/**
 * Basic IRC implementation of a channel.
 *
 * @author Jaco de Groot
 */
public class IrcChannel implements Channel {
    protected String name;
    protected String topic;
    protected Vector users = new Vector();
    protected Vector bots = new Vector();
    protected Vector bans = new Vector();
    protected Vector operators = new Vector();
    protected boolean topicProtection = false;
    protected boolean moderated = false;
    protected boolean noOutsideMessages = false;
    protected boolean deleteWhenLastUserLeaves = false;
    protected int userLimit = -1;

    protected IrcChannel(String name) {
        this.name = name;
    }

    public Vector getUsers() {
        return users;
    }

    public void addUser(User user) {
        users.add(user);
        IrcUser ircUser = (IrcUser)user;
        ircUser.addChannel(this);
    }

    /**
     * Currently only bans on nicks are handled.
     */
    public void addBan(String ban) {
        bans.add(ban);
    }

    public void removeBan(String ban) {
        bans.remove(ban);
    }

    public boolean containsBan(String ban) {
        return bans.contains(ban);
    }

    public boolean isBanned(User user) {
        String nick = user.getNick();
        String hostname = user.getHostname();
        Iterator iterator = bans.iterator();
        while (iterator.hasNext()) {
            String ban = (String)iterator.next();
            if ((ban.startsWith("*!") && ban.endsWith("@" + hostname))
                    || (ban.startsWith(nick + "!"))) {
                return true;
            }
        }
        return false;
    }

    public Collection getBans() {
        return bans;
    }

    public void setOperator(User user, boolean operator) {
        if (operator) {
            operators.add(user);
        } else {
            operators.remove(user);
        }
    }

    public boolean isOperator(User user) {
        if (operators.contains(user)) {
            return true;
        } else {
            return false;
        }
    }
    
    public void removeUser(User user) {
        users.remove(user);
        operators.remove(user);
        IrcUser ircUser = (IrcUser)user;
        ircUser.removeChannel(this);
    }
    
    public String getName() {
        return name;
    }
    
    public boolean containsUser(User user) {
        return users.contains(user);
    }

    public int getNumberOfUsers() {
        return users.size();
    }

    public int getNumberOfVisibleUsers() {
        return users.size();
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
    
    public String getTopic() {
        return topic;
    }
    
    public boolean isModerated() {
        return moderated;
    }

    public boolean noOutsideMessages() {
        return noOutsideMessages;
    }
    
    public void setNoOutsideMessages(boolean noOutsideMessages) {
        this.noOutsideMessages = noOutsideMessages;
    }

    public void setModerated(boolean moderated) {
        this.moderated = moderated;
    }
    
    public int moderatedChange(Date date) {
        return MODERATED_MODE_UNCHANGED;
    }
    
    public boolean topicProtection() {
        return topicProtection;
    }

    public void setTopicProtection(boolean protect) {
        topicProtection = protect;
    }
    
    public int getUserLimit() {
        return userLimit;
    }
    
    public void setUserLimit(int maximum) {
        userLimit = maximum;
    }

    public boolean deleteWhenLastUserLeaves() {
        return deleteWhenLastUserLeaves;
    }

    public void setDeleteWhenLastUserLeaves(boolean delete) {
        deleteWhenLastUserLeaves = delete;
    }

}

