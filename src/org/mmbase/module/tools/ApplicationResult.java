/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.tools;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * Contains the status after installing an MMBase 'application'. I.e. whether is was successful, and
 * a newline separated message String containing the reason(s).
 * @version $Id: ApplicationResult.java,v 1.2 2006-12-05 21:14:24 michiel Exp $
 */
class ApplicationResult {

    private static final Logger log = Logging.getLoggerInstance(ApplicationResult.class);

    protected final StringBuffer resultMessage;
    protected boolean success;

    ApplicationResult() {
        resultMessage = new StringBuffer();
        success = true;
    }

    String getMessage() {
        return resultMessage.toString();
    }

    boolean isSuccess() {
        return success;
    }

    private void addMessage(String message) {
        if (resultMessage.length() > 0) {
            resultMessage.append('\n');
        }
        resultMessage.append(message);
    }

    /**
     * Adds a message and logs it as an error, and sets the success status to false.
     */
    boolean error(String message) {
        success = false;
        log.error(message);
        addMessage(message);
        return false;
    }

    /**
     * Adds a message and logs it as an warning, and sets the success status to false.
     */
    boolean warn(String message) {
        success = false;
        log.warn(message);
        addMessage(message);
        return false;
    }

    /**
     * Adds a message  and sets the success status to true.
     */
    boolean success(String message) {
        success = true;
        addMessage(message);
        return true;
    }

}
