/*
 * WizardException.java
 *
 * Created on 27 november 2001, 14:16
 */

package org.mmbase.applications.editwizard;

/**
 *
 * @author  Administrator
 * @version
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


