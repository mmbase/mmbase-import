/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.basic;

import java.util.Map;

import org.mmbase.security.Rank;

/**
 * Support for authentication method 'class' for 'basic' authentication.
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.8
 */
public class ClassLoginModule implements LoginModule {

    private Map ranks;

    public void load(Map properties) {
        ranks = properties;
    }

    public boolean login(NameContext user, Map loginInfo,  Object[] parameters) {
        org.mmbase.security.classsecurity.ClassAuthentication.Login li = org.mmbase.security.classsecurity.ClassAuthentication.classCheck("class");
        if (li == null) {
            throw new SecurityException("Class authentication failed (class not authorized)");
        }
        String userName = (String) li.getMap().get("username");

        String r = (String) ranks.get(userName);
        Rank rank;
        if (r == null) {
            rank = Rank.BASICUSER;
        } else {
            rank = Rank.getRank(r);
        }

        user.setIdentifier(userName);
        user.setRank(rank);
        return true;
    }
}
