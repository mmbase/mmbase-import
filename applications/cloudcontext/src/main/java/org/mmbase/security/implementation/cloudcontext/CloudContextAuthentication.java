/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloudcontext;

import org.mmbase.security.*;
import org.mmbase.security.implementation.cloudcontext.builders.Users;


/**
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9.1
 */
public abstract class CloudContextAuthentication extends Authentication {

    public final static CloudContextAuthentication getInstance() {
        return (CloudContextAuthentication) org.mmbase.module.core.MMBase.getMMBase().getMMBaseCop().getAuthentication();
    }

    public  UserProvider getUserProvider() {
        return Users.getBuilder().getProvider();
    }

}
