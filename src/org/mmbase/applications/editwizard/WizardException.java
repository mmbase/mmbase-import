/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard;

/**
 *
 * EditWizard
 * @javadoc
 * @author Kars Veling
 * @since   MMBase-1.6
 * @version $Id: WizardException.java,v 1.2 2002-02-25 11:53:58 pierre Exp $
 */
public class WizardException extends java.lang.Exception {
    /**
     * Creates new <code>WizardException</code> without detail message.
     */
    public WizardException() {
    }


    /**
     * Constructs an <code>WizardException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public WizardException(String msg) {
        super(msg);
    }
}


