/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security;

import org.mmbase.util.functions.Parameters;
/**
 * A piece of 'action check' functionality. Provided by actions themselves, but security
 * implementations can perhaps also use this interface to administer their implementation of {@link
 * Authorization#check(UserContext, Action, Parameters)}.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9
 */
public interface ActionChecker extends java.io.Serializable {

    boolean check(UserContext user, Action ac, Parameters parameters);


    /**
     * The ActionChecker that always allows every action to to everybody.
     * @since MMBase-1.9.2
     */
    public static final ActionChecker ALLOWS = new ActionChecker() {
            private static final long serialVersionUID = 1L;
            public boolean check(UserContext user, Action ac, Parameters parameters) {
                return true;
            }
            @Override
            public String toString() {
                return "allows";
            }
        };

    /**
     * This basic implementation of ActionChecker checks the action only based on rank. A minimal
     * rank is to be supplied in the constructor.
     */

    public static class Rank implements  ActionChecker {
        private static final long serialVersionUID = 7047822780810829661L;
        final org.mmbase.security.Rank rank;
        public Rank(org.mmbase.security.Rank r) {
            rank = r;
        }
        public boolean check(UserContext user, Action ac, Parameters parameters) {
            return user.getRank().getInt() >= rank.getInt();
        }
        @Override
        public String toString() {
            return "at least " + rank;
        }
    }
}
