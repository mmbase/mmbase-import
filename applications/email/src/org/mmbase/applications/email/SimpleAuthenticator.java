/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.email;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import org.mmbase.util.logging.*;


/**
 * Password authentication for the email
 * @since MMBase-1.8.4
 * @version $Id$
 */
public class SimpleAuthenticator extends Authenticator {

    private final PasswordAuthentication authentication;
    private static final Logger log = Logging.getLoggerInstance(SimpleAuthenticator.class);


    /**
     * Create a password authenticator
     * @param username Username.
     * @param password Password.
     */
    public SimpleAuthenticator(String username, String password) {
        authentication = new PasswordAuthentication(username, password);
    }


    /**
     * Override of the default implementation.
     * @return Returns the email authentication principle.
     */
    protected PasswordAuthentication getPasswordAuthentication() {
        if (log.isDebugEnabled()) {
            log.debug("Authentication requested: site=" + getRequestingSite() +
                      ", port=" + getRequestingPort() + 
                      ", protocol=" + getRequestingProtocol() +
                      ", prompt=" + getRequestingPrompt() +
                      ", defaultUserName=" + getDefaultUserName());
        }
        return authentication;
    }
}