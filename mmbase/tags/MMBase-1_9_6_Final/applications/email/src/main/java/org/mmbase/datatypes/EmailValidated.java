/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
*/
package org.mmbase.datatypes;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/*
 * @author Michiel Meeuwissen
 * @version $Id: VerifyEmailProcessor.java 41821 2010-04-09 14:02:48Z michiel $

 */

public class EmailValidated extends org.mmbase.applications.email.EmailEvent {
    private static final Logger log = Logging.getLoggerInstance(EmailValidated.class);
    private static final long serialVersionUID = 1L;

    private final int nodeNumber;

    public EmailValidated(int nd) {
        this.nodeNumber = nd;
    }

    public int getNode() {
        return nodeNumber;
    }


}
