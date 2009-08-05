/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.eo.chat.repository.mmbase;

import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.RelationManager;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMBaseContext;
import org.mmbase.util.Escape;

import nl.eo.chat.repository.*;
import nl.eo.chat.repository.irc.*;

/**
 * This implementation of a channel uses MMBase to retrieve and store
 * information about users.
 *
 * @author Jaco de Groot
 */
public class MMBaseUserRepository extends IrcUserRepository {
    private Cloud cloud;
    private NodeManager usersNodeManager;
    private RelationManager rolerelRelationManager;
    private Node userGroupNode;
    private Node chatserversNode;
    String usersNodeManagerName;
    String usersAccountFieldName;
    String usersSessioinFieldName;
    
    public MMBaseUserRepository(Cloud cloud, NodeManager usersNodeManager,
            RelationManager rolerelRelationManager, Node userGroupNode,
            Node chatserversNode, String usersNodeManagerName,
            String usersAccountFieldName, String usersSessioinFieldName) {
        this.cloud = cloud;
        this.usersNodeManager = usersNodeManager;
        this.rolerelRelationManager = rolerelRelationManager;
        this.userGroupNode = userGroupNode;
        this.chatserversNode = chatserversNode;
        this.usersNodeManagerName = usersNodeManagerName;
        this.usersAccountFieldName = usersAccountFieldName;
        this.usersSessioinFieldName = usersSessioinFieldName;
    }

    public User getUser(Socket socket) {
        return (User)registeredSockets.get(socket);
    }

    public int register(Socket socket) {
        IrcUser user = (IrcUser)unregisteredSockets.get(socket);
        String nick = user.getNick();
        if (nick == null) {
            return UserRepository.REGISTER_NEED_NICK;
        }
        if (user.getHostname() == null) {
            return UserRepository.REGISTER_NEED_HOSTNAME;
        }
        if (user.getUsername() == null) {
            return UserRepository.REGISTER_NEED_USERNAME;
        }
        if (user.getRealname() == null) {
            return UserRepository.REGISTER_NEED_REALNAME;
        }
        String pass = user.getPass();
        if (pass == null) {
            return UserRepository.REGISTER_NEED_PASS;
        }
        Node usersNode;
        NodeList nodeList;
        nodeList = cloud.getList(userGroupNode.getStringValue("number"),
                                 userGroupNode.getNodeManager().getName() + "," + usersNodeManagerName,
                                 usersNodeManagerName + ".number",
                                 usersNodeManagerName + "."+ usersAccountFieldName + " = '" + Escape.singlequote(nick) + "' AND " + usersNodeManagerName + "." + usersSessioinFieldName + " = '" + Escape.singlequote(pass) + "'",
                                 null, null, null, false);
        if (nodeList.size() == 0) {
            return UserRepository.REGISTER_INCORRECT_PASSWORD;
        } else {
            String usersNodeNumber = nodeList.getNode(0).getStringValue(usersNodeManagerName + ".number");
            usersNode = cloud.getNode(usersNodeNumber);
        }
        MMBaseUser mmbaseUser = new MMBaseUser(user, cloud, rolerelRelationManager, chatserversNode, usersNode, usersNodeManagerName, usersAccountFieldName);
        unregisteredSockets.remove(socket);
        unregisteredNicks.remove(nick.toLowerCase());
        registeredSockets.put(socket, mmbaseUser);
        registeredNicks.put(nick.toLowerCase(), mmbaseUser);
        return UserRepository.REGISTER_OK;
    }

    public int registerAsOperator(User user, String username, String password) {
        MMBaseUser mmbaseUser = (MMBaseUser)user;
        int result = mmbaseUser.registerAsOperator(username, password);
        if (result != UserRepository.REGISTER_AS_OPERATOR_OK) {
            result = super.registerAsOperator(user, username, password);
        }
        return result;
    }

    public boolean isValidNick(String nick) {
        return true;
    }

}

