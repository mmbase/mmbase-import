/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.basic;

import java.util.Map;
import java.util.HashMap;

import org.mmbase.security.Rank;

/**
 * Support for authentication method 'class' for 'basic' authentication.
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.8
 */
public class ClassLoginModule implements LoginModule {

    private Map<String, String> ranks = new HashMap<String, String>();

    public void load(Map<String, Object> properties) {
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            if (entry.getValue() instanceof String) {
                ranks.put(entry.getKey(), (String) entry.getValue());
            }
        }
    }

    public boolean login(NameContext user, Map<String, ?> loginInfo,  Object[] parameters) {
        org.mmbase.security.classsecurity.ClassAuthentication.Login li = org.mmbase.security.classsecurity.ClassAuthentication.classCheck("class", loginInfo);
        if (li == null) {
            throw new SecurityException("Class authentication failed (class not authorized)");
        }
        String userName = li.getMap().get("username"); // specified
        if (userName == null) userName = (String) loginInfo.get("username");

        String r = li.getMap().get("rank");
        if (r == null) r = (String) loginInfo.get("rank");
        if (r == null) r = ranks.get(userName);
        Rank rank = r == null ? Rank.BASICUSER : Rank.getRank(r);
        user.setIdentifier(userName);
        user.setRank(rank);
        return true;
    }
}
