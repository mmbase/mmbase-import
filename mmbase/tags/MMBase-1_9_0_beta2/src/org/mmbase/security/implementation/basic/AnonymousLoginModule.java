/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.security.implementation.basic;

import java.util.Map;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Class AnonymousLoginModule
 * @javadoc
 * @author Eduard Witteveen
 * @version $Id: AnonymousLoginModule.java,v 1.8 2008-01-10 14:12:24 michiel Exp $
 */

public class AnonymousLoginModule implements LoginModule {
    private static final Logger log = Logging.getLoggerInstance(AnonymousLoginModule.class);

    public void load(Map<String, Object> properties) {
        // nah do nothing..
    }

    public boolean login(NameContext user, Map<String, Object> loginInfo,  Object[] parameters) {
        log.debug("anonymous login..");
        // set the identifier...
        user.setIdentifier("anonymous");
        return true;
    }
}
