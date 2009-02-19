/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.tools;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class ApplicationResult {

    /** MMbase logging system */
    private static Logger log = Logging.getLoggerInstance(ApplicationResult.class.getName());
    
    protected StringBuffer resultMessage;
    protected boolean success;

    public ApplicationResult() {
        resultMessage = new StringBuffer();
        success = true;
    }

    public String getMessage() {
        return resultMessage.toString();
    }

    public boolean isSuccess() {
        return success;
    }

    private void addMessage(String message) {
        if (resultMessage.length() > 0) {
            resultMessage.append('\n');
        }
        resultMessage.append(message);
    }

    public boolean error(String message) {
        success = false;
        log.error(message);
        addMessage(message);
        return false;
    }

    public boolean warn(String message) {
        success = false;
        log.warn(message);
        addMessage(message);
        return false;
    }

    public boolean success(String message) {
        success = true;
        addMessage(message);
        return true;
    }

}
