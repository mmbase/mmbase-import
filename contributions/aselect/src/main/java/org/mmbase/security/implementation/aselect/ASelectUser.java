/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.aselect;

import org.mmbase.security.SecurityException;
import org.mmbase.security.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * UserContext implementation for the ASelectAuthentiction implementation. Currently this
 * implementation is about the most straight-forward user implementation possible: (A user has an
 * identifier and a rank, both specified by the constructor).
 *
 * @author Arnout Hannink     (Alfa & Ariss)
 * @author Michiel Meeuwissen (Publieke Omroep)
 *
 * @version $Id$
 * @since  MMBase-1.7
 * @see ASelectAuthentication
 */

public class ASelectUser extends BasicUser {
    private static final Logger log = Logging.getLoggerInstance(ASelectUser.class);

    private static final long serialVersionUID = 1;
    private Rank   rank;
    long key;



    // constructor, perhaps needs more argumetns
    public ASelectUser(Authentication a, String name, Rank r, long uniqueNumber, String app) {
        super(a, app, name);
        if (log.isDebugEnabled()) {
            log.debug("Instantiating " + name);
        }
        rank       = r;
        key = uniqueNumber;
    }


    // javadoc inherited
    public String getOwnerField() {
        return getIdentifier();
    }

    public Rank getRank() throws SecurityException {
        return rank;
    }

    public boolean equals(Object o) {
        if (o instanceof ASelectUser) {
            ASelectUser ou = (ASelectUser) o;
            return super.equals(o) && key == ou.key;
        } else {
            return false;
        }
    }

}
