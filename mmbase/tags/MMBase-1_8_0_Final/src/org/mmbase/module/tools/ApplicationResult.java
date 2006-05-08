/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.tools;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

class ApplicationResult {

    /** MMbase logging system */
    private static Logger log = Logging.getLoggerInstance(ApplicationResult.class.getName());
    
    protected StringBuffer resultMessage;
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

    boolean error(String message) {
        success = false;
        log.error(message);
        addMessage(message);
        return false;
    }

    boolean warn(String message) {
        success = false;
        log.warn(message);
        addMessage(message);
        return false;
    }

    boolean success(String message) {
        success = true;
        addMessage(message);
        return true;
    }

}
