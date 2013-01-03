/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloud;

import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.core.MMObjectBuilder;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Security from within MMBase. The mmbaseusers builder used to store nothing more than name/password combination. 
 *
 * @author Eduard Witteveen
 * @version $Id$
 */
public class UserBuilder extends MMObjectBuilder {
    private static final Logger log = Logging.getLoggerInstance(UserBuilder.class);

    private org.mmbase.util.Encode encoder = null;

    /**
     * {@inheritDoc}
     */
    public boolean init() {
        String encoding = (String) getInitParameters().get("encoding");
        if(encoding==null) {
            log.warn("no property 'encoding' defined in '"+getTableName()+".xml' using default encoding");
            encoder = new org.mmbase.util.Encode("MD5");
        } else {
            encoder = new org.mmbase.util.Encode(encoding);
        }
        log.info("Using " + encoder.getEncoding() + " as our encoding for password");
        return super.init();
    }



    /**
     * Checks whether the given username/password combination exists and is correct.
     * If password is null, then only the existence of the user is checked.
     */
    boolean exists(String username, String password) {
        if (log.isTraceEnabled()) {
            log.trace("username: '" + username + "' password: '" + password + "'");
        }
        java.util.Enumeration e = search("WHERE username = '"+username+"' ");
        while(e.hasMoreElements()) {
            MMObjectNode node = (MMObjectNode) e.nextElement();
            if(password == null || encode(password).equals(node.getStringValue("password"))) {
                // found it !
                log.trace("username: '"+username+"' password: '"+password+"' found in node #" + node.getNumber());
                return true;
            } else {
                log.trace("username: '"+username+"' found in node #" + node.getNumber()+" --> PASSWORDS NOT EQUAL");
            }
        }
        if (log.isTraceEnabled()) {
            log.trace("username: '"+username+"' --> USERNAME NOT CORRECT");
        }
        return false;
    }

    protected String encode(String value) {
        return encoder.encode(value);
    }
}
