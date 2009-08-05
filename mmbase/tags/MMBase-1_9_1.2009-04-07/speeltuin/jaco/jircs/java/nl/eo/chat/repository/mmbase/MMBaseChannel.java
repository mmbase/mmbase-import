/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.eo.chat.repository.mmbase;

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
 * information about a channel.
 *
 * @author Jaco de Groot
 */
public class MMBaseChannel extends IrcChannel {
    Cloud cloud;
    RelationManager rolerelRelationManager;
    Node chatserversNode;
    Node chatchannelsNode;
    int lastOn = -1;
    
    protected MMBaseChannel(String name, Cloud cloud,
            RelationManager rolerelRelationManager, Node chatserversNode,
            Node chatchannelsNode) {
        super(name);
        this.cloud = cloud;
        this.rolerelRelationManager = rolerelRelationManager;
        this.chatserversNode = chatserversNode;
        this.chatchannelsNode = chatchannelsNode;
        StringTokenizer st = new StringTokenizer(chatchannelsNode.getStringValue("banlist"));
        while (st.hasMoreTokens()) {
            String tok = st.nextToken();
            bans.add(tok);
        }
        String t = chatchannelsNode.getStringValue("topic");
        if (t != null && !t.equals("")) {
            topic = t;
        }
        if (chatchannelsNode.getIntValue("topicprotection") == 1) {
            super.setTopicProtection(true);
        }
        if (chatchannelsNode.getIntValue("moderated") == 1) {
            super.setModerated(true);
        }
        if (chatchannelsNode.getIntValue("nooutsidemessages") == 1) {
            super.setNoOutsideMessages(true);
        }
        super.setUserLimit(chatchannelsNode.getIntValue("userlimit"));
    }

    protected void updateChannel(Node chatchannelsNode) {
        this.chatchannelsNode = chatchannelsNode;
        System.err.println("Updating channel "+getName()+" (number="+chatchannelsNode.getNumber());
        bans = new Vector();
        StringTokenizer st = new StringTokenizer(chatchannelsNode.getStringValue("banlist"));
        while (st.hasMoreTokens()) {
            String tok = st.nextToken();
            bans.add(tok);
        }
        String t = chatchannelsNode.getStringValue("topic");
        if (t != null && !t.equals("")) {
            topic = t;
        }
        else {
            topic = null;
        }
        super.setTopicProtection(chatchannelsNode.getIntValue("topicprotection") == 1);
        super.setModerated(chatchannelsNode.getIntValue("moderated") == 1);
        super.setNoOutsideMessages(chatchannelsNode.getIntValue("nooutsidemessages") == 1);
        super.setUserLimit(chatchannelsNode.getIntValue("userlimit"));
    }
    
    public void addUser(User user) {
        MMBaseUser mmbaseUser = (MMBaseUser)user;
        Node rolerelRelation = mmbaseUser.getRolerelRelation(chatchannelsNode);
        if (rolerelRelation != null) {
            String role = rolerelRelation.getStringValue("role");
            if (role != null && role.indexOf('o') != -1) {
                operators.add(user);
            }
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
        MMBaseUser mmbaseUser = (MMBaseUser)user;
        Node rolerelRelation = mmbaseUser.getRolerelRelation(chatchannelsNode);
        if (operator) {
            if (rolerelRelation == null) {
                rolerelRelation = mmbaseUser.createRolerelRelation(chatchannelsNode);
                rolerelRelation.setStringValue("role", "o");
                rolerelRelation.commit();
            } else {
                String role = rolerelRelation.getStringValue("role");
                if (role.indexOf('o') == -1) {
                    role = role + "o";
                    rolerelRelation.setStringValue("role", role);
                    rolerelRelation.commit();
                }
            }
        } else {
            if (rolerelRelation != null) {
                String role = rolerelRelation.getStringValue("role");
                if ("o".equals(role)) {
                    rolerelRelation.delete();
                } else {
                    int i = role.indexOf('o');
                    if (i != -1) {
                        if (i < role.length() - 1) {
                            role = role.substring(0, i) + role.substring(i + 1);
                        } else {
                            role = role.substring(0, i);
                        }
                        rolerelRelation.setStringValue("role", role);
                        rolerelRelation.commit();
                    }
                }
            }
        }
        super.setOperator(user, operator);
    }
    
    public int moderatedChange(Date currentDate) {
        Node node = cloud.getNode(chatchannelsNode.getNumber());
        WorkingHours workingHours = new WorkingHours(node.getStringValue("workinghours"));
        int currentOn = -1;
        if (workingHours.open(currentDate) == 0) {
            currentOn = 1;
        } else if (workingHours.close(currentDate) == 0) {
            currentOn = 0;
        }
        if (currentOn == -1) {
            return MODERATED_MODE_UNCHANGED;
        }
        if (lastOn == -1) {
            lastOn = currentOn;
            if (currentOn == 0) {
                return MODERATED_MODE_ON;
            } else {
                return MODERATED_MODE_OFF;
            }
        } else {
            if (lastOn != currentOn) {
                lastOn = currentOn;
                if (currentOn == 0) {
                    return MODERATED_MODE_ON;
                } else {
                    return MODERATED_MODE_OFF;
                }
            } else {
                return MODERATED_MODE_UNCHANGED;
            }
        }
    }

    public void setTopic(String topic) {
        if (topic == null) {
            chatchannelsNode.setStringValue("topic", "");
        } else {
            chatchannelsNode.setStringValue("topic", topic);
        }
        chatchannelsNode.commit();
        super.setTopic(topic);
    }

    public void setTopicProtection(boolean settable) {
        if (settable) {
            chatchannelsNode.setIntValue("topicprotection", 1);
        } else {
            chatchannelsNode.setIntValue("topicprotection", 0);
        }
        chatchannelsNode.commit();
        super.setTopicProtection(settable);
    }

    public void setModerated(boolean moderated) {
        if (moderated) {
            chatchannelsNode.setIntValue("moderated", 1);
        } else {
            chatchannelsNode.setIntValue("moderated", 0);
        }
        chatchannelsNode.commit();
        super.setModerated(moderated);
    }

    public void setNoOutsideMessages(boolean noOutsideMessages) {
        if (noOutsideMessages) {
            chatchannelsNode.setIntValue("nooutsidemessages", 1);
        } else {
            chatchannelsNode.setIntValue("nooutsidemessages", 0);
        }
        chatchannelsNode.commit();
        super.setNoOutsideMessages(noOutsideMessages);
    }

    public void setUserLimit(int maximum) {
        chatchannelsNode.setIntValue("userlimit", maximum);
        chatchannelsNode.commit();
        super.setUserLimit(maximum);
    }

    protected Node getChatchannelsNode() {
        return chatchannelsNode;
    }

}

