/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard;

/**
 * This exception is thrown when an error occurs in the EditWizards
 *
 * @author Kars Veling
 * @since   MMBase-1.6
 * @version $Id: WizardException.java,v 1.3 2003-08-29 09:34:39 pierre Exp $
 */
public class WizardException extends java.lang.Exception {

    //javadoc is inherited
    public WizardException() {
        super();
    }

    //javadoc is inherited
    public WizardException(String message) {
        super(message);
    }

    //javadoc is inherited
    public WizardException(Throwable cause) {
        super(cause);
    }

    //javadoc is inherited
    public WizardException(String message, Throwable cause) {
        super(message, cause);
    }
}


