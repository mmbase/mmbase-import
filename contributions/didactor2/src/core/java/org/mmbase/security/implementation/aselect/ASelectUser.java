/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.aselect;

import org.mmbase.security.SecurityException;
import org.mmbase.security.*;

/**
 * UserContext implementation for the ASelectAuthentiction implementation. Currently this
 * implementation is about the most straight-forward user implementation possible: (A user has an
 * identifier and a rank, both specified by the constructor).
 *
 * @author Arnout Hannink     (Alfa & Ariss)
 * @author Michiel Meeuwissen (Publieke Omroep)
 *
 * @version $Id: ASelectUser.java,v 1.1 2006-01-31 19:53:06 azemskov Exp $
 * @since  MMBase-1.7
 * @see ASelectAuthentication
 */

public class ASelectUser extends UserContext {

    private String identifier;
    private Rank   rank;


    // constructor, perhaps needs more argumetns
    public ASelectUser(String name, Rank r) {
        identifier = name;
        rank       = r;
    }

    // javadoc inherited
    public String getIdentifier() {
        return identifier;
    }

    // javadoc inherited
    public String getOwnerField() {
        return getIdentifier();
    }

    public Rank getRank() throws SecurityException {
        return rank;
    }

}
