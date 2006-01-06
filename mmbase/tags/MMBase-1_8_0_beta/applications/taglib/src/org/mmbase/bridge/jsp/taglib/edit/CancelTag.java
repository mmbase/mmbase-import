/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.edit;

import org.mmbase.bridge.Transaction;

/**
 * This tag can be used inside a transaction tag, to cancel it.
 *
 * @author Michiel Meeuwissen 
 * @version $Id: CancelTag.java,v 1.5 2003-06-06 10:03:19 pierre Exp $
 */

public class CancelTag extends CommitTag {

    protected void doAction(Transaction t) {
        t.cancel();
    }
}
