/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.eo.chat.repository.mmbase;

import org.mmbase.bridge.*;

import nl.eo.chat.repository.*;
import nl.eo.chat.repository.irc.*;

/**
 * This implementation of a user uses MMBase to retrieve and store
 * information about a user.
 *
 * @author Jaco de Groot
 */
public class MMBaseUser extends IrcUser {
    Cloud cloud;
    Node chatserversNode;
    Node usersNode;
    // The rolerel relation with the chatservers node
    RelationManager rolerelRelationManager;
    String usersNodeManagerName;
    String usersAccountFieldName;

    protected MMBaseUser(IrcUser ircUser, Cloud cloud,
            RelationManager rolerelRelationManager, Node chatserversNode,
            Node usersNode, String usersNodeManagerName,
            String usersAccountFieldName) {
        this.pass = ircUser.getPass();
        this.hostname = ircUser.getHostname();
        this.nick= ircUser.getNick();
        this.username = ircUser.getUsername();
        this.realname = ircUser.getRealname();
        this.socket = ircUser.getSocket();
        this.cloud = cloud;
        this.rolerelRelationManager = rolerelRelationManager;
        this.chatserversNode = chatserversNode;
        this.usersNode = usersNode;
        this.usersNodeManagerName = usersNodeManagerName;
        this.usersAccountFieldName = usersAccountFieldName;
    }

    protected Node getUsersNode() {
        return usersNode;
    }
    
    protected int registerAsOperator(String username, String password) {
        if (username.equals(usersNode.getStringValue(usersAccountFieldName))) {
            // Use getNode to prevent getting back an old value.
            Node node;
            try {
                node = cloud.getNode(usersNode.getNumber());
            } catch(Exception e) {
                node = null;
            }
            if (node != null) {
                if (password.equals(node.getStringValue("password"))) {
                    Node rolerelRelation = getRolerelRelation(chatserversNode);
                    if (rolerelRelation != null) {
                        String role = rolerelRelation.getStringValue("role");
                        if (role != null || role.indexOf('o') != -1) {
                            return UserRepository.REGISTER_AS_OPERATOR_OK;
                        }
                    }
                }
            }
        }
        return UserRepository.REGISTER_AS_OPERATOR_INCORRECT_PASSWORD;
    }

    /**
     * Returns the rolrel relation between this user and the specified node.
     *
     * <p><b>Note: The relation is returned as a node (see comment in source
     * code for the reason behind this).</b></p>
     *
     * @return  the rolrel relation between this user and the specified node or
     *          null if no relation exists.
     */
    protected Node getRolerelRelation(Node node) {
        Node rolerelRelation = null;
        NodeList nodeList;
        nodeList = cloud.getList(usersNode.getStringValue("number"),
                                 usersNodeManagerName + ",rolerel," + node.getNodeManager().getName(),
                                 "rolerel.number",
                                 node.getNodeManager().getName() + ".number = " + node.getStringValue("number"),
                                 null, null, null, false);
        if (nodeList.size() > 0) {
            // If we want to be RMMCI compatible (no casting), we can't do:
            //
            // rolerelRelation = (Relation)cloud.getNode(nodeList.getNode(0).getStringValue("rolerel.number"));
            //
            // If we want to be MMBase 1.5.1 compatible we can't do:
            //
            // rolerelRelation = (Relation)cloud.getRelation(nodeList.getNode(0).getStringValue("rolerel.number"));
            //
            // The following code is a workaround for this:
            //
            // Node rolerelNode = cloud.getNode(nodeList.getNode(0).getStringValue("rolerel.number"));
            // RelationList relationList = node.getRelations();
            // RelationIterator relationIterator = relationList.relationIterator();
            // while (relationIterator.hasNext()) {
            //     Relation relation = relationIterator.nextRelation();
            //     if (relation.getNumber() == rolerelNode.getNumber()) {
            //         rolerelRelation = relation;
            //     }
            // }
            //
            // Because of a bug in MMBase 1.5.1 this workaround returns a
            // Relation object that uses an InsRel internally wich causes
            // rolerelRelation.getStringValue("role") to allways return an
            // empty string.
            //
            // Bottomline, we just return this relation as a Node, wich seems
            // to work fine.
            rolerelRelation = cloud.getNode(nodeList.getNode(0).getStringValue("rolerel.number"));
        }
        return rolerelRelation;
    }
    
    /**
     * Creates a rolrel relation between this user and the specified node.
     *
     * @return  the created relation.
     */
    protected Relation createRolerelRelation(Node node) {
        Relation relation = rolerelRelationManager.createRelation(usersNode, node);
        relation.commit();
        return relation;
    }

    public boolean isRestricted() {
        return true;
    }

}
