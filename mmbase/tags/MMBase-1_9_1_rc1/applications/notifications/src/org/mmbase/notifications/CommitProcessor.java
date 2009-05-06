/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.notifications;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * We could configure that the default status for a certain notification is such that it should be
 * confirmed.
 * Unused, so untested, at the moment.
 *
 * @author Michiel Meeuwissen
 * @version $Id: CommitProcessor.java,v 1.3 2007-11-12 18:00:58 michiel Exp $
 **/
public class CommitProcessor  implements org.mmbase.datatypes.processors.CommitProcessor {
    public void commit(Node node, Field field) {
        if (node.isNew() && SMSNotification.class.getName().equals(node.getStringValue(field.getName()))) {
            if (node.getIntValue("status") == 1) {
                node.setIntValue("status", 0); // people must confirm by SMS.
            }
        }
    }

}
