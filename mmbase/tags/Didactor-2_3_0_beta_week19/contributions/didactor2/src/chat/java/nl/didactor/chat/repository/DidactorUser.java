package nl.didactor.chat.repository;

import org.mmbase.bridge.*;

import nl.eo.chat.repository.*;
import nl.eo.chat.repository.irc.*;

/**
 * Class representing a Didactor user in the chat.
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class DidactorUser extends IrcUser {
    Cloud cloud;
    Node usersNode;
    DidactorPrivateLogger logger;

    protected DidactorUser (IrcUser ircUser, Cloud cloud, Node usersNode) {
        this.pass = ircUser.getPass();
        this.hostname = ircUser.getHostname();
        this.nick= ircUser.getNick();
        this.username = ircUser.getUsername();
        this.realname = ircUser.getRealname();
        this.socket = ircUser.getSocket();
        this.cloud = cloud;
        this.usersNode = usersNode;
    }

    protected Node getUsersNode() {
        return usersNode;
    }
    
    /**
     * This method is called to register somebody as an operator
     * within the chat. This will only succeed if the user is
     * a teacher; without checking the password.
     */
    protected int registerAsOperator(String username, String password) {
        if (username.equals(usersNode.getStringValue("username"))) {
            // Use getNode to prevent getting back an old value.
            Node node;
            try {
                node = cloud.getNode(usersNode.getNumber());
            } catch(Exception e) {
                node = null;
            }
            if (node != null) {
                if (node.getNodeManager().getName().equals("teachers")) {
                    return UserRepository.REGISTER_AS_OPERATOR_OK;
                }
            }
        }
        return UserRepository.REGISTER_AS_OPERATOR_INCORRECT_PASSWORD;
    }

    public boolean isRestricted() {
        return true;
    }

    public PrivateLogger getPrivateLogger() {
	synchronized (this) {
	    if (this.logger == null) {
		this.logger = new DidactorPrivateLogger(this);
	    }
	}
	return this.logger;
    }

}
