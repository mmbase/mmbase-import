/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloudcontext;

import org.mmbase.security.*;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.core.MMObjectBuilder;

/**
 * The implemention of 'users' is pluggable, and should be returned by {@link
 * Authenticate#getUserProvider}. Implementation defines what is a user, and how some of the
 * esential properties of them are acquired.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * MMBase-1.9.1
 */
public interface UserProvider {

    /**
     * Returns the node representing the anonymous node.
     * @todo Can this be <code>null</code>?
     */
    MMObjectNode getAnonymousUser();

    /**
     * Returns the node associated with a certain username/password combination,
     * or <code>null</code>
     * @param encode Whether or not the user is requested usering the username/encodedpassword login
     * 'type' see {@link Authenticate#login}.
     */
    MMObjectNode getUser(String user, String pw, boolean encoded);

    /**
     * Returns the node associated with a certain username
     * or <code>null</code>
     */
    MMObjectNode getUser(String user);


    /**
     * Returns the node associated with a certain username/rank combination
     * or <code>null</code>
     */
    MMObjectNode getUserByRank(String userName, String rank);

    String getDefaultContext(MMObjectNode user);

    Rank getRank(MMObjectNode userNode);

    boolean isValid(MMObjectNode userNode);

    MMObjectBuilder getUserBuilder();

    String encode(String password);

    /**
     * Returns whether the given node is an 'own' node. It should return true if the node is representing the mmbaseusers object which represents the current user.
     * Extensions could e.g. also implement returning true for the associated people node.
     */
    boolean isOwnNode(User user, MMObjectNode node);



}
