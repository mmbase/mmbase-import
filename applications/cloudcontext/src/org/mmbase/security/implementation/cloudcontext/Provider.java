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
 *
 * @author Michiel Meeuwissen
 * @version $Id: Provider.java,v 1.1.2.1 2008-12-08 16:30:55 michiel Exp $
 * MMBase-1.8.7
 */
public interface Provider {



    boolean check();

    MMObjectNode getAnonymousUser();

    MMObjectNode getUser(String user, String pw, boolean encoded);

    MMObjectNode getUser(String user);

    MMObjectNode getUserByRank(String userName, String rank);

    Rank getRank(MMObjectNode userNode);

    boolean isValid(MMObjectNode userNode);

    String encode(String pw);

    boolean allowEncodedPassword();

    MMObjectBuilder getUserBuilder();

    String getDefaultContext(MMObjectNode user);

}
