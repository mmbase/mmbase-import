/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.context;

import org.mmbase.security.Rank;
import java.util.Map;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * ClassLogin, authentication based on 'class', using &lt;security&gt;/classauthentication.xml or ClassAuthenticationWrapper.
 *
 * @author Michiel Meeuwissen
 * @version $Id: ClassLogin.java,v 1.9 2008-10-01 16:57:34 michiel Exp $
 * @since MMBase-1.8
 */

public class ClassLogin extends ContextLoginModule {
    private static final Logger log = Logging.getLoggerInstance(ClassLogin.class);

    public ContextUserContext login(Map<String, ?> userLoginInfo, Object[] userParameters) throws org.mmbase.security.SecurityException {

        org.mmbase.security.classsecurity.ClassAuthentication.Login li = org.mmbase.security.classsecurity.ClassAuthentication.classCheck("class", userLoginInfo);
        if (li == null) {
            throw new SecurityException("Class authentication failed  '" + userLoginInfo + "' (class not authorized)");
        }
        // get username
        String userName = li.getMap().get("username");
        String reqRank  = li.getMap().get("rank");
        if(userName == null && reqRank == null) throw new org.mmbase.security.SecurityException("expected the property 'username' and/or 'rank' with login");

        if ("anonymous".equals(reqRank) && userName == null) {
            return getValidUserContext("anonymous", Rank.ANONYMOUS);
        }

        org.w3c.dom.Element node = getAccount(userName, null, reqRank);
        if(node == null) {
            log.info("No user with name '" + userName + "' and rank '" + reqRank + "'");
            return null;
        }
        userName = node.getAttribute("name");

        Rank rank= getRank(userName, null);
        if(rank == null) {
            log.warn( "expected a rank for user with name '" + userName + "', canceling a valid login due to the fact that the rank attribute was not set");
            return null;

        }
        return getValidUserContext(userName, rank);
    }
}
