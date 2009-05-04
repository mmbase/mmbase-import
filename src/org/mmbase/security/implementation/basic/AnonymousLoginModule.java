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
 * @version $Id$
 */

public class AnonymousLoginModule implements LoginModule {
    private static final Logger log = Logging.getLoggerInstance(AnonymousLoginModule.class);

    public void load(Map properties) {
        // nah do nothing..
    }

    public boolean login(NameContext user, Map loginInfo,  Object[] parameters) {
        log.debug("anonymous login..");
        // set the identifier...
        user.setIdentifier("anonymous");
        return true;
    }
}
