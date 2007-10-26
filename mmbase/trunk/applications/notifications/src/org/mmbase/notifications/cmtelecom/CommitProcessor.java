/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.notifications.cmtelecom;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**

 *
 * @author Michiel Meeuwissen
 * @version $Id: CommitProcessor.java,v 1.1 2007-10-26 13:19:00 michiel Exp $
 **/
public class CommitProcessor  implements org.mmbase.datatypes.processors.CommitProcessor {
    public void commit(Node node, Field field) {
        if (node.isNew() && Mobile2YouNotification.class.getName().equals(node.getStringValue(field.getName()))) {
            if (node.getIntValue("status") == 1) {
                node.setIntValue("status", 0); // people must confirm by SMS.
            }
        }
    }

}
