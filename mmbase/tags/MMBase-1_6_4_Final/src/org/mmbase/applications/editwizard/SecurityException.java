/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard;

/**
 * EditWizard
 * @javadoc
 * @author Kars Veling
 * @since   MMBase-1.6
 * @version $Id: SecurityException.java,v 1.2 2002-02-25 11:53:58 pierre Exp $
 */
public class SecurityException extends Exception {

    public SecurityException(String message) {
        super(message);
    }
}
