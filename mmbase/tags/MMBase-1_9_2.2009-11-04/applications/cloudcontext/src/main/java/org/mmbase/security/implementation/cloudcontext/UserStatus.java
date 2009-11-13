/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloudcontext;


/**
 * @author Michiel Meeuwissen
 * @version $Id: User.java 35335 2009-05-21 08:14:41Z michiel $
 * @see    org.mmbase.security.implementation.cloudcontext.builders.Users
 */
public enum UserStatus {

    BLOCKED(-1),
    NEW(0),
    INUSE(1);

    private final int i;
    private UserStatus(int i) {
        this.i = i;
    }
    public int getValue() {
        return i;
    }

    public static UserStatus valueOf(int i) {
        for (UserStatus us : UserStatus.values()) {
            if (us.getValue() == i) return us;
        }
        throw new IllegalArgumentException();
    }

}
