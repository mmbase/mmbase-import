package nl.didactor.chat.repository;

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
import org.mmbase.util.Encode;

import nl.eo.chat.repository.*;
import nl.eo.chat.repository.irc.*;

/**
 * UserRepository that uses the Didactor objectmodel.
 * @author Johannes Verelst
 */
public class DidactorUserRepository extends IrcUserRepository {
    private Cloud cloud;

    public DidactorUserRepository(Cloud cloud) {
        this.cloud = cloud;
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
        NodeList nodeList = null;
        nodeList = cloud.getList(null, "people",
                                 "people.number",
                                 "username = '" + Encode.encode("ESCAPE_SINGLE_QUOTE", nick) + "'", 
                                 null, null, null, false);
        if (nodeList.size() == 0) {
            return UserRepository.REGISTER_INCORRECT_PASSWORD;
        } else {
            String usersNodeNumber = nodeList.getNode(0).getStringValue("people.number");
            usersNode = cloud.getNode(usersNodeNumber);
        }

        DidactorUser dUser = new DidactorUser(user, cloud, usersNode);
        unregisteredSockets.remove(socket);
        unregisteredNicks.remove(nick.toLowerCase());
        registeredSockets.put(socket, dUser);
        registeredNicks.put(nick.toLowerCase(), dUser);
        return UserRepository.REGISTER_OK;
    }

    public int registerAsOperator(User user, String username, String password) {
        DidactorUser mmbaseUser = (DidactorUser)user;
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

