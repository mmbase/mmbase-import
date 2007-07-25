/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security;

/**
 * @author Michiel Meeuwissen
 * @version $Id: ActionChecker.java,v 1.1 2007-07-25 06:47:11 michiel Exp $
 * @since MMBase-1.9
 */
public interface ActionChecker extends java.io.Serializable {

    boolean check(UserContext user, Action ac);

    /**
     * This basic implementation of ActionChecker checks the action only based on rank. A minimal
     * rank is to be supplied in the constructor.
     */

    public static class Rank implements  ActionChecker {
        final org.mmbase.security.Rank rank;
        public Rank(org.mmbase.security.Rank r) {
            rank = r;
        }
        public boolean check(UserContext user, Action ac) {
            return user.getRank().getInt() >= rank.getInt();
        }
    }
}
