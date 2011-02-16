/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.edit;


import org.mmbase.bridge.Transaction;

import org.mmbase.util.logging.*;

/**
 * This tag can be used inside a transaction tag, to commit it. It also
 * serves as a base class for e.g. CancelTag
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class CommitTag extends TransactionReferrerTag {
    private static final Logger log = Logging.getLoggerInstance(CommitTag.class);

    @Override
    protected void doAction(Transaction t) {
        log.debug("Committing " + t);
        t.commit();
    }
}
