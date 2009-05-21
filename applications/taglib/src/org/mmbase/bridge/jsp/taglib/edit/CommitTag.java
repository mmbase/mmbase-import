/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.edit;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.jsp.taglib.CloudReferrerTag;
import org.mmbase.bridge.Transaction;

import org.mmbase.util.logging.*;

/**
 * This tag can be used inside a transaction tag, to commit it. It also
 * serves as a baseclass for e.g. CancelTag
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class CommitTag extends CloudReferrerTag {
    // perhaps it would be nicer to extend CloudReferrer to TransactionReferrer first.

    private static final Logger log = Logging.getLoggerInstance(CommitTag.class);

    private Attribute transaction = Attribute.NULL;
    public void setTransaction(String t) throws JspTagException {
        transaction = getAttribute(t);
    }

    protected void doAction(Transaction t) {
        log.debug("Committing " + t);
        t.commit();
    }

    /**
     * @since MMBase-1.8.5
     */
    protected Transaction getTransactionVar() throws JspTagException {
        // find the parent transaction:
        TransactionTag tt = findParentTag(TransactionTag.class, transaction.getString(this), false);
        if (tt != null) return (Transaction) tt.getCloudVar();
        Transaction t = (Transaction) pageContext.getAttribute(TransactionTag.KEY, TransactionTag.SCOPE);
        if (t != null) return t;
        throw new JspTagException("Could not find parent transaction provider");
    }


    public int doStartTag() throws JspTagException{
        Transaction trans = getTransactionVar();
        doAction(trans);
        return SKIP_BODY;
    }
}
